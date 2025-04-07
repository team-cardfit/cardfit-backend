package CardRecommendService.card;

import CardRecommendService.cardHistory.CardHistoryQueryRepository;
import CardRecommendService.cardHistory.Category;
import CardRecommendService.memberCard.MemberCard;
import CardRecommendService.memberCard.MemberCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // uuid와 cardId를 통해 해당 카드가 로그인 사용자 소유인지 확인 후 상세 정보를 반환
    public CardDetailResponse getCardDetailByCardId(String uuid, Long cardId) {
        // MemberCardRepository의 메서드 반환 타입을 Optional<MemberCard>로 수정했다고 가정합니다.
        MemberCard memberCard = memberCardRepository.findFirstByCard_IdAndUuid(cardId, uuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 카드가 사용자의 카드가 아닙니다."));
        Card card = memberCard.getCard();
        return mapToCardDetailResponse(card);
    }

    // 선택된 카테고리 기반 추천 (사용자 uuid를 받음)
    public CardRecommendResponse getRecommendCards(String uuid, Set<Category> selectedCategories, int minAnnualFee, int maxAnnualFee) {
        // 추가 검증 로직이 필요한 경우 여기에 구현 가능
        List<Card> filteredCards = cardRepository.findByAnnualFeeBetween(minAnnualFee, maxAnnualFee);
        List<Long> matchedCardIds = filteredCards.stream()
                .map(card -> new long[]{card.getId(), countMatchedCategories(card, selectedCategories)})
                .sorted((a, b) -> Long.compare(b[1], a[1]))
                .limit(5)
                .map(arr -> (Long) arr[0])
                .collect(Collectors.toList());
        List<Card> recommendedCards = cardRepository.findTop3ByIdIn(matchedCardIds);
        List<CardDetailResponse> details = recommendedCards.stream()
                .map(this::mapToCardDetailResponse)
                .collect(Collectors.toList());
        return new CardRecommendResponse(details, selectedCategories);
    }

    // 회원 보유 카드 기반 추천 – 동적 쿼리로 기본 top 카테고리 추출
    public List<CardDetailResponse> getRecommendedCardsInfo(String uuid, List<Long> cardIds,
                                                            int minAnnualFee, int maxAnnualFee, int monthOffset) {
        // 사용자가 소유한 카드 목록(연관된 Card의 id)을 검증
        List<Long> validCardIds = memberCardRepository.findAllByCard_IdInAndUuid(cardIds, uuid)
                .stream()
                .map(memberCard -> memberCard.getCard().getId())
                .collect(Collectors.toList());
        if (validCardIds.isEmpty()) {
            throw new IllegalArgumentException("사용자의 카드 정보가 없습니다.");
        }
        Set<Category> categories = queryRepository.getTop5CategoriesList(validCardIds, monthOffset);
        return getRecommendedCardsInfoInternal(validCardIds, categories, minAnnualFee, maxAnnualFee);
    }

    // 회원 보유 카드 + 외부 제공 카테고리 기반 추천
    public List<CardDetailResponse> getRecommendedCardsInfo(String uuid, List<Long> cardIds,
                                                            List<Category> providedCategories,
                                                            int minAnnualFee, int maxAnnualFee, int monthOffset) {
        List<Long> validCardIds = memberCardRepository.findAllByCard_IdInAndUuid(cardIds, uuid)
                .stream()
                .map(memberCard -> memberCard.getCard().getId())
                .collect(Collectors.toList());
        if (validCardIds.isEmpty()) {
            throw new IllegalArgumentException("사용자의 카드 정보가 없습니다.");
        }
        Set<Category> categories = (providedCategories == null || providedCategories.isEmpty())
                ? queryRepository.getTop5CategoriesList(validCardIds, monthOffset)
                : new HashSet<>(providedCategories);
        return getRecommendedCardsInfoInternal(validCardIds, categories, minAnnualFee, maxAnnualFee);
    }

    private List<CardDetailResponse> getRecommendedCardsInfoInternal(List<Long> validCardIds,
                                                                     Set<Category> categories,
                                                                     int minAnnualFee,
                                                                     int maxAnnualFee) {
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

    private Map<Long, Integer> getCardMatchCounts(Set<Category> categories, int minAnnualFee, int maxAnnualFee) {
        List<Card> cards = qCardRepository.findCardsMatchingTopCategoriesAndAnnualFee(categories, minAnnualFee, maxAnnualFee);
        return cards.stream()
                .collect(Collectors.toMap(Card::getId,
                        card -> {
                            int matchCount = 0;
                            if (categories.contains(card.getStore1())) matchCount++;
                            if (categories.contains(card.getStore2())) matchCount++;
                            if (categories.contains(card.getStore3())) matchCount++;
                            return matchCount;
                        }));
    }

    private int countMatchedCategories(Card card, Set<Category> selectedCategories) {
        return (int) Stream.of(card.getStore1(), card.getStore2(), card.getStore3())
                .filter(Objects::nonNull)
                .filter(selectedCategories::contains)
                .count();
    }

    private CardDetailResponse mapToCardDetailResponse(Card card) {
        return new CardDetailResponse(
                card.getCardName(),
                card.getCardCorp(),
                card.getImgUrl(),
                card.getAnnualFee(),
                card.getStore1(),
                card.getDiscount1(),
                card.getStore2(),
                card.getDiscount2(),
                card.getStore3(),
                card.getDiscount3()
        );
    }
}
