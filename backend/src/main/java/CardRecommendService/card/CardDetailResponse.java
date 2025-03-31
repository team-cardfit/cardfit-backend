package CardRecommendService.card;

import CardRecommendService.cardBenefits.CardBenefitsResponse;
import CardRecommendService.cardHistory.Category;
import CardRecommendService.memberCard.MemberCardResponse;

import java.util.List;
import java.util.stream.Collectors;

public record CardDetailResponse(

        String cardName,
        String cardCorp,
        String imgUrl,
        int annualFee,
        Category store1,
        String discount1,
        Category store2,
        String discount2,
        Category store3,
        String discount3

) {


}
