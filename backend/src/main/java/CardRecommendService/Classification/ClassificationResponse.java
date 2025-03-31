package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryResponse;
import java.util.List;

public record ClassificationResponse(
        List<CardHistoryResponse> cardHistories,
        int amount,
        double percent
) {
}
