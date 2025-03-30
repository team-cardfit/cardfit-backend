package CardRecommendService.memberCard;


import CardRecommendService.card.CardBasicInfoResponse;
import CardRecommendService.cardHistory.CardHistorySelectedRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MemberCardController {


    private MemberCardService memberCardService;

    public MemberCardController(MemberCardService memberCardService) {
        this.memberCardService = memberCardService;
    }

    // 02. 내 카드 불러오기
    // uuid에 해당하는 사용자의 모든 카드 목록 조회
    @GetMapping("/membercards/{uuid}")
    public List<CardBasicInfoResponse> getAllMemberCardBasicInfo(@PathVariable String uuid) {

        return memberCardService.getAllMemberCardBasicInfoByUserId(uuid); // 카드 목록을 리스트로 반환
    }

    // 03. 소비패턴분석카드
    @GetMapping("/membercards")
    public List<CardBasicInfoResponse> selectCardsByIds(@RequestParam String memberCardIds) {
        List<Long> ids = Arrays.stream(memberCardIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // MemberCardService에서 선택된 카드들 반환
        return memberCardService.selectCardsByIds(ids);

    }

    // 05. n월 내역
    // 멤버 카드와 n월 결제 내역을 조회, 일 단위로 묶어서 보여줌
    @GetMapping("/membercards/daily")
    public DailyCardHistoryPageResponse getCardsHistories(
            @RequestParam List<Long> memberCardIds,
            @RequestParam (required = false, defaultValue = "1") int monthOffset,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "13") int size) {
//        Month convertedMonth = Month.of(month); // int를 Month로 변환

//        Pageable pageable = PageRequest.of(page - 1, size);

        return memberCardService.getCardsHistories(memberCardIds, monthOffset, page, size);
    }

}