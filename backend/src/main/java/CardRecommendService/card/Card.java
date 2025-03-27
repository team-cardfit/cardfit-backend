package CardRecommendService.card;


import CardRecommendService.cardBenefits.CardBenefits;
import CardRecommendService.cardHistory.Category;
import CardRecommendService.memberCard.MemberCard;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.List;

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

    public Card(String cardName, String cardCorp, int annualFee, Category store1, Category store2, Category store3, List<CardBenefits> cardBenefits) {
        this.cardName = cardName;
        this.cardCorp = cardCorp;
        this.annualFee = annualFee;
        this.store1 = store1;
        this.store2 = store2;
        this.store3 = store3;
        this.cardBenefits = cardBenefits;
    }



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
}
