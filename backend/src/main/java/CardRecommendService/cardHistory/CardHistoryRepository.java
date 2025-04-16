package CardRecommendService.cardHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    List<CardHistory> findByClassificationId(Long classificationId);
}
