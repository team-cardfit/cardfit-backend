package CardRecommendService.memberCard;

import CardRecommendService.card.CardDetailResponse;
import CardRecommendService.cardHistory.CardHistoryResponse;

import java.util.List;

public record MemberCardResponse(

        String cardNumber,
        String cardImg,
        String uuid,
        CardDetailResponse card,
        List<CardHistoryResponse> cardHistories


) {
}
