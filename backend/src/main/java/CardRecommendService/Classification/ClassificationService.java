package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryQueryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

// ClassificationService.java
@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    public ClassificationService(ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
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

        // 연결된 카드 히스토리가 있으면 삭제 불가
        if (classification.getCardHistories() != null && !classification.getCardHistories().isEmpty()) {
            throw new RuntimeException("결제내역이 존재하는 분류는 삭제할 수 없습니다.");
        }

        classificationRepository.deleteById(classificationId);
    }


}
