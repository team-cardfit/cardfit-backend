package CardRecommendService.Classification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ClassificationRepository.java
public interface ClassificationRepository extends JpaRepository<Classification, Long> {
    List<Classification> findByUuid(String uuid);
}

