package CardRecommendService.card.cardEntity;

import CardRecommendService.card.cardResponse.CardDetailResponse;
import CardRecommendService.memberCard.MemberCard;
import jakarta.persistence.*;

import java.util.List;

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

    @OneToMany(mappedBy = "card")
    @OrderColumn(name = "sequence")
    private List<CardCategoryDiscountMapping> categoryDiscountMappings;

    protected Card() {

    }

    public Card(String cardName, String cardCorp, String imgUrl, int annualFee, List<MemberCard> memberCards, List<CardCategoryDiscountMapping> categoryDiscountMappings) {
        this.cardName = cardName;
        this.cardCorp = cardCorp;
        this.imgUrl = imgUrl;
        this.annualFee = annualFee;
        this.memberCards = memberCards;
        this.categoryDiscountMappings = categoryDiscountMappings;
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

    public List<CardCategoryDiscountMapping> getCategoryDiscountMappings() {
        return categoryDiscountMappings;
    }

    public CardDetailResponse toDetailResponse() {
        return new CardDetailResponse(
                this.cardName,
                this.cardCorp,
                this.imgUrl,
                this.annualFee,
                this.categoryDiscountMappings
        );
    }
}
