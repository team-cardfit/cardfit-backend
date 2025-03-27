package CardRecommendService.Classification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ClassificationController {

    private ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    //분류 생성
    @PostMapping("/classifications")
    public ResponseEntity<Map<String, Long>> createClassification(@RequestBody CreateClassificationRequest request) {
        Long classificationId = classificationService.createClassification(request);
        return ResponseEntity.ok(Map.of("id", classificationId));
    }

    //분류 목록 조회
    @GetMapping("/classifications")
    public List<ClassificationResponse> getClassificationList() {
        return classificationService.getClassificationList();
    }

    //분류 삭제
    @DeleteMapping("/classifications/{classificationId}")
    public void deleteClassification(@PathVariable Long classificationId) {
        classificationService.deleteClassification(classificationId);
    }

}