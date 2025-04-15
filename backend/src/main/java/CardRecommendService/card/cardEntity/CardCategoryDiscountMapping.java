package CardRecommendService.card.cardEntity;

import CardRecommendService.card.Category;
import jakarta.persistence.*;

@Entity
public class CardCategoryDiscountMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String discount;

    // 순서를 위해 필요하다면 @OrderColumn 역할을 하는 별도의 필드 추가
    private Integer sequence;

    public CardCategoryDiscountMapping() {
    }

    public CardCategoryDiscountMapping(Card card, Category category, String discount, Integer sequence) {
        this.card = card;
        this.category = category;
        this.discount = discount;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public Category getCategory() {
        return category;
    }

    public String getDiscount() {
        return discount;
    }

    public Integer getSequence() {
        return sequence;
    }
}
