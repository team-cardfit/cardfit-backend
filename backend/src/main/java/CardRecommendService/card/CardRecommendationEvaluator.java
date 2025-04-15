package CardRecommendService.card;

import CardRecommendService.card.cardEntity.Card;
import CardRecommendService.card.cardResponse.CardDetailResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

//CardRecommendationEvaluator는 후보 카드들에 대해
//기준 카테고리 집합과 일치하는 카드 카테고리 개수를 기반으로 추천 로직을 수행합니다.
public class CardRecommendationEvaluator {

    private final List<Card> cards;
    private final Set<Category> categories;

    public CardRecommendationEvaluator(List<Card> cards, Set<Category> categories) {
        this.cards = cards;
        this.categories = categories;
    }


    // 후보 카드 목록에서 각 카드의 매칭 점수를 계산하여 상위 3개 카드를 추천하고,
    // 이를 CardDetailResponse DTO로 변환하여 반환합니다.
    public List<CardDetailResponse> getRecommendedCardsInfoInternal() {
        // 카드별 매칭 점수를 계산합니다.
        Map<Long, Integer> cardMatchCounts = getCardMatchCounts();

        // 매칭 점수가 높은 상위 3개의 카드 ID를 추출합니다.
        List<Long> topCardIds = cardMatchCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        // topCardIds 순서대로 각 카드 ID에 해당하는 카드를 찾아서 순서를 보존합니다.
        List<Card> topCards = topCardIds.stream()
                .map(id -> cards.stream()
                        .filter(card -> card.getId().equals(id)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 상위 추천 카드들을 DTO로 변환하여 반환합니다.
        return topCards.stream()
                .map(card -> card.toDetailResponse())
                .collect(Collectors.toList());
    }

    //각 카드에 대해 cardCategories 컬렉션을 순회하여,
    //기준 categories 집합에 포함되는 카드 카테고리 개수를 계산합니다.
    private Map<Long, Integer> getCardMatchCounts() {
        return cards.stream()
                .collect(Collectors.toMap(Card::getId,
                        card -> (int) card.getCategoryDiscountMappings().stream()
                                .filter(cardCategory -> categories.contains(cardCategory.getCategory()))
                                .count()));
    }
}
