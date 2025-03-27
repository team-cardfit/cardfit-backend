package CardRecommendService.memberCard;

import CardRecommendService.card.CardBasicInfoResponse;
import CardRecommendService.cardHistory.CardHistory;
import CardRecommendService.cardHistory.CardHistoryRepository;
import CardRecommendService.cardHistory.CardHistoryResponse;
import CardRecommendService.cardHistory.Paging;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberCardService {

    private final MemberCardRepository memberCardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    public MemberCardService(MemberCardRepository memberCardRepository, CardHistoryRepository cardHistoryRepository) {
        this.memberCardRepository = memberCardRepository;
        this.cardHistoryRepository = cardHistoryRepository;
    }

    // uuid에 해당하는 사용자의 모든 카드 정보 조회
    public List<CardBasicInfoResponse> getAllMemberCardBasicInfoByUserId(String uuid) {
        // uuid에 해당하는 모든 카드 조회 후, 카드 이름과 이미지만 추출하여 리스트 반환
        return memberCardRepository.findByUuid(uuid)
                .stream()
                .map(memberCard -> new CardBasicInfoResponse(
                        memberCard.getId(),
                        memberCard.getCard().getCardName(),
                        memberCard.getCard().getCardCorp(),
                        memberCard.getCard().getImgUrl(),
                        memberCard.getId(),
                        memberCard.getCard().getAltTxt()
                ))
                .collect(Collectors.toList()); // 리스트로 반환
    }

    // 선택된 카드 아이디 리스트로 카드 정보 조회 (MemberCardService)
    public List<CardBasicInfoResponse> selectCardsByIds(List<Long> memberCardId) {
        List<MemberCard> memberCards = memberCardRepository.findAllByIdIn(memberCardId);

        return memberCards.stream()
                .map(memberCard -> new CardBasicInfoResponse(
                        memberCard.getId(),
                        memberCard.getCard().getCardName(),
                        memberCard.getCard().getImgUrl(),
                        memberCard.getCard().getCardCorp(),
                        memberCard.getId(),
                        memberCard.getCard().getAltTxt() // 선택된 카드들 반환
                ))
                .collect(Collectors.toList());
    }

    // 멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링
    public DailyCardHistoryPageResponse getCardsHistories(List<Long> memberCardIds, Month month, Pageable pageable) {

        // 1. 해당하는 MemberCard들 조회
        List<MemberCard> memberCards = memberCardRepository.findAllByIdIn(memberCardIds);

        // 2. 해당 카드들에 대한 결제 내역 조회 (특정 달에 해당하는)
        YearMonth yearMonth = YearMonth.now().withMonth(month.getValue()); // 현재 연도에 해당 월을 지정
        LocalDate startOfMonth = yearMonth.atDay(1); // 해당 달의 첫 번째 날
        LocalDate endOfMonth = yearMonth.atEndOfMonth(); // 해당 달의 마지막 날

        LocalDateTime startOfMonthTime = startOfMonth.atStartOfDay(); // 시작 시간 (00:00)
        LocalDateTime endOfMonthTime = endOfMonth.atTime(23, 59, 59); // 종료 시간 (23:59:59)

        Page<CardHistory> cardHistories = cardHistoryRepository.findByMemberCardInAndPaymentDatetimeBetween(
                memberCards, startOfMonthTime, endOfMonthTime, pageable
        );

        // 3. CardHistory -> CardHistoryResponse 변환
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

        Paging paging = new Paging(cardHistories.getNumber(),
                cardHistories.getSize(),
                cardHistories.getTotalPages(),
                cardHistories.getTotalElements());

        // 4. 일별 그룹화 + totalAmount 계산
        List<DailyCardHistoryResponse> dailyCardHistoryResponses = responses.stream()
                .collect(Collectors.groupingBy(
                        response -> response.paymentDatetime().toLocalDate(), // 날짜별 그룹화
                        LinkedHashMap::new, // 순서 유지
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> new DailyCardHistoryResponse(
                        entry.getKey(), // 날짜
                        entry.getValue(), // 해당 날짜의 결제 내역 리스트
                        entry.getValue().stream().mapToInt(CardHistoryResponse::amount).sum() // 총 결제 금액
                ))
                .toList();

        return new DailyCardHistoryPageResponse(dailyCardHistoryResponses, paging);
    }

}
