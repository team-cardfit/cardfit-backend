package CardRecommendService.Classification;

import CardRecommendService.cardHistory.CardHistorySelectedResponse;
import CardRecommendService.cardHistory.CardHistoryService;
import CardRecommendService.loginUtils.CurrentUserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ClassificationController.java
@RestController
public class ClassificationController {

    private final ClassificationService classificationService;
    private final CardHistoryService cardHistoryService;

    public ClassificationController(ClassificationService classificationService, CardHistoryService cardHistoryService) {
        this.classificationService = classificationService;
        this.cardHistoryService = cardHistoryService;
    }

    // 분류 생성 엔드포인트: @ResponseStatus를 사용하여 HTTP 201(CREATED)을 반환
    @PostMapping("/classifications")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createClassification(@RequestBody CreateClassificationRequest request,
                                                  @CurrentUserId String uuid) {
        Long classificationId = classificationService.createClassification(request, uuid);
        return Map.of("id", classificationId);
    }

    // 분류 삭제
    @DeleteMapping("/classifications/{classificationId}")
    public void deleteClassification(@PathVariable Long classificationId) {
        classificationService.deleteClassification(classificationId);
    }
}
