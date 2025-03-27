package CardRecommendService;

import CardRecommendService.card.Card;
import CardRecommendService.card.CardRepository;
import CardRecommendService.cardBenefits.CardBenefits;
import CardRecommendService.cardBenefits.CardBenefitsRepository;
import CardRecommendService.cardHistory.CardHistory;
import CardRecommendService.cardHistory.CardHistoryRepository;
import CardRecommendService.cardHistory.Category;
import CardRecommendService.memberCard.MemberCard;
import CardRecommendService.memberCard.MemberCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DataSeeder {

    private final CardRepository cardRepository;
    private final CardBenefitsRepository cardBenefitsRepository;
    private final CardHistoryRepository cardHistoryRepository;
    private final MemberCardRepository memberCardRepository;

    public DataSeeder(CardRepository cardRepository, CardBenefitsRepository cardBenefitsRepository, CardHistoryRepository cardHistoryRepository, MemberCardRepository memberCardRepository) {
        this.cardRepository = cardRepository;
        this.cardBenefitsRepository = cardBenefitsRepository;
        this.cardHistoryRepository = cardHistoryRepository;
        this.memberCardRepository = memberCardRepository;
    }

    @Transactional
    public void initData() {

        Card card1 = new Card("넌이제파산", "고금리은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 10000,
                Category.할인점, Category.스트리밍, Category.배달앱, new ArrayList<>());
        Card card2 = new Card("부자되세요", "황금은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 12000,
                Category.백화점, Category.음식점, Category.편의점, new ArrayList<>());
        Card card3 = new Card("미래희망", "희망은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 15000,
                Category.온라인쇼핑, Category.교통, Category.보험, new ArrayList<>());
        Card card4 = new Card("적자행진", "무한대출은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 13000,
                Category.주유소, Category.여행, Category.커피제과, new ArrayList<>());
        Card card5 = new Card("알뜰카드", "절약은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 11000,
                Category.생활잡화, Category.병원, Category.교육, new ArrayList<>());
        Card card6 = new Card("대출천국", "풍요은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 14000,
                Category.해외결제, Category.스트리밍, Category.항공, new ArrayList<>());
        Card card7 = new Card("VIP클럽", "천하은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 16000,
                Category.영화, Category.주유소, Category.생활, new ArrayList<>());
        Card card8 = new Card("한도무제한", "자유은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 17000,
                Category.편의점, Category.배달앱, Category.반려동물, new ArrayList<>());
        Card card9 = new Card("통장두둑", "행복은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 18000,
                Category.온라인쇼핑, Category.음식점, Category.주유소, new ArrayList<>());
        Card card10 = new Card("절약마스터", "미래은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 19000,
                Category.할인점, Category.병원, Category.여행, new ArrayList<>());
        Card card11 = new Card("스타카드", "별은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 21000,
                Category.편의점, Category.보험, Category.온라인쇼핑, new ArrayList<>());
        Card card12 = new Card("금고", "백만장자은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 22000,
                Category.백화점, Category.영화, Category.보험, new ArrayList<>());
        Card card13 = new Card("스카이클럽", "파란하늘은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 23000,
                Category.여행, Category.편의점, Category.생활, new ArrayList<>());
        Card card14 = new Card("별빛카드", "은하은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 24000,
                Category.주유소, Category.스트리밍, Category.편의점, new ArrayList<>());
        Card card15 = new Card("황금카드", "로얄은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 25000,
                Category.보험, Category.편의점, Category.커피제과, new ArrayList<>());
        Card card16 = new Card("비전카드", "미래비전은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 26000,
                Category.온라인쇼핑, Category.편의점, Category.여행, new ArrayList<>());
        Card card17 = new Card("슈퍼클럽", "그랜드은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 27000,
                Category.슈퍼마켓, Category.편의점, Category.보험, new ArrayList<>());
        Card card18 = new Card("넥스트레벨", "넥스트은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 28000,
                Category.보험, Category.음식점, Category.영화, new ArrayList<>());
        Card card19 = new Card("최고의카드", "프리미엄은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 29000,
                Category.스트리밍, Category.할인점, Category.반려동물, new ArrayList<>());
        Card card20 = new Card("프리카드", "레전드은행", "https://i.namu.wiki/i/xAvOG4dLY84qdmQF5H9Jrwog_4zlY7JUp0P7eyH-xUhHt5so5sPNu5qS5wc6Eyy_h_Fj3sIpiw86zxXx-lGxtRgQQ2Ek6VvMPPtt_8axmCNU-Y711t6xaEOKFg3M8F9eJFT-4uuYf4ioNIl9HKfJZA.webp", 30000,
                Category.배달앱, Category.커피제과, Category.편의점, new ArrayList<>());

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);
        cardRepository.save(card4);
        cardRepository.save(card5);
        cardRepository.save(card6);
        cardRepository.save(card7);
        cardRepository.save(card8);
        cardRepository.save(card9);
        cardRepository.save(card10);
        cardRepository.save(card11);
        cardRepository.save(card12);
        cardRepository.save(card13);
        cardRepository.save(card14);
        cardRepository.save(card15);
        cardRepository.save(card16);
        cardRepository.save(card17);
        cardRepository.save(card18);
        cardRepository.save(card19);
        cardRepository.save(card20);

        String uuid = "1";  // 예시 Member 객체

        MemberCard memberCard1 = new MemberCard("1234-5678-9012-3456", card1, uuid);
        MemberCard memberCard2 = new MemberCard("5678-9012-3456-7890", card2, uuid);
        MemberCard memberCard3 = new MemberCard("9012-3456-7890-1234", card1, uuid);

        memberCardRepository.save(memberCard1);
        memberCardRepository.save(memberCard2);
        memberCardRepository.save(memberCard3);


        CardHistory history1 = new CardHistory(1000, "스타벅스", LocalDateTime.parse("2025-03-01T14:30:00"), Category.커피제과, memberCard1, uuid);
        CardHistory history2 = new CardHistory(5000, "파리바게트", LocalDateTime.parse("2025-03-01T09:15:00"), Category.커피제과, memberCard1, uuid);
        CardHistory history3 = new CardHistory(12000, "BBQ치킨", LocalDateTime.parse("2025-03-01T19:00:00"), Category.음식점, memberCard1, uuid);
        CardHistory history4 = new CardHistory(3000, "GS25", LocalDateTime.parse("2025-03-04T13:45:00"), Category.편의점, memberCard1, uuid);
        CardHistory history5 = new CardHistory(8000, "CGV", LocalDateTime.parse("2025-03-05T18:30:00"), Category.생활, memberCard2, uuid);
        CardHistory history6 = new CardHistory(1500, "투썸플레이스", LocalDateTime.parse("2025-03-06T10:00:00"), Category.커피제과, memberCard2, uuid);
        CardHistory history7 = new CardHistory(4200, "이마트24", LocalDateTime.parse("2025-03-07T15:20:00"), Category.편의점, memberCard2, uuid);
        CardHistory history8 = new CardHistory(25000, "교보문고", LocalDateTime.parse("2025-02-08T11:10:00"), Category.생활, memberCard3, uuid);
        CardHistory history9 = new CardHistory(13000, "맥도날드", LocalDateTime.parse("2025-02-09T17:50:00"), Category.음식점, memberCard3, uuid);
        CardHistory history10 = new CardHistory(6000, "올리브영", LocalDateTime.parse("2025-02-10T14:00:00"), Category.생활잡화, memberCard3, uuid);

        cardHistoryRepository.save(history1);
        cardHistoryRepository.save(history2);
        cardHistoryRepository.save(history3);
        cardHistoryRepository.save(history4);
        cardHistoryRepository.save(history5);
        cardHistoryRepository.save(history6);
        cardHistoryRepository.save(history7);
        cardHistoryRepository.save(history8);
        cardHistoryRepository.save(history9);
        cardHistoryRepository.save(history10);


    }
}
