package CardRecommendService.cardHistory.cardHistoryResponse;

import CardRecommendService.card.Category;

import java.time.LocalDateTime;

public record SetCardHistoriesResponse(
        Long cardHistoryId,
        String cardName,
        String cardCorp,
        String storeName,
        int amount,
        LocalDateTime paymentDatetime,
        Category category,
        String classification
) {
}
