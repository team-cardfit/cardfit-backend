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

    //특정 사용자의 선택한 카드들의 기간별 사용 내역을 조회
    @GetMapping("/membercards/histories/selected")
    public CardHistorySelectedResponse getSelectedMemberCards(@RequestParam String selectedCardIds,
                                                  @RequestParam(required = false, defaultValue = "1") Integer monthOffset,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "13") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Long> ids = Arrays.stream(selectedCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return cardHistoryService.getSelected(ids, monthOffset, pageable);
    }


    //기능 1. 결제 기록에 Classification 추가.
    @PatchMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
    public CardHistoryWithClassificationResponse updateClassification(
            @PathVariable Long cardHistoryId,
            @PathVariable Long classificationId) {

        CardHistory updatedHistory = cardHistoryService.updateClassification(cardHistoryId, classificationId);

        return new CardHistoryWithClassificationResponse(updatedHistory);
    }

    //기능 2: 결제 기록에서 특정 Classification 삭제
    @DeleteMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
    public CardHistoryWithClassificationResponse deleteClassification(
            @PathVariable Long cardHistoryId,
            @PathVariable Long classificationId) {

        // 서비스에서 해당 결제 기록에서 Classification 삭제 처리
        CardHistory updatedHistory = cardHistoryService.deleteClassification(cardHistoryId, classificationId);

        // 삭제된 결제 기록을 응답으로 반환
        return new CardHistoryWithClassificationResponse(updatedHistory);
    }


    @GetMapping("/membercards/histories/classification")
    public CardHistorySelectedResponseWithPercentResponse getSelectedMemberCards(
            @RequestParam String selectedCardIds,
            @RequestParam(required = false, defaultValue = "1") Integer monthOffset,
            @RequestParam(required = false) Long classificationId) {

        List<Long> ids = Arrays.stream(selectedCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return cardHistoryService.getSelected(ids, monthOffset, classificationId);
    }

    // 카드히스토리 ID 목록은 쿼리 파라미터로, 분류 ID는 요청 본문(UpdateClassificationRequest)으로 받는 예시
    @PatchMapping("/cardhistories/changeclassification")
    @ResponseStatus(HttpStatus.OK)
    public String updateSelectedCardHistoriesClassification(
            @RequestParam String cardHistoryIds, // 예: "123,456,789"
            @RequestBody UpdateClassificationRequest request) {

        // 카드히스토리 ID들을 파싱합니다.
        List<Long> ids = Arrays.stream(cardHistoryIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 서비스 로직 호출
        cardHistoryService.updateClassificationForSelectedCardHistories(ids, request.classificationId());
        return "이동 완료";
    }
}
