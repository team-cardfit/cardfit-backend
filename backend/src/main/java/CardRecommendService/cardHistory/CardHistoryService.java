package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.Classification.ClassificationRepository;
import CardRecommendService.cardHistory.UpdateClassificationDto.UpdateClassificationRequest;
import CardRecommendService.cardHistory.UpdateClassificationDto.UpdateClassificationResponse;
import CardRecommendService.cardHistory.cardHistoryDto.*;
import CardRecommendService.memberCard.MemberCard;
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
    public CardHistoryResponse createAndAutoClassifyCardHistory(String uuid, CardHistoryRequest req) {
        // 1) 로그인한 사용자의 MemberCard 목록을 한 번에 조회
        List<MemberCard> userCards = memberCardRepository.findByUuid(uuid);

        // 2) 요청된 memberCardId가 이 목록에 포함되어 있는지 검증
        MemberCard memberCard = userCards.stream()
                .filter(c -> c.getId().equals(req.memberCardId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 회원카드를 찾을 수 없거나, 본인 소유가 아닙니다. id: " + req.memberCardId()
                ));

        // 3) 기본 분류(미분류 = 1) 로 조회
//        Classification defaultClassification = classificationRepository.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("미분류(1) Classification을 찾을 수 없습니다."));

        // 4) CardHistory 엔티티 생성 및 저장 (미분류 상태)
        CardHistory history = new CardHistory(
                req.amount(),
                req.storeName(),
                req.paymentDatetime(),
                req.category(),
                memberCard,
                null
        );
        history = cardHistoryRepository.save(history);

        // 5) 자동 분류 로직 (카테고리 → 분류 id)
        Long newClassificationId = switch (history.getCategory()) {
            case 음식점, 배달앱, 커피제과, 슈퍼마켓, 홈쇼핑, 편의점 -> 2L;
            case 백화점, 아울렛, 할인점 -> 3L;
            case 관리비, 공과금 -> 4L;
            default -> 1L;
        };
        if (!newClassificationId.equals(1L)) {
            Classification target = classificationRepository.findById(newClassificationId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "해당 분류를 찾을 수 없습니다. id: " + newClassificationId
                    ));
            history.setClassification(target);
            history = cardHistoryRepository.save(history);
        }

        // 6) 결과 DTO 반환
        return new CardHistoryResponse(
                memberCard.getCard().getCardName(),
                memberCard.getCard().getCardCorp(),
                history.getStoreName(),
                history.getAmount(),
                history.getPaymentDatetime(),
                history.getCategory(),
                history.getClassification().getTitle()
        );
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
