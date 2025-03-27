package CardRecommendService.memberCard;


import CardRecommendService.cardHistory.Paging;

import java.util.List;

public record DailyCardHistoryPageResponse(List<DailyCardHistoryResponse> cardHistoryResponses,
                                           Integer totalCost,
                                           int totalGroup,
                                           int startPage,
                                           int totalPage,
                                           int size,
                                           int totalCount) {

}
