package CardRecommendService.cardHistory.cardHistoryDto;

import CardRecommendService.cardHistory.Paging;

import java.time.LocalDate;
import java.util.List;

public record CardHistorySelectedResponse(List<CardHistoryResponse> cardHistoryResponseList,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          Integer totalCost,
                                          Paging page
) {
}
