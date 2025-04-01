package CardRecommendService.Classification;

import CardRecommendService.loginUtils.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @PostMapping("/classifications")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createClassification(@RequestBody CreateClassificationRequest request,
                                                  @CurrentUserId String uuid) {
        Long classificationId = classificationService.createClassification(request, uuid);
        return Map.of("id", classificationId);
    }

    @GetMapping("/classifications")
    @ResponseStatus(HttpStatus.OK)
    public List<CreateClassificationResponse> getMyClassifications(@CurrentUserId String uuid) {
        return classificationService.getClassificationsByUuid(uuid);
    }

    // 분류 삭제
    @DeleteMapping("/classifications/{classificationId}")
    public void deleteClassification(@PathVariable Long classificationId) {
        classificationService.deleteClassification(classificationId);
    }
}
