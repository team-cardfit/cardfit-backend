package CardRecommendService.cardHistory;

import java.util.List;

public record CardHistorySelectedRequest(String uuid,
                                         List<Long> memberCardId) {
}
