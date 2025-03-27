package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class CardController {

    private final CardRepository cardRepository;
    private final CardService cardService;

    public CardController(CardRepository cardRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    //모든 카드 리스트를 목록으로 조회
    @GetMapping("/cards")
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards();
    }

    //특정 카드 상세 조회
    @GetMapping("/cards/{cardId}")
    public CardDetailResponse getCardDetailByCardId(@PathVariable Long cardId) {

        return cardService.getCardDetailByCardId(cardId);
    }

    //카드 추천 서비스 로직
    @GetMapping("/cards/recommend")
    public List<long[]> getRecommendCards(
            @RequestParam int minAnnualFee,
            @RequestParam int maxAnnualFee,
            @RequestParam Set<Category> storeCategories) {

        return cardService.getRecommendCards(storeCategories, minAnnualFee, maxAnnualFee);

    }
}
