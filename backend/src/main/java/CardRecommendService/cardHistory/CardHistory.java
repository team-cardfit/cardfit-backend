package CardRecommendService.cardHistory;


import CardRecommendService.Classification.Classification;
import CardRecommendService.card.Category;
import CardRecommendService.memberCard.MemberCard;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CardHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int amount; // 각 결제 금액

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime paymentDatetime; // 결제 시각

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberCard memberCard;

    @ManyToOne
    @JoinColumn(name = "classification_id")
    private Classification classification;

    protected CardHistory() {
    }

    public CardHistory(int amount, String storeName, LocalDateTime paymentDatetime, Category category, MemberCard memberCard, Classification classification) {
        this.amount = amount;
        this.storeName = storeName;
        this.paymentDatetime = paymentDatetime;
        this.category = category;
        this.memberCard = memberCard;
        this.classification = classification;
    }

    public Long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getStoreName() {
        return storeName;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDateTime getPaymentDatetime() {
        return paymentDatetime;
    }

    public MemberCard getMemberCard() {
        return memberCard;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }



}
