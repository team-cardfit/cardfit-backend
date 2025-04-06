package CardRecommendService.card;

import CardRecommendService.cardHistory.CardHistoryQueryRepository;
import CardRecommendService.cardHistory.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardHistoryQueryRepository queryRepository;
    private final QCardRepository qCardRepository;


    public CardService(CardRepository cardRepository, List<Card> allCards, CardHistoryQueryRepository queryRepository, QCardRepository qCardRepository) {
        this.cardRepository = cardRepository;
        this.queryRepository = queryRepository;
        this.qCardRepository = qCardRepository;
    }

    //모든 카드 리스트를 목록으로 조회
    @Transactional
    public List<CardResponse> getAllCards() {
        List<Card> cards = cardRepository.findAll();

        return cards.stream()
                .map(card -> new CardResponse(
                        card.getCardCorp(),
                        card.getCardName(),
                        card.getAnnualFee()
                ))
                .collect(Collectors.toList());
    }


    //카드 상세 조회
    public CardDetailResponse getCardDetailByCardId(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("없는 카드"));

        return new CardDetailResponse(
                card.getCardName(),
                card.getCardCorp(),
                card.getImgUrl(),
                card.getAnnualFee(),
                card.getStore1(),
                card.getDiscount1(),
                card.getStore2(),
                card.getDiscount2(),
                card.getStore3(),
                card.getDiscount3()

        );
    }


    //카드 추천 로직
    public CardRecommendResponse getRecommendCards(Set<Category> selectedCategories, int minAnnualFee, int maxAnnualFee) {
        // 연회비 필터링을 적용하여 모든 카드 조회
        List<Card> filteredCards = cardRepository.findByAnnualFeeBetween(minAnnualFee, maxAnnualFee);

        // 각 카드의 매칭된 카테고리 개수를 계산하여 리스트 생성
        List<Long> matchedCards = filteredCards.stream()
                .map(card -> new long[]{card.getId(), countMatchedCategories(card, selectedCategories)})
                .sorted((a, b) -> Long.compare(b[1], a[1])) // 매칭된 개수 기준으로 내림차순 정렬
                .limit(5) // 최대 4개 제한
                .map(id -> (Long) id[0])
                .collect(Collectors.toList());

        List<Card> top3ByIdIn = cardRepository.findTop3ByIdIn(matchedCards);

        List<CardDetailResponse> list = top3ByIdIn.stream().map(
                        cards -> new CardDetailResponse(cards.getCardName(),
                                cards.getCardCorp(),
                                cards.getImgUrl(),
                                cards.getAnnualFee(),
                                cards.getStore1(),
                                cards.getDiscount1(),
                                cards.getStore2(),
                                cards.getDiscount2(),
                                cards.getStore3(),
                                cards.getDiscount3()
                        ))
                .toList();

        return new CardRecommendResponse(list, selectedCategories);
    }

    // 카드의 카테고리와 선택한 카테고리 일치 개수 계산
    private int countMatchedCategories(Card card, Set<Category> selectedCategories) {
        Set<Category> cardCategories = getCardCategories(card);
        return (int) cardCategories.stream()
                .filter(selectedCategories::contains)
                .count();
    }

    // 카드에서 카테고리 정보 추출
    private Set<Category> getCardCategories(Card card) {
        return Stream.of(card.getStore1(), card.getStore2(), card.getStore3())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }



    //결제 금액 상위 5개 카테고리와 카드의 카테고리 일치 개수 개산
    private Map<Long, Integer> recommendedCads(List<Long> memberCardIds,
                                               int minAnnualFee,
                                               int maxAnnualFee,
                                               int monthOffset){

        Set<Category> categories = queryRepository.getTop5CategoriesList(memberCardIds, monthOffset);

        List<Card> cardsMatchingTopCategories = qCardRepository.findCardsMatchingTopCategoriesAndAnnualFee(categories, minAnnualFee, maxAnnualFee);

        return cardsMatchingTopCategories.stream()
                .collect(Collectors.toMap(Card::getId,
                        card -> {
                            int matchCount = 0;
                            if (categories.contains(card.getStore1())) matchCount++;
                            if (categories.contains(card.getStore2())) matchCount++;
                            if (categories.contains(card.getStore3())) matchCount++;
                            return matchCount;
                        }
                        ));
    };

    //카드추천 로직
    public List<CardDetailResponse> getRecommendedCardsInfo(List<Long> memberCardIds,
                                                            int minAnnualFee,
                                                            int maxAnnualFee,
                                                            int monthOffset){

        Map<Long, Integer> cardIdAndMatchCategoriesCountMap = recommendedCads(memberCardIds, minAnnualFee, maxAnnualFee, monthOffset);

        List<Long> top3CardIds = cardIdAndMatchCategoriesCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Card> top3CardsInfo = cardRepository.findAllById(top3CardIds);

        return top3CardsInfo.stream().map(card ->
                new CardDetailResponse(
                        card.getCardName(),
                        card.getCardCorp(),
                        card.getImgUrl(),
                        card.getAnnualFee(),
                        card.getStore1(),
                        card.getDiscount1(),
                        card.getStore2(),
                        card.getDiscount2(),
                        card.getStore3(),
                        card.getDiscount3())
        ).toList();
    }


}


