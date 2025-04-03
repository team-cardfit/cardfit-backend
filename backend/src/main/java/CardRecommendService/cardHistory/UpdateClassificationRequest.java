package CardRecommendService.cardHistory;

import java.util.List;

public record UpdateClassificationRequest(
        Long classificationId,
        List<Long> cardHistoriesIds
) {
}
