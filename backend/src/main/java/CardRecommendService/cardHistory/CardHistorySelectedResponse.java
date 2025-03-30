package CardRecommendService.cardHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CardHistorySelectedResponse(List<CardHistoryResponse> cardHistoryResponseList,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          Integer totalCost,
                                          Paging page) {
}
