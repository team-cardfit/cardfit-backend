package CardRecommendService.cardHistory;

import java.time.LocalDateTime;

public record CardHistoryWithClassificationResponse(
        Long id,
        int amount,
        String storeName,
        LocalDateTime paymentDatetime,
        Category category,
        String uuid,
        String classificationTitle) {

    // 생성자에서 CardHistory 객체를 받아서 레코드 필드에 값을 할당
    public CardHistoryWithClassificationResponse(CardHistory updatedHistory) {
        this(
                updatedHistory.getId(),
                updatedHistory.getAmount(),
                updatedHistory.getStoreName(),
                updatedHistory.getPaymentDatetime(),
                updatedHistory.getCategory(),
                updatedHistory.getUuid(),
                updatedHistory.getClassification() != null ? updatedHistory.getClassification().getTitle() : null // classification이 null일 수 있으므로 처리
        );
    }
}