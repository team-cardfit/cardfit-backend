package CardRecommendService.cardBenefits;

import CardRecommendService.card.Card;
import jakarta.persistence.*;

@Entity
public class CardBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bnfName;

    private String bnfDetail;

    private String bngDetail;

    @ManyToOne
    private Card card;

    protected CardBenefits() {
    }

    public CardBenefits(String bnfName, String bnfDetail, String bngDetail, Card card) {
        this.bnfName = bnfName;
        this.bnfDetail = bnfDetail;
        this.bngDetail = bngDetail;
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public String getBnfName() {
        return bnfName;
    }

    public String getBnfDetail() {
        return bnfDetail;
    }

    public String getBngDetail() {
        return bngDetail;
    }

    public Card getCard() {
        return card;
    }
}



