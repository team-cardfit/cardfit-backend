package CardRecommendService.card.cardResponse;

import CardRecommendService.card.cardEntity.CardCategoryDiscountMapping;

import java.util.List;

public record CardDetailResponse(
        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        List<CardCategoryDiscountMapping> categoryDiscountMappings) {
}
