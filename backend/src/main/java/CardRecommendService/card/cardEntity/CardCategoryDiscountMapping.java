package CardRecommendService.card.cardEntity;

import CardRecommendService.card.Category;
import jakarta.persistence.*;

@Entity
public class CardCategoryDiscountMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String discount;

    // 순서를 위해 필요하다면 @OrderColumn 역할을 하는 별도의 필드 추가
    private Integer sequence;

    public CardCategoryDiscountMapping() {
    }

    // 만약 Card 정보를 사용해야 한다면, 카드의 아이디만 별도로 받을 수 있도록 생성자를 수정할 수도 있습니다.
    public CardCategoryDiscountMapping(Category category, String discount, Integer sequence) {
        this.category = category;
        this.discount = discount;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
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
