package CardRecommendService.card;

import CardRecommendService.card.cardEntity.Card;
import CardRecommendService.card.cardResponse.CardDetailResponse;
import CardRecommendService.card.cardResponse.CardResponse;
import CardRecommendService.cardHistory.CardHistoryQueryRepository;
import CardRecommendService.memberCard.MemberCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final MemberCardRepository memberCardRepository;
    private final CardHistoryQueryRepository queryRepository;
    private final QCardRepository qCardRepository;

    public CardService(CardRepository cardRepository,
                       MemberCardRepository memberCardRepository,
                       CardHistoryQueryRepository queryRepository,
                       QCardRepository qCardRepository) {
        this.cardRepository = cardRepository;
        this.memberCardRepository = memberCardRepository;
        this.queryRepository = queryRepository;
        this.qCardRepository = qCardRepository;
    }

    @Transactional
    public List<CardResponse> getAllCards() {
        return cardRepository.findAll().stream()
                .map(card -> new CardResponse(card.getCardCorp(), card.getCardName(), card.getAnnualFee()))
                .collect(Collectors.toList());
    }

    // 회원 보유 카드 기반 추천 – 동적 쿼리로 기본 top 카테고리 추출
    public List<CardDetailResponse> getRecommendedCardsInfo(String uuid, List<Long> selectedCardIds,
                                                            int minAnnualFee, int maxAnnualFee, int monthOffset) {
        // 사용자가 소유한 카드 목록(연관된 Card의 id)을 검증
        List<Long> userMemberCardIds = memberCardRepository.findByUuid(uuid)
                .stream()
                .filter(memberCard -> selectedCardIds.contains(memberCard.getId()))
                .map(memberCard -> memberCard.getId())
                .toList();

        if (userMemberCardIds.isEmpty()) {
            throw new IllegalArgumentException("사용자의 카드 정보가 없습니다.");
        }
        Set<Category> categories = queryRepository.getTop5CategoriesList(userMemberCardIds, monthOffset);
        return getRecommendedCardsInfoInternal(userMemberCardIds, categories, minAnnualFee, maxAnnualFee);
    }

    // 회원 보유 카드 + 외부 제공 카테고리 기반 추천 (동적 쿼리 포함)
    public List<CardDetailResponse> getRecommendedCardsInfo(String uuid, List<Long> selectedCardIds,
                                                            List<Category> providedCategories,
                                                            int minAnnualFee, int maxAnnualFee, int monthOffset) {
        List<Long> userMemberCardIds = memberCardRepository.findByUuid(uuid)
                .stream()
                .filter(memberCard -> selectedCardIds.contains(memberCard.getId()))
                .map(memberCard -> memberCard.getId())
                .toList();

        if (userMemberCardIds.isEmpty()) {
            throw new IllegalArgumentException("사용자의 카드 정보가 없습니다.");
        }
        Set<Category> categories = (providedCategories == null || providedCategories.isEmpty())
                ? queryRepository.getTop5CategoriesList(userMemberCardIds, monthOffset)
                : new HashSet<>(providedCategories);
        return getRecommendedCardsInfoInternal(userMemberCardIds, categories, minAnnualFee, maxAnnualFee);
    }

    private List<CardDetailResponse> getRecommendedCardsInfoInternal(List<Long> validCardIds,
                                                                     Set<Category> categories,
                                                                     int minAnnualFee, int maxAnnualFee) {
        Map<Long, Integer> cardMatchCounts = getCardMatchCounts(categories, minAnnualFee, maxAnnualFee);
        List<Long> topCardIds = cardMatchCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<Card> topCards = cardRepository.findAllById(topCardIds);
        return topCards.stream()
                .map(this::mapToCardDetailResponse)
                .collect(Collectors.toList());
    }

    //카드의 cardCategories 리스트를 순회하여, 일치하는 카테고리 개수를 세도록 변경
    private Map<Long, Integer> getCardMatchCounts(Set<Category> categories, int minAnnualFee, int maxAnnualFee) {
        List<Card> cards = qCardRepository.findCardsMatchingTopCategoriesAndAnnualFee(categories, minAnnualFee, maxAnnualFee);
        return cards.stream()
                .collect(Collectors.toMap(Card::getId,
                        card -> (int) card.getCardCategories().stream()
                                .filter(cc -> categories.contains(cc.getCategory()))
                                .count()
                ));
    }

    //응답 객체에 카드의 카테고리 리스트와 할인 리스트를 전달
    private CardDetailResponse mapToCardDetailResponse(Card card) {
        return new CardDetailResponse(
                card.getCardName(),
                card.getCardCorp(),
                card.getImgUrl(),
                card.getAnnualFee(),
                card.getCardCategories(),
                card.getCardDiscounts()
        );
    }
}
