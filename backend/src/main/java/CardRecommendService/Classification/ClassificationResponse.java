// ClassificationResponse.java
package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryResponse;
import java.util.List;

public record ClassificationResponse(
        String title,
        List<CardHistoryResponse> cardHistories
) {
}
