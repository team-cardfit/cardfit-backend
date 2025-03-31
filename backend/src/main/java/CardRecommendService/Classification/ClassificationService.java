package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryRepository;
import CardRecommendService.cardHistory.CardHistoryResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ClassificationService.java
@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final CardHistoryRepository cardHistoryRepository;

    public ClassificationService(ClassificationRepository classificationRepository, CardHistoryRepository cardHistoryRepository) {
        this.classificationRepository = classificationRepository;
        this.cardHistoryRepository = cardHistoryRepository;
    }

    @Transactional
    public Long createClassification(CreateClassificationRequest request, String uuid) {
        Classification classification = new Classification(
                request.title(),
                uuid
        );

        classificationRepository.save(classification);

        return classification.getId();
    }

    // 분류 제거
    @Transactional
    public void deleteClassification(Long classificationId) {
        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new RuntimeException("없는 분류"));

        // 연결된 카드 히스토리가 있으면 삭제 불가
        if (classification.getCardHistories() != null && !classification.getCardHistories().isEmpty()) {
            throw new RuntimeException("결제내역이 존재하는 분류는 삭제할 수 없습니다.");
        }

        classificationRepository.deleteById(classificationId);
    }
}
