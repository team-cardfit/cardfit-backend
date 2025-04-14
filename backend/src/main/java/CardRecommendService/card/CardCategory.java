package CardRecommendService.card;

import jakarta.persistence.*;

@Entity
public class CardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // enum 값을 문자열로 저장 (예: "통신", "여행" 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 각 CardCategory는 하나의 Card에 속함
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    protected CardCategory() {}

    public CardCategory(Card card, Category category) {
        this.card = card;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public Card getCard() {
        return card;
    }
}
