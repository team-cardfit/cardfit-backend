package CardRecommendService.Classification;

import CardRecommendService.loginUtils.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ClassificationController.java
@RestController
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    // 분류 생성 엔드포인트: @ResponseStatus를 사용하여 HTTP 201(CREATED)을 반환
    @PostMapping("/classifications")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createClassification(@RequestBody CreateClassificationRequest request,
                                                  @CurrentUserId String uuid) {
        Long classificationId = classificationService.createClassification(request, uuid);
        return Map.of("id", classificationId);
    }

    // 로그인한 사용자의 분류 목록과 연결된 카드 히스토리 조회
    @GetMapping("/classifications")
    public List<ClassificationResponse> getClassificationList(@CurrentUserId String uuid) {
        return classificationService.getClassificationList(uuid);
    }

    // 분류 삭제
    @DeleteMapping("/classifications/{classificationId}")
    public void deleteClassification(@PathVariable Long classificationId) {
        classificationService.deleteClassification(classificationId);
    }
}
