package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistory;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    @Column(nullable = false)
    private String title;

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


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public Classification(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }


    public Map<Long, String> getClassificationTitle(Long classificationId){
        HashMap<Long, String> idAndTitle = new HashMap<>();

        idAndTitle.put(id, getTitle());

        return idAndTitle;
    }
}
