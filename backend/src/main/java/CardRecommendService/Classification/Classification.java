package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistory;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String title;

    @ManyToOne
    private CardHistory getCardHistories;

    @OneToMany(mappedBy = "classification")
    private List<CardHistory> cardHistories;

    protected Classification() {
    }

    // Classification.java
    public Classification(String title, String uuid) {
        this.title = title;
        this.uuid = uuid;
    }

    public List<CardHistory> getCardHistories() {
        return cardHistories;
    }

    public CardHistory getGetCardHistories() {
        return getCardHistories;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public CardHistory getCardHistory() {
        return getCardHistories;
    }

    public Classification(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }
}
