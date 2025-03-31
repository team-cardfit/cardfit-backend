package CardRecommendService.cardHistory;

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
