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

        Set<Category> categories;
        if (providedCategories == null || providedCategories.isEmpty())
            categories = queryRepository.getTop5CategoriesList(userMemberCardIds, monthOffset);
        else categories = new HashSet<>(providedCategories);

        // 후보 카드 조회 (QCardRepository를 통해 minAnnualFee, maxAnnualFee 조건 적용)
        List<Card> candidateCards = qCardRepository.findCardsMatchingTopCategoriesAndAnnualFee(categories, minAnnualFee, maxAnnualFee);

        CardRecommendationEvaluator cardRecommendationEvaluator1 = new CardRecommendationEvaluator(candidateCards, categories);

        return cardRecommendationEvaluator1.getRecommendedCardsInfoInternal();
    }
}
