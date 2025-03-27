package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistory;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    private CardHistory getCardHistories;

    @OneToMany(mappedBy = "classification")
    private List<CardHistory> cardHistories;

    private boolean isChecked; // 체크 여부를 나타내는 필드

    protected Classification() {
    }

    public Classification(Long id, String title, CardHistory getCardHistories, List<CardHistory> cardHistories, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.getCardHistories = getCardHistories;
        this.cardHistories = cardHistories;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public Classification(String title) {
        this.title = title;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
