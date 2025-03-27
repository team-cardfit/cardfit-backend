package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.Classification.ClassificationResponse;
import CardRecommendService.memberCard.MemberCardResponse;

import java.time.LocalDateTime;

public record CardHistoryResponse(
        String cardName,
        String cardCrop,
        String storeName,
        int amount,
        LocalDateTime paymentDatetime,
        Category category,
        String classification
) {
}
