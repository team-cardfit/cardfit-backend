package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.Classification.ClassificationRepository;
import CardRecommendService.memberCard.MemberCardRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class CardHistoryService {

    private final CardHistoryRepository cardHistoryRepository;
    private final CardHistoryQueryRepository cardHistoryQueryRepository;
    private final ClassificationRepository classificationRepository;

    public CardHistoryService(CardHistoryRepository cardHistoryRepository, CardHistoryQueryRepository cardHistoryQueryRepository, ClassificationRepository classificationRepository) {
        this.cardHistoryRepository = cardHistoryRepository;
        this.cardHistoryQueryRepository = cardHistoryQueryRepository;
        this.classificationRepository = classificationRepository;
    }

    //특정 사용자의 선택한 카드들의 기간별 사용 내역을 조회
    public CardHistorySelectedResponse getSelected(List<Long> selectedCardIds, Integer monthOffset, Pageable pageable) {
        Page<CardHistory> selectedMemberCards = cardHistoryQueryRepository.findSelectedByMemberIdAndPeriod(selectedCardIds, monthOffset, pageable);

        Integer memberCardsTotalCost
                = cardHistoryQueryRepository.getMemberCardsTotalAmount(selectedCardIds, monthOffset);

        List<CardHistoryResponse> cardHistoryResponses = selectedMemberCards.getContent()
                .stream()
                .map(selectedMemberCard -> new CardHistoryResponse(
                        selectedMemberCard.getMemberCard().getCard().getCardName(),
                        selectedMemberCard.getMemberCard().getCard().getCardCorp(),
                        selectedMemberCard.getStoreName(),
                        selectedMemberCard.getAmount(),
                        selectedMemberCard.getPaymentDatetime(),
                        selectedMemberCard.getCategory(),
                        selectedMemberCard.getClassification() != null ? selectedMemberCard.getClassification().getTitle() : "-" // 🔥 `String` 변환
                )).toList();

        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

            Paging page = new Paging(
                    selectedMemberCards.getNumber() + 1,
                    selectedMemberCards.getSize(),
                    selectedMemberCards.getTotalPages(),
                    selectedMemberCards.getTotalElements());

        return new CardHistorySelectedResponse(cardHistoryResponses, startDate, endDate, memberCardsTotalCost, page);
    }


    //    기능 1. 결제 기록에 Classification 추가.
    @Transactional
    public CardHistory updateClassification(Long cardHistoryId, Long classificationId) {

        CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));

        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));

        // 추가: classification이 null이 아니고, 제대로 설정되었는지 확인
        System.out.println("업데이트할 classification: " + classification);

        cardHistory.setClassification(classification);

        CardHistory updatedHistory = cardHistoryRepository.save(cardHistory);

        // 추가: cardHistory가 제대로 업데이트되었는지 확인
        System.out.println("업데이트된 cardHistory: " + updatedHistory);

        return cardHistoryRepository.save(cardHistory);

    }

    //기능 2.결제 기록에 Classification 삭제.
    @Transactional
    public CardHistory deleteClassification(Long cardHistoryId, Long classificationId) {

        // 결제 기록 찾기
        CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));

        // 해당 Classification 찾기
        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));

        // 만약 해당 결제 기록에 해당 Classification이 설정되어 있으면 null로 설정하여 삭제
        if (cardHistory.getClassification() != null && cardHistory.getClassification().equals(classification)) {
            cardHistory.setClassification(null);
        } else {
            throw new IllegalArgumentException("이 결제 기록에 해당 Classification이 연결되어 있지 않습니다.");
        }

        // 결제 기록 저장
        return cardHistoryRepository.save(cardHistory);
    }

    public CardHistorySelectedResponseWithPercentResponse getSelected(
            List<Long> selectedCardIds,
            Integer monthOffset,
            Long classificationId) {

        // 분류 조건을 포함한 결제 기록 조회 (페이징 없이 전체 결과)
        List<CardHistory> selectedMemberCards =
                cardHistoryQueryRepository.findSelectedByMemberIdAndPeriodAndClassification(
                        selectedCardIds, monthOffset, classificationId);

        // 해당 분류에 해당하는 결제 금액 합계 조회
        Integer classificationTotalCost =
                cardHistoryQueryRepository.getMemberCardsTotalAmountByClassification(
                        selectedCardIds, monthOffset, classificationId);

        // 전체 결제 금액 조회 (모든 분류의 결제 금액)
        Integer overallTotalCost =
                cardHistoryQueryRepository.getMemberCardsTotalAmount(selectedCardIds, monthOffset);

        // 전체 금액 대비 분류 금액의 퍼센티지 계산 (소수점 포함)
        double percent = overallTotalCost > 0
                ? (classificationTotalCost / (double) overallTotalCost) * 100
                : 0;

        // 조회된 결과를 CardHistoryResponse로 매핑
        List<CardHistoryResponse> cardHistoryResponses = selectedMemberCards.stream()
                .map(selectedMemberCard -> new CardHistoryResponse(
                        selectedMemberCard.getMemberCard().getCard().getCardName(),
                        selectedMemberCard.getMemberCard().getCard().getCardCorp(),
                        selectedMemberCard.getStoreName(),
                        selectedMemberCard.getAmount(),
                        selectedMemberCard.getPaymentDatetime(),
                        selectedMemberCard.getCategory(),
                        selectedMemberCard.getClassification() != null
                                ? selectedMemberCard.getClassification().getTitle() : "-"
                ))
                .toList();

        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        return new CardHistorySelectedResponseWithPercentResponse(
                cardHistoryResponses,
                startDate,
                endDate,
                classificationTotalCost,
                percent
        );
    }

    @Transactional
    public void assignDefaultClassification(String uuid) {
        // 1. 로그인한 사용자의 기본 분류("기타")를 조회. 없으면 생성
        Classification defaultClassification = classificationRepository
                .findByUuidAndTitle(uuid, "기타")
                .orElseGet(() -> {
                    // "기본 분류"를 생성할 때, uuid를 할당하여 사용자와 연결
                    Classification newDefault = new Classification("기타", uuid);
                    return classificationRepository.save(newDefault);
                });

        // 2. 해당 uuid의 CardHistory 중 classification이 null인 항목 조회
        List<CardHistory> histories = cardHistoryRepository.findByUuidAndClassificationIsNull(uuid);

        if (histories.isEmpty()) {
            // 이미 모든 기록에 분류가 채워져 있는 경우
            System.out.println("다 채워져 있습니다.");
            return;
        }

        // 3. null인 항목에 기본 분류("기타")를 할당
        histories.forEach(history -> {
            history.setClassification(defaultClassification);
            cardHistoryRepository.save(history);
        });
    }
}