package CardRecommendService.cardHistory;

import java.time.LocalDate;
import java.util.List;

public record CardHistorySelectedResponseWithPercentResponse(
        List<CardHistoryResponse> cardHistoryResponseList,
        LocalDate startDate,
        LocalDate endDate,
        Integer totalCost,
        Double percent
) {
}
