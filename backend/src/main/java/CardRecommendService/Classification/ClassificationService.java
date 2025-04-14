package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryQueryRepository;
import CardRecommendService.cardHistory.CardHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ClassificationCreateResponse createClassification(CreateClassificationRequest request, String uuid) {
        Classification classification = new Classification(
                request.title(),
                uuid
        );

        classificationRepository.save(classification);

        return new ClassificationCreateResponse(classification.getId(),
                classification.getTitle(),
                classification.getUuid());
    }

    //분류 조회
    public List<CreateClassificationResponse> getClassificationsByUuid(String uuid) {
        return classificationRepository.findAllByUuid(uuid).stream()
                .map(c -> new CreateClassificationResponse(
                        c.getId(),
                        c.getTitle()))
                .toList();
    }

    // 분류 제거
    @Transactional
    public void deleteClassification(Long classificationId) {
        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new RuntimeException("없는 분류"));

        // 연결된 카드 히스토리가 있으면 삭제시 미분류로 자동 이동
        if (classification.getCardHistories() != null && !classification.getCardHistories().isEmpty()) {
            Classification targetClassification = classificationRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("미분류 상태가 존자하지 않습니다."));

            classification.reassignCardHistories(targetClassification);

            cardHistoryRepository.saveAll(classification.getCardHistories());
        }
        classificationRepository.deleteById(classificationId);
    }


}
