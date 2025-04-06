package CardRecommendService.Classification;

import CardRecommendService.loginUtils.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    //생성
    @PostMapping("/classifications")
    @ResponseStatus(HttpStatus.CREATED)
    public ClassificationCreateResponse createClassification(@RequestBody CreateClassificationRequest request,
                                                  @CurrentUserId String uuid) {

        return classificationService.createClassification(request, uuid);
    }

    //조회
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
