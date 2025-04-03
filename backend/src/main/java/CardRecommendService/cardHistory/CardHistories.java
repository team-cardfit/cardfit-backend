package CardRecommendService.cardHistory;

import java.util.List;

public class CardHistories {
    private final List<CardHistory> cardHistories;

    public CardHistories(List<CardHistory> cardHistories) {
        this.cardHistories = cardHistories;
    }

//    public Integer getTotalCost() {
//        int totalAmount = 0;
//        if (cardHistories != null) {
//            for (CardHistory cardHistory : cardHistories) {
//                totalAmount += cardHistory.getAmount();
//            }
//        }
//        return totalAmount;
//    }

    public Integer getTotalCost() {
        return cardHistories.stream()
                .map(CardHistory::getAmount)
                .reduce(0, Integer::sum);
    }
}
