package CardRecommendService.card;


import CardRecommendService.cardBenefits.CardBenefits;
import CardRecommendService.cardHistory.Category;
import CardRecommendService.memberCard.MemberCard;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String cardName;  // 카드 이름

    @Column(nullable = false)
    private String cardCorp;  // 카드 발급사

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private int annualFee;  // 연회비

    private String altTxt;

    @OneToMany(mappedBy = "card")
    private List<MemberCard> memberCards;

    @OneToMany(mappedBy = "card")
    private List<CardBenefits> cardBenefits;

    EnumSet<Category> category; // cardHistory 패키지의 Category enum을 사용

    @Enumerated(EnumType.STRING)
    private Category store1;

    @Enumerated(EnumType.STRING)
    private Category store2;

    @Enumerated(EnumType.STRING)
    private Category store3;

    private String discount1;

    private String discount2;

    private String discount3;

    protected Card() {
    }

    public Card(String cardName, String cardCorp, String imgUrl, int annualFee, Category store1, Category store2, Category store3, List<CardBenefits> cardBenefits) {
        this.cardName = cardName;
        this.cardCorp = cardCorp;
        this.imgUrl = imgUrl;
        this.annualFee = annualFee;
        this.store1 = store1;
        this.store2 = store2;
        this.store3 = store3;
        this.cardBenefits = cardBenefits;
    }

//    public Card(String cardName, String cardCorp, String imgUrl, int annualFee, Category store1, String discount1, Category store2, String discount2, Category store3, String discount3, EnumSet<Category> category) {
//        this.cardName = cardName;
//        this.cardCorp = cardCorp;
//        this.imgUrl = imgUrl;
//        this.annualFee = annualFee;
//        this.store1 = store1;
//        this.discount1 = discount1;
//        this.store2 = store2;
//        this.discount2 = discount2;
//        this.store3 = store3;
//        this.discount3 = discount3;
//        this.category = category;
//    }

    public Long getId() {
        return Id;
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

    public EnumSet<Category> getCategory() {
        return category;
    }

    public int getAnnualFee() {
        return annualFee;
    }

    public List<MemberCard> getMemberCards() {
        return memberCards;
    }

    public List<CardBenefits> getCardBenefits() {
        return cardBenefits;
    }

    public Category getStore1() {
        return store1;
    }

    public Category getStore2() {
        return store2;
    }

    public Category getStore3() {
        return store3;
    }

    public String getAltTxt() {
        return altTxt;
    }

    public String getDiscount1() {
        return discount1;
    }

    public String getDiscount2() {
        return discount2;
    }

    public String getDiscount3() {
        return discount3;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return annualFee == card.annualFee && Objects.equals(Id, card.Id) && Objects.equals(cardName, card.cardName) && Objects.equals(cardCorp, card.cardCorp) && Objects.equals(imgUrl, card.imgUrl) && Objects.equals(altTxt, card.altTxt) && Objects.equals(memberCards, card.memberCards) && Objects.equals(cardBenefits, card.cardBenefits) && Objects.equals(category, card.category) && store1 == card.store1 && store2 == card.store2 && store3 == card.store3 && Objects.equals(discount1, card.discount1) && Objects.equals(discount2, card.discount2) && Objects.equals(discount3, card.discount3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, cardName, cardCorp, imgUrl, annualFee, altTxt, memberCards, cardBenefits, category, store1, store2, store3, discount1, discount2, discount3);
    }
}
