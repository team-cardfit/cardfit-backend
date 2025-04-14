package CardRecommendService.card.cardResponse;

import CardRecommendService.card.cardEntity.CardCategory;
import CardRecommendService.card.cardEntity.CardDiscount;

import java.util.List;
import java.util.Set;

public record CardDetailResponse(
        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        Set<CardCategory> cardCategories,
        List<CardDiscount> cardDiscounts) {
}
