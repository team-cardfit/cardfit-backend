package CardRecommendService.memberCard;


import CardRecommendService.card.cardResponse.CardBasicInfoResponse;
import CardRecommendService.cardHistory.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberCardService {

    private final MemberCardRepository memberCardRepository;
    private final CardHistoryQueryRepository cardHistoryQueryRepository;

    public MemberCardService(MemberCardRepository memberCardRepository, CardHistoryQueryRepository cardHistoryQueryRepository, CardHistoryQueryRepository cardHistoryQueryRepository1) {
        this.memberCardRepository = memberCardRepository;
        this.cardHistoryQueryRepository = cardHistoryQueryRepository1;
    }

    // 02. 내 카드 불러오기
    // uuid에 해당하는 사용자의 모든 카드 정보 조회
    public List<CardBasicInfoResponse> getAllMemberCardBasicInfoByUserId(String uuid) {
        // uuid에 해당하는 모든 카드 조회 후, 카드 이름과 이미지만 추출하여 리스트 반환
        return memberCardRepository.findByUuid(uuid)
                .stream()
                .map(memberCard -> {
                    // 각 카드에 해당하는 결제 내역을 조회하여 총 결제 금액 계산
                    return new CardBasicInfoResponse(
                            memberCard.getId(),
                            memberCard.getCard().getCardName(),
                            memberCard.getCard().getCardCorp(),
                            memberCard.getCard().getImgUrl(),
                            memberCard.getId(),
                            memberCard.getCardHistoriesCollection().getTotalCost()
                    );
                })
                .collect(Collectors.toList());
    }

    // 03. 분석카드 목록
    // 선택된 카드 아이디 리스트로 카드 정보 조회 (MemberCardService)
    public List<CardBasicInfoResponse> selectCardsByIds(List<Long> memberCardId, String uuid, int monthOffset) {
        // 현재 사용자 uuid와 선택한 카드 id 목록이 모두 일치하는 경우에만 가져옴
        List<MemberCard> memberCards = memberCardRepository.findAllByIdInAndUuid(memberCardId, uuid);

        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);
        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);

        return memberCards.stream()
                .map(memberCard -> new CardBasicInfoResponse(
                        memberCard.getId(),
                        memberCard.getCard().getCardName(),
                        memberCard.getCard().getCardCorp(),
                        memberCard.getCard().getImgUrl(),
                        memberCard.getId(),
                        memberCard.getCardHistoriesCollection().getTotalCostByMonth(startDate, endDate) // 월별 총합 계산
                ))
                .collect(Collectors.toList());
    }

    // 멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링
    public DailyCardHistoryPageResponse getCardsHistories(String uuid, List<Long> memberCardsId, Integer monthOffset, int page, int size) {
        // 현재 사용자(uuid)에 해당하는 카드만 필터링
        List<MemberCard> validMemberCards = memberCardRepository.findAllByIdInAndUuid(memberCardsId, uuid);
        List<Long> validMemberCardIds = validMemberCards.stream()
                .map(MemberCard::getId)
                .collect(Collectors.toList());

        // validMemberCardIds를 기준으로 결제 내역 조회
        List<CardHistory> cardHistories = cardHistoryQueryRepository.oderByPaymentDateTimeAndPaging(
                validMemberCardIds, monthOffset, page, size);

        int totalCount = cardHistoryQueryRepository.getTotalCount(validMemberCardIds, monthOffset);

        // CardHistory -> CardHistoryResponse 변환
        List<CardHistoryResponse> responses = cardHistories.stream()
                .map(cardHistory -> new CardHistoryResponse(
                        cardHistory.getMemberCard().getCard().getCardName(),
                        cardHistory.getMemberCard().getCard().getCardCorp(),
                        cardHistory.getStoreName(),
                        cardHistory.getAmount(),
                        cardHistory.getPaymentDatetime(),
                        cardHistory.getCategory(),
                        cardHistory.getClassification() != null ? cardHistory.getClassification().getTitle() : "-"
                ))
                .toList();

        // 일별 그룹화 및 총 결제 금액 계산
        List<DailyCardHistoryResponse> dailyCardHistoryResponses = responses.stream()
                .collect(Collectors.groupingBy(
                        response -> response.paymentDatetime().toLocalDate(), // 날짜별 그룹화
                        LinkedHashMap::new, // 순서 유지
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new DailyCardHistoryResponse(
                        entry.getKey(), // 날짜
                        entry.getValue(), // 해당 날짜의 결제 내역 리스트
                        entry.getValue().stream().mapToInt(CardHistoryResponse::amount).sum() // 총 결제 금액
                ))
                .toList();

        Integer totalCost = cardHistoryQueryRepository.getMemberCardsTotalAmount(validMemberCardIds, monthOffset);
        int totalPages = (totalCount + size - 1) / size;

        return new DailyCardHistoryPageResponse(dailyCardHistoryResponses,
                totalCost, page, totalPages, size, totalCount);
    }
}