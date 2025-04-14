package CardRecommendService.card.cardEntity;

import jakarta.persistence.*;

public class CardDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardDiscount;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    protected CardDiscount() {}

    public CardDiscount(String cardDiscount, Card card) {
        this.cardDiscount = cardDiscount;
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public String getCardDiscount() {
        return cardDiscount;
    }

    public Card getCard() {
        return card;
    }
}
