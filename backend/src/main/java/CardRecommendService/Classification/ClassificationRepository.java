package CardRecommendService.Classification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassificationRepository extends JpaRepository<Classification, Long> {
    Optional<Classification> findByUuidAndTitle(String uuid, String title);
}
