package CardRecommendService.memberCard;

import CardRecommendService.cardHistory.CardHistories;
import CardRecommendService.cardHistory.CardHistory;
import CardRecommendService.card.Card;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class MemberCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @OneToMany(mappedBy = "memberCard")
    private List<CardHistory> cardHistories;

    private String uuid;

    protected MemberCard() {
    }

    public MemberCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Card getCard() {
        return card;
    }

    public String getUuid() {
        return uuid;
    }

    //일급컬랙션 사용
    public CardHistories getCardHistoriesCollection() {
        return new CardHistories(cardHistories);
    }
}
