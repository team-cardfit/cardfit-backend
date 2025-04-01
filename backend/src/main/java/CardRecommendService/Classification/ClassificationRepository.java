package CardRecommendService.Classification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassificationRepository extends JpaRepository<Classification, Long> {
    List<Classification> findAllByUuid(String uuid);
}
