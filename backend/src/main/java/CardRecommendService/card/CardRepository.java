package CardRecommendService.card;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByAnnualFeeBetween(int minAnnualFee, int maxAnnualFee);

    List<Card> findTop3ByIdIn(List<Long> ids);

}
