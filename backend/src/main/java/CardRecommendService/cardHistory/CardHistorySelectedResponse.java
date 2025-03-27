package CardRecommendService.cardHistory;

import java.util.List;

public record CardHistorySelectedResponse(List<CardHistoryResponse> cardHistoryResponseList,
                              Integer totalCost,
                              Paging page) {
}
