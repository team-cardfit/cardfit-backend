package CardRecommendService.memberCard;

import java.util.List;

public record DailyCardHistoryRequest(String uuid,
                                      List<Long> memberCardId) {
}
