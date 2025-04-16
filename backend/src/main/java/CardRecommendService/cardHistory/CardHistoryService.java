package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.Classification.ClassificationRepository;
import CardRecommendService.card.Category;
import CardRecommendService.cardHistory.UpdateClassificationDto.UpdateClassificationRequest;
import CardRecommendService.cardHistory.UpdateClassificationDto.UpdateClassificationResponse;
import CardRecommendService.cardHistory.cardHistoryDto.*;
import CardRecommendService.memberCard.MemberCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardHistoryService {

    private final CardHistoryRepository cardHistoryRepository;
    private final CardHistoryQueryRepository cardHistoryQueryRepository;
    private final ClassificationRepository classificationRepository;
    private final MemberCardRepository memberCardRepository;

    public CardHistoryService(CardHistoryRepository cardHistoryRepository,
                              CardHistoryQueryRepository cardHistoryQueryRepository,
                              ClassificationRepository classificationRepository,
                              MemberCardRepository memberCardRepository) {
        this.cardHistoryRepository = cardHistoryRepository;
        this.cardHistoryQueryRepository = cardHistoryQueryRepository;
        this.classificationRepository = classificationRepository;
        this.memberCardRepository = memberCardRepository;
    }

    // 특정 사용자의 선택한 카드들의 기간별 사용 내역 조회 (uuid 추가)
    public CardHistorySelectedResponse getSelected(String uuid, List<Long> selectedCardIds, Integer monthOffset, Pageable pageable) {
        // 먼저, 선택된 카드들이 해당 사용자의 소유인지 검증
        List<Long> userMemberCardIds = memberCardRepository.findByUuid(uuid)
                .stream()
                .filter(memberCard -> selectedCardIds.contains(memberCard.getId()))
                .map(memberCard -> memberCard.getId())
                .toList();


        Page<CardHistory> selectedMemberCards =
                cardHistoryQueryRepository.findSelectedByMemberIdAndPeriod(userMemberCardIds, monthOffset, pageable);

        Integer memberCardsTotalCost =
                cardHistoryQueryRepository.getMemberCardsTotalAmount(userMemberCardIds, monthOffset);

        List<CardHistoryResponse> cardHistoryResponses = selectedMemberCards.getContent()
                .stream()
                .map(ch -> new CardHistoryResponse(
                        ch.getMemberCard().getCard().getCardName(),
                        ch.getMemberCard().getCard().getCardCorp(),
                        ch.getStoreName(),
                        ch.getAmount(),
                        ch.getPaymentDatetime(),
                        ch.getCategory(),
                        ch.getClassification() != null ? ch.getClassification().getTitle() : "-"
                ))
                .collect(Collectors.toList());

        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        Paging page = new Paging(
                selectedMemberCards.getNumber() + 1,
                selectedMemberCards.getSize(),
                selectedMemberCards.getTotalPages(),
                selectedMemberCards.getTotalElements()
        );

        return new CardHistorySelectedResponse(cardHistoryResponses, startDate, endDate, memberCardsTotalCost, page);
    }

    @Transactional
    public List<CardHistoryResponse> createAndAutoClassifyCardHistory(String uuid, CardHistoryRequest request) {
        // 1. 우선 미분류(분류 id=1)를 기본으로 새 결제 내역 엔티티 생성
        Classification defaultClassification = classificationRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("미분류(1) Classification을 찾을 수 없습니다."));

        CardHistory history = new CardHistory(
                request.amount(),
                request.storeName(),
                request.paymentDatetime(),
                request.category(),
                request.memberCard(),  // MemberCard 정보가 DTO에 포함되어 있을 경우
                uuid,
                defaultClassification
        );
        // 저장 (미분류 상태로)
        history = cardHistoryRepository.save(history);

        // 2. 자동 분류 로직: 미분류인 경우에 Category 값에 따라 변경
        Long newClassificationId = 1L;  // 기본: 미분류
        Category category = history.getCategory();
        switch (category) {
            // 음식 관련 항목 → 분류 2
            case 음식점:
            case 배달앱:
            case 커피제과:
            case 슈퍼마켓:
            case 홈쇼핑:
            case 편의점:
                newClassificationId = 2L;
                break;
            // 의류 관련 항목 → 분류 3
            case 백화점:
            case 아울렛:
            case 할인점:
                newClassificationId = 3L;
                break;
            // 주거 관련 항목 → 분류 4
            case 관리비:
            case 공과금:
                newClassificationId = 4L;
                break;
            default:
                newClassificationId = 1L; // 해당되지 않으면 그대로 미분류 유지
                break;
        }
        // 미분류에서 다른 분류로 변경해야 한다면 업데이트 실행
        if (!newClassificationId.equals(1L)) {
            final Long targetClassificationId = newClassificationId;
            Classification targetClassification = classificationRepository.findById(newClassificationId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다. classificationId: " + targetClassificationId));
            history.setClassification(targetClassification);
            cardHistoryRepository.save(history);
        }

        // 3. 생성된 CardHistory 데이터를 CardHistoryResponse DTO로 변환하여 List로 반환
        // (여기서는 단일 내역만 반환하지만, 추후 확장이 필요한 경우 List로 감쌀 수 있습니다.)
        CardHistoryResponse response = new CardHistoryResponse(
                history.getMemberCard().getCard().getCardName(),  // 예시: 회원 카드에 연결된 카드 정보를 사용
                history.getMemberCard().getCard().getCardCorp(),
                history.getStoreName(),
                history.getAmount(),
                history.getPaymentDatetime(),
                history.getCategory(),
                history.getClassification().getTitle()  // 예시: Classification의 제목
        );
        return List.of(response);
    }

    // 결제 기록에 Classification 추가 (uuid 추가)
    @Transactional
    public CardHistory updateClassification(String uuid, Long cardHistoryId, Long classificationId) {
        CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));

        // 추가: 해당 결제 기록이 로그인한 사용자의 카드인지 검증
        if (!cardHistory.getMemberCard().getUuid().equals(uuid)) {
            throw new IllegalArgumentException("이 결제 기록은 해당 사용자의 것이 아닙니다.");
        }

        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));

        cardHistory.setClassification(classification);
        return cardHistoryRepository.save(cardHistory);
    }

    // 결제 기록에서 Classification 삭제 (uuid 추가)
    @Transactional
    public CardHistory deleteClassification(String uuid, Long cardHistoryId, Long classificationId) {
        CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));

        if (!cardHistory.getMemberCard().getUuid().equals(uuid)) {
            throw new IllegalArgumentException("이 결제 기록은 해당 사용자의 것이 아닙니다.");
        }

        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));

        if (cardHistory.getClassification() != null && cardHistory.getClassification().equals(classification)) {
            cardHistory.setClassification(null);
        } else {
            throw new IllegalArgumentException("이 결제 기록에 해당 Classification이 연결되어 있지 않습니다.");
        }

        return cardHistoryRepository.save(cardHistory);
    }

    // 분류 조건을 포함한 결제 내역 조회 (uuid 추가)
    public CardHistorySelectedResponseWithPercentResponse getSelected(String uuid, List<Long> selectedCardIds, Integer monthOffset, Long classificationId) {
        // 사용자 소유 카드 검증
        List<Long> userMemberCardIds = memberCardRepository.findByUuid(uuid)
                .stream()
                .filter(memberCard -> selectedCardIds.contains(memberCard.getId()))
                .map(memberCard -> memberCard.getId())
                .toList();


        List<CardHistory> selectedMemberCards =
                cardHistoryQueryRepository.findSelectedByMemberIdAndPeriodAndClassification(userMemberCardIds, monthOffset, classificationId);

        Integer classificationTotalCost =
                cardHistoryQueryRepository.getMemberCardsTotalAmountByClassification(userMemberCardIds, monthOffset, classificationId);

        Integer overallTotalCost =
                cardHistoryQueryRepository.getMemberCardsTotalAmount(userMemberCardIds, monthOffset);

        double percent = overallTotalCost > 0 ? (classificationTotalCost / (double) overallTotalCost) * 100 : 0;

        List<SetCardHistoriesResponse> setCardHistoriesResponses = selectedMemberCards.stream()
                .map(ch -> new SetCardHistoriesResponse(
                        ch.getId(),
                        ch.getMemberCard().getCard().getCardName(),
                        ch.getMemberCard().getCard().getCardCorp(),
                        ch.getStoreName(),
                        ch.getAmount(),
                        ch.getPaymentDatetime(),
                        ch.getCategory(),
                        ch.getClassification() != null ? ch.getClassification().getTitle() : "-"
                ))
                .collect(Collectors.toList());

        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        return new CardHistorySelectedResponseWithPercentResponse(
                setCardHistoriesResponses,
                startDate,
                endDate,
                classificationTotalCost,
                overallTotalCost,
                percent
        );
    }

    // 여러 결제 기록에 대해 분류 업데이트 (uuid 추가)
    @Transactional
    public UpdateClassificationResponse updateClassificationForSelectedCardHistories(String uuid, UpdateClassificationRequest request) {
        // 대상 분류 조회
        Classification targetClassification = classificationRepository.findById(request.classificationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));

        // 선택된 카드 히스토리들을 조회
        List<CardHistory> cardHistories = cardHistoryRepository.findAllById(request.cardHistoriesIds());

        // 각 카드히스토리가 로그인한 사용자 소유인지 검증 후 업데이트 수행
        for (CardHistory history : cardHistories) {
            if (!history.getMemberCard().getUuid().equals(uuid)) {
                throw new IllegalArgumentException("카드 히스토리 " + history.getId() + "는 해당 사용자의 것이 아닙니다.");
            }
            history.setClassification(targetClassification);
        }

        return new UpdateClassificationResponse(request.classificationId());
    }

    // 모든 분류(분석) 조회하기 (uuid 추가)
    public List<AnalyzedResponse> cardHistoriesAnalyzedByClassifications(String uuid, List<Long> selectedCardIds, int monthOffset) {
        // 사용자 소유 카드 검증
        List<Long> userMemberCardIds = memberCardRepository.findByUuid(uuid)
                .stream()
                .filter(memberCard -> selectedCardIds.contains(memberCard.getId()))
                .map(memberCard -> memberCard.getId())
                .toList();

        int totalAmountBySelectedCards = cardHistoryQueryRepository.getMemberCardsTotalAmount(userMemberCardIds, monthOffset);

        Map<Long, Integer> amountsByClassifications = cardHistoryQueryRepository.getAmountsByClassifications(userMemberCardIds, monthOffset);

        Map<Long, String> titlesByClassifications = cardHistoryQueryRepository.getTitleByClassifications(userMemberCardIds, monthOffset);

        return amountsByClassifications.entrySet().stream()
                .map(entry -> {
                    Long classificationId = entry.getKey();
                    Integer amountByClassification = entry.getValue();
                    double percent = totalAmountBySelectedCards > 0 ? (amountByClassification / (double) totalAmountBySelectedCards) * 100 : 0;
                    String title = titlesByClassifications.get(classificationId);
                    return new AnalyzedResponse(classificationId, title, percent);
                })
                .collect(Collectors.toList());
    }
}
