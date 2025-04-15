package CardRecommendService.card;

import CardRecommendService.card.cardResponse.CardDetailResponse;
import CardRecommendService.card.cardResponse.CardResponse;
import CardRecommendService.loginUtils.CurrentUserId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // 모든 카드 목록 조회
    @GetMapping("/cards")
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards();
    }

    // 회원 보유 카드 + 외부 제공 카테고리 기반 추천 (동적 쿼리 포함)
    @GetMapping("/cards/recommendations")
    public List<CardDetailResponse> recommendByCategoriesAndAmount(
            @CurrentUserId String uuid,
            @RequestParam List<Long> selectedCardIds,
            @RequestParam(required = false) List<Category> categories,
            @RequestParam(defaultValue = "0") int minAnnualFee,
            @RequestParam(defaultValue = "500000") int maxAnnualFee,
            @RequestParam(defaultValue = "1") int monthOffset) {
        return cardService.getRecommendedCardsInfo(uuid, selectedCardIds, categories, minAnnualFee, maxAnnualFee, monthOffset);
    }
}
