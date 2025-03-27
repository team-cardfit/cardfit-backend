package CardRecommendService.card;

import CardRecommendService.cardBenefits.CardBenefitsResponse;
import CardRecommendService.memberCard.MemberCardResponse;

import java.util.List;
import java.util.stream.Collectors;

public record CardDetailResponse(

        String cardIssuer,
        String cardName,
        int annualFee,
        List<CardBenefitsResponse> cardBenefits

) {


}
