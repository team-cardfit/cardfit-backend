package CardRecommendService.cardHistory;

import java.time.LocalDateTime;

public record CardHistoryRequest(String uuid,
                                 Long memberCardId,
                                 int amount,
                                 String storeName,
                                 LocalDateTime paymentDatetime,
                                 Category category) {
}
