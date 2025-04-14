package CardRecommendService.card;

import CardRecommendService.card.cardEntity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
