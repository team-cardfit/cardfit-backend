package CardRecommendService.card;

public record CardBasicInfoResponse(
        Long id,
        String cardName,
        String cardCorp,
        String cardImg,
        Long memberCardId,
        String altTxt

) {
}
