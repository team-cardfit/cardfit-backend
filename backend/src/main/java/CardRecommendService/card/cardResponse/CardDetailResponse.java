package CardRecommendService.card.cardResponse;

import CardRecommendService.card.CardCategory;
import CardRecommendService.card.CardDiscount;

import java.util.List;

public record CardDetailResponse(
        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        List<CardCategory> cardCategories,
        List<CardDiscount> cardDiscounts) {
}
