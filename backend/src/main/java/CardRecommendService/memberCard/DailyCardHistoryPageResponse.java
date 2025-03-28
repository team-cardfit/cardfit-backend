package CardRecommendService.memberCard;


import CardRecommendService.cardHistory.Paging;

import java.util.List;

public record DailyCardHistoryPageResponse(List<DailyCardHistoryResponse> cardHistoryResponses,
                                           Integer totalCost,
                                           int page,
                                           int totalPages,
                                           int size,
                                           int totalCount
                                           ) {

}
