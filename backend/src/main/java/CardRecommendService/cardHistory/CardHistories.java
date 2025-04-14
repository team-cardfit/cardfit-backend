package CardRecommendService.cardHistory;

import java.time.LocalDateTime;
import java.util.List;

public class CardHistories {
    private final List<CardHistory> cardHistories;

    public CardHistories(List<CardHistory> cardHistories) {
        this.cardHistories = cardHistories;
    }

    public Integer getTotalCost() {
        return cardHistories.stream()
                .map(CardHistory::getAmount)
                .reduce(0, Integer::sum);
    }

    public Integer getTotalCostByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        return cardHistories.stream()
                .filter(ch -> !ch.getPaymentDatetime().isBefore(startDate) && !ch.getPaymentDatetime().isAfter(endDate))
                .map(CardHistory::getAmount)
                .reduce(0, Integer::sum);
    }

}
