package CardRecommendService.cardHistory;

import java.util.List;

public record FindAllResponse(List<CardHistoryResponse> cardHistoryResponseList,
                              Integer totalCost,
                              Paging page) {
}
