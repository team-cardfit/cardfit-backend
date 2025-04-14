package CardRecommendService.memberCard;

import CardRecommendService.cardHistory.cardHistoryResponse.CardHistoryResponse;

import java.time.LocalDate;
import java.util.List;

public record DailyCardHistoryResponse(
        LocalDate date,
        List<CardHistoryResponse> paymentHistories,
        int dailyAmount
) {
}
