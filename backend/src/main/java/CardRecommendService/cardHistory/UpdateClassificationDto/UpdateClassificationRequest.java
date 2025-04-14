package CardRecommendService.cardHistory.UpdateClassificationDto;

import java.util.List;

public record UpdateClassificationRequest(
        Long classificationId,
        List<Long> cardHistoriesIds
) {
}
