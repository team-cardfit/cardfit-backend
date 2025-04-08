package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;
import CardRecommendService.loginUtils.CurrentUserId;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // 모든 카드 목록 조회
    @GetMapping
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards();
    }

//    // 카드 상세 조회 (해당 카드가 현재 사용자 소유인지 확인)
//    @GetMapping("/{cardId}")
//    public CardDetailResponse getCardDetail(@CurrentUserId String uuid,
//                                            @PathVariable Long cardId) {
//        return cardService.getCardDetailByCardId(uuid, cardId);
//    }
//
//    // 선택된 카테고리 기반 카드 추천
//    @GetMapping("/recommend")
//    public CardRecommendResponse recommendCards(
//            @CurrentUserId String uuid,
//            @RequestParam(defaultValue = "0") int minAnnualFee,
//            @RequestParam(defaultValue = "500000") int maxAnnualFee,
//            @RequestParam Set<Category> storeCategories) {
//        return cardService.getRecommendCards(uuid, storeCategories, minAnnualFee, maxAnnualFee);
//    }

    // 회원 보유 카드 기반 추천 (동적 쿼리: 기본 카테고리 추출)
    @GetMapping("/recommendation")
    public List<CardDetailResponse> recommendByAmount(
            @CurrentUserId String uuid,
            @RequestParam List<Long> selectedCardIds,
            @RequestParam(defaultValue = "0") int minAnnualFee,
            @RequestParam(defaultValue = "500000") int maxAnnualFee,
            @RequestParam(defaultValue = "1") int monthOffset) {
        return cardService.getRecommendedCardsInfo(uuid, selectedCardIds, minAnnualFee, maxAnnualFee, monthOffset);
    }

    // 회원 보유 카드 + 외부 제공 카테고리 기반 추천 (동적 쿼리 포함)
    @GetMapping("/recommendations")
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
