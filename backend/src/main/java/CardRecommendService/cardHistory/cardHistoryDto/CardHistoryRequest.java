package CardRecommendService.cardHistory.cardHistoryDto;

import CardRecommendService.card.Category;
import CardRecommendService.memberCard.MemberCard;

import java.time.LocalDateTime;

public record CardHistoryRequest(int amount,
                                 String storeName,
                                 LocalDateTime paymentDatetime,
                                 Category category,
                                 MemberCard memberCard) {
}
