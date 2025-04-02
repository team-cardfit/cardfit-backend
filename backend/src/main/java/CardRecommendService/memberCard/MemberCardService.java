package CardRecommendService.memberCard;

import CardRecommendService.card.CardBasicInfoResponse;
import CardRecommendService.cardHistory.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberCardService {

    private final MemberCardRepository memberCardRepository;
    private final CardHistoryRepository cardHistoryRepository;
    private final CardHistoryQueryRepository cardHistoryQueryRepository;

    public MemberCardService(MemberCardRepository memberCardRepository, CardHistoryRepository cardHistoryRepository, CardHistoryQueryRepository cardHistoryQueryRepository, CardHistoryQueryRepository cardHistoryQueryRepository1) {
        this.memberCardRepository = memberCardRepository;
        this.cardHistoryRepository = cardHistoryRepository;
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
                    List<CardHistory> cardHistories = cardHistoryRepository.findByMemberCardId(memberCard.getId());
                    return new CardBasicInfoResponse(
                            memberCard.getId(),
                            memberCard.getCard().getCardName(),
                            memberCard.getCard().getCardCorp(),
                            memberCard.getCard().getImgUrl(),
                            memberCard.getId(),
                            memberCard.getCard().getAltTxt(),
                            totalCost(cardHistories)
                    );
                })
                .collect(Collectors.toList());
    }

    // 03. 분석카드 목록
    // 선택된 카드 아이디 리스트로 카드 정보 조회 (MemberCardService)
    public List<CardBasicInfoResponse> selectCardsByIds(List<Long> memberCardId) {

        List<MemberCard> memberCards = memberCardRepository.findAllByIdIn(memberCardId);
        List<CardHistory> cardHistories = cardHistoryRepository.findAllById(memberCardId);

        return memberCards.stream()
                .map(memberCard -> new CardBasicInfoResponse(
                        memberCard.getId(),
                        memberCard.getCard().getCardName(),
                        memberCard.getCard().getCardCorp(),
                        memberCard.getCard().getImgUrl(),
                        memberCard.getId(),
                        memberCard.getCard().getAltTxt(),
                        totalCost(cardHistories)
                ))
                .collect(Collectors.toList());
    }

    Integer totalCost (List<CardHistory> cardHistories) {
        int totalAmount = 0;
        for (CardHistory cardHistory : cardHistories) {
           totalAmount = totalAmount + cardHistory.getAmount();
        }
        return totalAmount;
    }

    // 멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링
    public DailyCardHistoryPageResponse getCardsHistories(List<Long> memberCardsId, Integer monthOffset, int page, int size) {

        List<CardHistory> cardHistories = cardHistoryQueryRepository.oderByPaymentDateTimeAndPaging(
                memberCardsId, monthOffset, page, size);

        int totalCount = cardHistoryQueryRepository.getTotalCount(memberCardsId, monthOffset);

        //CardHistory -> CardHistoryResponse 변환
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

        //일별 그룹화 + totalAmount 계산
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

        Integer totalCost = cardHistoryQueryRepository.getMemberCardsTotalAmount(memberCardsId, monthOffset);
        int totalPages = (totalCount + size - 1) / size;

        return new DailyCardHistoryPageResponse(dailyCardHistoryResponses,
                totalCost, page, totalPages, size, totalCount);
    }

}
