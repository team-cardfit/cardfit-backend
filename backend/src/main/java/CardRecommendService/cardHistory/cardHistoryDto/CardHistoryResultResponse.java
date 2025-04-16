package CardRecommendService.cardHistory.cardHistoryDto;

import java.util.List;

public record CardHistoryResultResponse(
        List<CardHistoryResponse> filteredCardHistories, // 🔥 CardHistory 대신 CardHistoryResponse 사용
        double totalAmount,
        double selectedAmount,
        double percentage
) {
}
