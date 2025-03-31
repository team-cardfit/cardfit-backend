package CardRecommendService.card;


import CardRecommendService.cardBenefits.CardBenefitsResponse;
import CardRecommendService.cardHistory.Category;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CardService {

    private final CardRepository cardRepository;


    public CardService(CardRepository cardRepository, List<Card> allCards) {
        this.cardRepository = cardRepository;
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
                .limit(4) // 최대 4개 제한
                .map(id -> (Long) id[0])
                .collect(Collectors.toList());

        return new CardRecommendResponse(matchedCards, selectedCategories);
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


}


