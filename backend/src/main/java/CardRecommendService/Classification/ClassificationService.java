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
    public Long createClassification(CreateClassificationRequest request, String userUuid) {
        Classification classification = new Classification(
                request.title(),
                userUuid
        );

        classificationRepository.save(classification);
        return classification.getId();
    }

    // 로그인한 사용자의 uuid를 기반으로 분류 목록과 해당 분류에 연결된 카드 히스토리들을 반환
    public List<ClassificationResponse> getClassificationList(String uuid) {
        List<Classification> classifications = classificationRepository.findByUuid(uuid);

        return classifications.stream()
                .map(classification -> new ClassificationResponse(
                        classification.getTitle(),
                        classification.getCardHistories() == null ? List.of()
                                : classification.getCardHistories().stream()
                                .map(ch -> new CardHistoryResponse(
                                        ch.getMemberCard().getCard().getCardName(),
                                        ch.getMemberCard().getCard().getCardCorp(),
                                        ch.getStoreName(),
                                        ch.getAmount(),
                                        ch.getPaymentDatetime(),
                                        ch.getCategory(),
                                        ch.getClassification() != null ? ch.getClassification().getTitle() : "-"
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
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
