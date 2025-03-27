package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.Classification.ClassificationRepository;
import CardRecommendService.card.Card;
import CardRecommendService.card.CardResponse;
import CardRecommendService.cardBenefits.CardBenefitsResponse;
import CardRecommendService.memberCard.MemberCard;
import CardRecommendService.memberCard.MemberCardRepository;

import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CardHistoryService {

    private final CardHistoryRepository cardHistoryRepository;
    private final MemberCardRepository memberCardRepository;

    private final CardHistoryQueryRepository cardHistoryQueryRepository;

    private final ClassificationRepository classificationRepository;

    public CardHistoryService(CardHistoryRepository cardHistoryRepository, MemberCardRepository memberCardRepository, CardHistoryQueryRepository cardHistoryQueryRepository, ClassificationRepository classificationRepository) {
        this.cardHistoryRepository = cardHistoryRepository;
        this.memberCardRepository = memberCardRepository;
        this.cardHistoryQueryRepository = cardHistoryQueryRepository;
        this.classificationRepository = classificationRepository;
    }

    //특정 사용자의 선택한 카드들의 기간별 사용 내역을 조회
    public FindAllResponse getSelected(String uuid, List<Long> memberCardIds, Integer monthOffset, Pageable pageable) {
        Page<CardHistory> selectedMemberCards = cardHistoryQueryRepository.findSelectedByMemberIdAndPeriod(uuid, memberCardIds, monthOffset, pageable);

        Integer memberCardsTotalCost
                = cardHistoryQueryRepository.getMemberCardsTotalAmount(uuid, memberCardIds, monthOffset);

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

        Paging page = new Paging(
                selectedMemberCards.getNumber(),
                selectedMemberCards.getSize(),
                selectedMemberCards.getTotalPages(),
                selectedMemberCards.getTotalElements());

        return new FindAllResponse(cardHistoryResponses, memberCardsTotalCost, page);
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

    @Transactional
    public CardHistoryResultPageResponse calculateClassificationPayments(
            String uuid, List<Long> memberCardIds, Integer monthOffset, List<Long> classificationIds, Pageable pageable) {

        // 1. 총 결제 금액을 `getMemberCardsTotalAmount`로 구하기
        Integer totalAmount = cardHistoryQueryRepository.getMemberCardsTotalAmount(uuid, memberCardIds, monthOffset);

        // 2. classificationIds에 해당하는 CardHistory 목록을 조회
        Page<CardHistory> cardHistories = cardHistoryRepository.findByClassificationIdIn(classificationIds, pageable);

        double selectedAmount = 0;

        List<CardHistoryResponse> filteredCardHistories = new ArrayList<>(); // CardHistoryResponse 리스트로 변경

        // 3. 필터링된 카드 기록들을 모은다.
        for (CardHistory history : cardHistories) {
            if (classificationIds.contains(history.getClassification().getId())) { // ClassificationId로 필터링
                filteredCardHistories.add(
                        new CardHistoryResponse(
                                history.getMemberCard().getCard().getCardName(),
                                history.getMemberCard().getCard().getCardCorp(),
                                history.getStoreName(),
                                history.getAmount(),
                                history.getPaymentDatetime(),
                                history.getCategory(),
                                history.getClassification() != null ? history.getClassification().getTitle() : "-"
                        )
                );
                selectedAmount += history.getAmount(); // 선택된 금액의 합산
            }
        }

        Paging paging = new Paging(cardHistories.getNumber(),
                cardHistories.getSize(),
                cardHistories.getTotalPages(),
                cardHistories.getTotalElements());

        // 4. 퍼센티지 계산 (총 금액 대비 선택된 금액 비율)
        double percentage = totalAmount > 0 ? (selectedAmount / totalAmount) * 100 : 0;

        // 퍼센티지를 두 자리까지 반올림
        BigDecimal percentageDecimal = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);

        // 5. 결과 반환
        return new CardHistoryResultPageResponse( new CardHistoryResultResponse(filteredCardHistories, totalAmount, selectedAmount, percentageDecimal.doubleValue()),
                paging);
    }
}


