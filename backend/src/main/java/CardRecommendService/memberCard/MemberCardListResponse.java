package CardRecommendService.memberCard;

import CardRecommendService.card.Card;

import java.util.List;

public record MemberCardListResponse(List<MemberCardResponse> cards) {}