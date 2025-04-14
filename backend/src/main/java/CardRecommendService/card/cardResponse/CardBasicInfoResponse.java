package CardRecommendService.card.cardResponse;

public record CardBasicInfoResponse(
        Long id,
        String cardName,
        String cardCorp,
        String cardImg,
        Long memberCardId,
        Integer cardTotalAmount) {
}
