package CardRecommendService.card.cardEntity;

import CardRecommendService.memberCard.MemberCard;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardName;  // 카드 이름

    @Column(nullable = false)
    private String cardCorp;  // 카드 발급사

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private int annualFee;  // 연회비

    @OneToMany(mappedBy = "card")
    private List<MemberCard> memberCards;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardCategory> cardCategories = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardDiscount> cardDiscounts = new ArrayList<>();

    protected Card() { }

    public Card(String cardName, String cardCorp, String imgUrl, int annualFee, List<MemberCard> memberCards, List<CardCategory> cardCategories, List<CardDiscount> cardDiscounts) {
        this.cardName = cardName;
        this.cardCorp = cardCorp;
        this.imgUrl = imgUrl;
        this.annualFee = annualFee;
        this.memberCards = memberCards;
        this.cardCategories = cardCategories;
        this.cardDiscounts = cardDiscounts;
    }

    public Long getId() {
        return id;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardCorp() {
        return cardCorp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getAnnualFee() {
        return annualFee;
    }

    public List<MemberCard> getMemberCards() {
        return memberCards;
    }

    public List<CardCategory> getCardCategories() {
        return cardCategories;
    }

    public List<CardDiscount> getCardDiscounts() {
        return cardDiscounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return annualFee == card.annualFee &&
                Objects.equals(id, card.id) &&
                Objects.equals(cardName, card.cardName) &&
                Objects.equals(cardCorp, card.cardCorp) &&
                Objects.equals(imgUrl, card.imgUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardName, cardCorp, imgUrl, annualFee);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardName='" + cardName + '\'' +
                ", cardCorp='" + cardCorp + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", annualFee=" + annualFee +
                '}';
    }
}
