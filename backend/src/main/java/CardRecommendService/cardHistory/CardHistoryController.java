package CardRecommendService.cardHistory;

import CardRecommendService.loginUtils.CurrentUserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CardHistoryController {

    private final CardHistoryService cardHistoryService;

    public CardHistoryController(CardHistoryService cardHistoryService) {
        this.cardHistoryService = cardHistoryService;
    }

    // 특정 사용자의 선택한 카드들의 기간별 사용 내역 조회 (로그인한 사용자 uuid 추가)
    @GetMapping("/membercards/histories/selected")
    public CardHistorySelectedResponse getSelectedMemberCards(
            @CurrentUserId String uuid,
            @RequestParam String selectedCardIds,
            @RequestParam(required = false, defaultValue = "1") Integer monthOffset,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "13") int size) {

        Pageable pageable = PageRequest.of(currentPage - 1, size);

        List<Long> ids = Arrays.stream(selectedCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // uuid를 추가로 전달하여 로그인 사용자와 일치하는지 검증
        return cardHistoryService.getSelected(uuid, ids, monthOffset, pageable);
    }

    // 결제 기록에 Classification 추가 (로그인한 사용자 uuid 추가)
    @PatchMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
    public CardHistoryWithClassificationResponse updateClassification(
            @CurrentUserId String uuid,
            @PathVariable Long cardHistoryId,
            @PathVariable Long classificationId) {

        CardHistory updatedHistory = cardHistoryService.updateClassification(uuid, cardHistoryId, classificationId);

        return new CardHistoryWithClassificationResponse(updatedHistory);
    }

    // 결제 기록에서 특정 Classification 삭제 (로그인한 사용자 uuid 추가)
    @DeleteMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
    public CardHistoryWithClassificationResponse deleteClassification(
            @CurrentUserId String uuid,
            @PathVariable Long cardHistoryId,
            @PathVariable Long classificationId) {

        CardHistory updatedHistory = cardHistoryService.deleteClassification(uuid, cardHistoryId, classificationId);

        return new CardHistoryWithClassificationResponse(updatedHistory);
    }

    // 분류 조건을 포함하여 결제 내역 조회 (로그인한 사용자 uuid 추가)
    @GetMapping("/membercards/histories/classification")
    public CardHistorySelectedResponseWithPercentResponse getSelectedMemberCards(
            @CurrentUserId String uuid,
            @RequestParam String selectedCardIds,
            @RequestParam(required = false, defaultValue = "1") Integer monthOffset,
            @RequestParam(required = false) Long classificationId) {

        List<Long> ids = Arrays.stream(selectedCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return cardHistoryService.getSelected(uuid, ids, monthOffset, classificationId);
    }

    // 여러 결제 기록에 대해 분류 업데이트 (로그인한 사용자 uuid 추가)
    @PatchMapping("/cardhistories/changeclassification")
    @ResponseStatus(HttpStatus.OK)
    public UpdateClassificationResponse updateSelectedCardHistoriesClassification(
            @CurrentUserId String uuid,
            @RequestBody UpdateClassificationRequest request) {

        return cardHistoryService.updateClassificationForSelectedCardHistories(uuid, request);
    }

    // 모든 분류(분석) 조회하기 (로그인한 사용자 uuid 추가)
    @GetMapping("membercards/classifications/analyzed")
    public List<AnalyzedResponse> getClassificationsData(
            @CurrentUserId String uuid,
            @RequestParam String selectedCardIds,
            @RequestParam(defaultValue = "1") int monthOffset) {

        List<Long> ids = Arrays.stream(selectedCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return cardHistoryService.cardHistoriesAnalyzedByClassifications(uuid, ids, monthOffset);
    }
}
