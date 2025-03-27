package CardRecommendService.memberCard;


import CardRecommendService.cardHistory.Paging;

import java.util.List;

public record DailyCardHistoryPageResponse(List<DailyCardHistoryResponse> cardHistoryResponses,
                                           Paging paging) {
}
