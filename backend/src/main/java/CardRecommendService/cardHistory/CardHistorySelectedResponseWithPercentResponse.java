package CardRecommendService.cardHistory;

import java.time.LocalDate;
import java.util.List;

public record CardHistorySelectedResponseWithPercentResponse(
        List<SetCardHistoriesResponse> setCardHistoriesResponses,
        LocalDate startDate,
        LocalDate endDate,
        Integer classificationCost,
        Integer totalCost,
        Double percent
) {
}
