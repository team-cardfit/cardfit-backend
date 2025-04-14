package CardRecommendService.card.cardResponse;

import CardRecommendService.card.cardEntity.CardCategory;
import CardRecommendService.card.cardEntity.CardDiscount;

import java.util.List;

public record CardDetailResponse(
        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        List<CardCategory> cardCategories,
        List<CardDiscount> cardDiscounts) {
}
