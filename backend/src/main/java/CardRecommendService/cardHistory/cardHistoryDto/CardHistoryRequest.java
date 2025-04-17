package CardRecommendService.cardHistory.cardHistoryDto;

import CardRecommendService.card.Category;

import java.time.LocalDateTime;

public record CardHistoryRequest(
        int amount,
        String storeName,
        LocalDateTime paymentDatetime,
        Category category,
        Long memberCardId  // 회원 카드 ID만 전달
) {}
