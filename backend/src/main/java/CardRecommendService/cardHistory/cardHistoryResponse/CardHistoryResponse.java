package CardRecommendService.cardHistory.cardHistoryResponse;

import CardRecommendService.cardHistory.Category;

import java.time.LocalDateTime;

public record CardHistoryResponse(
        String cardName,
        String cardCorp,
        String storeName,
        int amount,
        LocalDateTime paymentDatetime,
        Category category,
        String classification
) {
}
