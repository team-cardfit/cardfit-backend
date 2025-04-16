//package CardRecommendService;
//
//import CardRecommendService.card.CardRecommendationEvaluator;
//
//import CardRecommendService.card.Category;
//import CardRecommendService.card.cardEntity.Card;
//import CardRecommendService.card.cardEntity.CardCategoryDiscountMapping;
//import CardRecommendService.card.cardResponse.CardDetailResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class CardRecommendationEvaluatorTest {
//
//    /**
//     * 여러 카드들의 매칭 점수를 기반으로 추천 로직을 검증합니다.
//     * - Card1: 두 개의 매칭 매핑(FOOD, TRAVEL) => 매칭 개수 2
//     * - Card2: 한 개의 매칭 매핑(FOOD) => 매칭 개수 1
//     * - Card3: 매칭되지 않는 매핑(ENTERTAINMENT) => 매칭 개수 0
//     * - Card4: 한 개의 매칭 매핑(TRAVEL)와 한 개의 비매칭 매핑(ENTERTAINMENT) => 매칭 개수 1
//     * 예상 결과는 매칭 개수가 가장 높은 Card1이 첫 번째 추천 결과로 나오며, 매칭이 전혀 없는 Card3는 추천 리스트에서 제외됩니다.
//     */
//    @Test
//    public void testGetRecommendedCardsInfoInternal() {
//        // 선택된 카테고리 집합 (Category는 enum으로 가정: 음식점, 여행, 맞지 않는 카테고리 등)
//        Set<Category> selectedCategories = new HashSet<>();
//        selectedCategories.add(Category.음식점);
//        selectedCategories.add(Category.여행);
//
//        // 카드 객체들을 담을 리스트 생성
//        List<Card> cards = new ArrayList<>();
//
//        // Card 1: 두 개의 매칭 매핑 (FOOD, TRAVEL) => 매칭 개수 2.
//        Card card1 = new Card("Card 1", "Corp 1", "img1", 100, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card1, "id", 1L);
//        List<CardCategoryDiscountMapping> mappings1 = new ArrayList<>();
//        mappings1.add(new CardCategoryDiscountMapping(card1, Category.음식점, "10%", 1));
//        mappings1.add(new CardCategoryDiscountMapping(card1, Category.여행, "5%", 2));
//        ReflectionTestUtils.setField(card1, "categoryDiscountMappings", mappings1);
//        cards.add(card1);
//
//        // Card 2: 한 개의 매칭 매핑 (FOOD) => 매칭 개수 1.
//        Card card2 = new Card("Card 2", "Corp 2", "img2", 150, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card2, "id", 2L);
//        List<CardCategoryDiscountMapping> mappings2 = new ArrayList<>();
//        mappings2.add(new CardCategoryDiscountMapping(card2, Category.음식점, "15%", 1));
//        ReflectionTestUtils.setField(card2, "categoryDiscountMappings", mappings2);
//        cards.add(card2);
//
//        // Card 3: 매칭되지 않는 매핑 (ENTERTAINMENT) => 매칭 개수 0.
//        Card card3 = new Card("Card 3", "Corp 3", "img3", 200, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card3, "id", 3L);
//        List<CardCategoryDiscountMapping> mappings3 = new ArrayList<>();
//        mappings3.add(new CardCategoryDiscountMapping(card3, Category.아울렛, "8%", 1));
//        ReflectionTestUtils.setField(card3, "categoryDiscountMappings", mappings3);
//        cards.add(card3);
//
//        // Card 4: 한 개의 매칭 매핑 (TRAVEL)와 한 개의 비매칭 매핑 (ENTERTAINMENT) => 매칭 개수 1.
//        Card card4 = new Card("Card 4", "Corp 4", "img4", 250, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card4, "id", 4L);
//        List<CardCategoryDiscountMapping> mappings4 = new ArrayList<>();
//        mappings4.add(new CardCategoryDiscountMapping(card4, Category.여행, "20%", 1));
//        mappings4.add(new CardCategoryDiscountMapping(card4, Category.영화, "3%", 2));
//        ReflectionTestUtils.setField(card4, "categoryDiscountMappings", mappings4);
//        cards.add(card4);
//
//        // CardRecommendationEvaluator 인스턴스 생성
//        CardRecommendationEvaluator evaluator = new CardRecommendationEvaluator(cards, selectedCategories);
//        List<CardDetailResponse> recommended = evaluator.getRecommendedCardsInfoInternal();
//
//        assertNotNull(recommended, "추천 결과 리스트는 null이 아니어야 합니다.");
//        // 최대 3개의 카드가 반환되어야 함.
//        assertTrue(recommended.size() <= 3, "최대 3개의 추천 카드가 반환되어야 합니다.");
//
//        // 매칭 점수가 가장 높은 Card 1이 첫 번째 추천 결과로 나와야 함.
//        assertEquals("Card 1", recommended.get(0).cardName(), "매칭 점수가 가장 높은 Card 1이 첫 번째 추천되어야 합니다.");
//
//        // 매칭이 0인 Card 3가 추천 리스트에 포함되지 않았는지 확인합니다.
//        List<String> recommendedNames = recommended.stream()
//                .map(CardDetailResponse::cardName)
//                .collect(Collectors.toList());
//        assertFalse(recommendedNames.contains("Card 3"), "매칭 정보가 없는 카드는 추천 리스트에 포함되면 안 됩니다.");
//    }
//
//    /**
//     * 선택 카테고리 집합이 빈 경우에 대한 테스트입니다.
//     * 이 경우 모든 카드의 매칭 점수는 0이며, 입력된 순서대로 최대 3개까지 추천되어야 합니다.
//     */
//    @Test
//    public void testGetRecommendedCardsInfoInternal_NoMatchingCategories() {
//        // 빈 카테고리 집합 사용
//        Set<Category> selectedCategories = new HashSet<>();
//
//        List<Card> cards = new ArrayList<>();
//
//        Card card1 = new Card("Card 1", "Corp 1", "img1", 100, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card1, "id", 1L);
//        List<CardCategoryDiscountMapping> mappings1 = new ArrayList<>();
//        mappings1.add(new CardCategoryDiscountMapping(card1, Category.음식점, "10%", 1));
//        ReflectionTestUtils.setField(card1, "categoryDiscountMappings", mappings1);
//        cards.add(card1);
//
//        Card card2 = new Card("Card 2", "Corp 2", "img2", 150, new ArrayList<>(), new ArrayList<>());
//        ReflectionTestUtils.setField(card2, "id", 2L);
//        List<CardCategoryDiscountMapping> mappings2 = new ArrayList<>();
//        mappings2.add(new CardCategoryDiscountMapping(card2, Category.여행, "15%", 1));
//        ReflectionTestUtils.setField(card2, "categoryDiscountMappings", mappings2);
//        cards.add(card2);
//
//        // 선택된 카테고리가 없으므로, 모든 카드의 매칭 점수는 0입니다.
//        // 입력된 순서대로 추천 결과가 반환되어야 합니다.
//        CardRecommendationEvaluator evaluator = new CardRecommendationEvaluator(cards, selectedCategories);
//        List<CardDetailResponse> recommended = evaluator.getRecommendedCardsInfoInternal();
//
//        assertNotNull(recommended, "추천 결과 리스트는 null이면 안됩니다.");
//        assertEquals(2, recommended.size(), "카드 수 만큼 추천 결과가 반환되어야 합니다.");
//        assertEquals("Card 1", recommended.get(0).cardName(), "첫 번째 카드가 Card 1이어야 합니다.");
//        assertEquals("Card 2", recommended.get(1).cardName(), "두 번째 카드가 Card 2이어야 합니다.");
//    }
//
//    /**
//     * 빈 카드 리스트를 입력했을 경우, 추천 결과가 빈 리스트로 반환되는지 확인하는 테스트입니다.
//     */
//    @Test
//    public void testGetRecommendedCardsInfoInternal_EmptyCardsList() {
//        Set<Category> selectedCategories = new HashSet<>(Arrays.asList(Category.음식점));
//        List<Card> cards = Collections.emptyList();
//
//        CardRecommendationEvaluator evaluator = new CardRecommendationEvaluator(cards, selectedCategories);
//        List<CardDetailResponse> recommended = evaluator.getRecommendedCardsInfoInternal();
//
//        assertNotNull(recommended, "추천 결과 리스트는 null이 아니어야 합니다.");
//        assertEquals(0, recommended.size(), "빈 카드 리스트에 대해 추천 결과 리스트는 빈 리스트여야 합니다.");
//    }
//}
