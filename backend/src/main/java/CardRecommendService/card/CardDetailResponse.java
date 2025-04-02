package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;

public record CardDetailResponse(
        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        Category store1,
        String discount1,
        Category store2,
        String discount2,
        Category store3,
        String discount3
) {
}
