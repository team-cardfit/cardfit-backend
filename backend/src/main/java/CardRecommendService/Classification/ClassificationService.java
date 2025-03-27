package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final CardHistoryRepository cardHistoryRepository;

    public ClassificationService(ClassificationRepository classificationRepository, CardHistoryRepository cardHistoryRepository) {
        this.classificationRepository = classificationRepository;
        this.cardHistoryRepository = cardHistoryRepository;
    }


    //분류 등록
    @Transactional
    public Long createClassification(CreateClassificationRequest request) {

        Classification classification = new Classification(
                request.title()
        );

        classificationRepository.save(classification);

        return classification.getId();
    }

    //분류 조회
    public List<ClassificationResponse> getClassificationList() {

        List<Classification> classifications = classificationRepository.findAll();

        return classifications.stream()
                .map(classification -> new ClassificationResponse(
                        classification.getTitle()
                ))
                .collect(Collectors.toList());
    }

    //분류 제거
    @Transactional
    public void deleteClassification(Long classificationId) {

        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new RuntimeException("없는 분류"));

        classificationRepository.deleteById(classificationId);
    }

}