package CardRecommendService.memberCard;


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

    @ManyToOne
    private Card card;

    @OneToMany(mappedBy = "memberCard")
    private List<CardHistory> cardHistories;

    private String uuid;

    public MemberCard() {
    }

    public MemberCard(String cardNumber, Card card, List<CardHistory> cardHistories, String uuid) {
        this.cardNumber = cardNumber;
        this.card = card;
        this.cardHistories = cardHistories;
        this.uuid = uuid;
    }

    public MemberCard(String cardNumber, Card card, String uuid) {
        this.cardNumber = cardNumber;
        this.card = card;
        this.uuid = uuid;
    }

    public MemberCard(Long id) {
        this.id = id;
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

    public List<CardHistory> getCardHistories() {
        return cardHistories;
    }
}
