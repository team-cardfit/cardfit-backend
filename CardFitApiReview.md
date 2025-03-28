# CardFit 서비스 코드 해설 보고서

본 보고서는 CardFit 서비스의 각 기능별 API와 서비스를 컨트롤러 및 서비스 레이어 단위로 구분하여 상세하게 설명합니다.  
각 API는 클라이언트의 요청에 따라 데이터를 조회, 변환, 가공하여 필요한 정보를 반환하며, 복잡한 기능에는 그 의도를 상세히 기술하였습니다.

---

## 1. 카드 API

카드 API는 사용자가 보유한 카드의 정보와 관련 혜택, 추천 정보를 제공하는 기능을 담당합니다.

### 1.1 카드 리스트 조회 API

**컨트롤러 (CardController.java)**

```java name=CardController.java
@GetMapping("/cards")
public List<CardResponse> getAllCards() {
    return cardService.getAllCards();
}
```

- **의도**:  
  클라이언트가 `/cards` 경로로 요청을 보내면, 전체 카드 목록을 조회하여 각 카드의 발급사, 카드 이름, 연회비, 혜택 정보를 포함한 응답 객체(List<CardResponse>)를 반환합니다.

**서비스 (CardService.java)**

```java name=CardService.java
@Transactional
public List<CardResponse> getAllCards() {
    List<Card> cards = cardRepository.findAll();

    return cards.stream()
            .map(card -> new CardResponse(
                    card.getCardCorp(),         // 카드 발급사
                    card.getCardName(),         // 카드 이름
                    card.getAnnualFee(),        // 연회비
                    card.getCardBenefits().stream()
                            .map(cardBenefits -> new CardBenefitsResponse(
                                    cardBenefits.getBnfName(),   // 혜택 이름
                                    cardBenefits.getBnfDetail(), // 혜택 상세 설명
                                    cardBenefits.getBngDetail()  // 추가 혜택 설명
                            ))
                            .collect(Collectors.toList()) // 카드 혜택 목록 변환
            ))
            .collect(Collectors.toList());
}
```

- **세부 의도**:
    1. 데이터베이스로부터 모든 카드 엔티티를 조회합니다.
    2. 각 카드 엔티티의 정보를 DTO인 `CardResponse`로 변환합니다.
    3. 카드의 혜택 정보(`CardBenefits`)도 변환 대상이며, `CardBenefitsResponse` 객체로 매핑하여 리스트에 포함시킵니다.
    4. 최종 결과로 전체 카드 정보를 클라이언트에 반환합니다.

---

### 1.2 카드 상세 조회 API

**컨트롤러 (CardController.java)**

```java name=CardController.java
@GetMapping("/cards/{cardId}")
public CardDetailResponse getCardDetailByCardId(@PathVariable Long cardId) {
    return cardService.getCardDetailByCardId(cardId);
}
```

- **의도**:  
  URL의 `{cardId}`를 이용하여 특정 카드의 상세 정보를 요청받고, 이를 서비스 레이어에 전달하여 상세 정보를 조회합니다.

**서비스 (CardService.java)**

```java name=CardService.java
public CardDetailResponse getCardDetailByCardId(Long cardId) {
    Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new IllegalArgumentException("없는 카드"));

    List<CardBenefitsResponse> cardBenefitsResponses = card.getCardBenefits().stream()
            .map(cardBenefits -> new CardBenefitsResponse(
                    cardBenefits.getBnfName(),
                    cardBenefits.getBnfDetail(),
                    cardBenefits.getBngDetail()))
            .collect(Collectors.toList());

    return new CardDetailResponse(
            card.getCardCorp(),
            card.getCardName(),
            card.getAnnualFee(),
            cardBenefitsResponses
    );
}
```

- **세부 의도**:
    1. 전달받은 카드 ID로 데이터베이스에서 해당 카드 엔티티를 조회합니다.
    2. 카드가 존재하지 않을 경우 명확한 에러 메시지를 포함한 예외가 발생합니다.
    3. 카드와 관련된 혜택들을 DTO(`CardBenefitsResponse`)로 변환하여, 상세 정보 DTO(`CardDetailResponse`)에 포함합니다.
    4. 최종적으로 상세 정보를 클라이언트에 반환합니다.

---

### 1.3 카드 추천 API

**컨트롤러 (CardController.java)**

```java name=CardController.java
@GetMapping("/cards/recommend")
public List<long[]> getRecommendCards(
        @RequestParam int minAnnualFee,
        @RequestParam int maxAnnualFee,
        @RequestParam Set<Category> storeCategories) {
    return cardService.getRecommendCards(storeCategories, minAnnualFee, maxAnnualFee);
}
```

- **의도**:  
  사용자가 관심 있는 연회비 범위와 상점 카테고리를 지정해 요청 시, 추천 알고리즘에 의해 추천 점수가 높은 카드 4개의 카드 ID와 해당 점수를 배열(`long[]`) 형태로 반환합니다.

**서비스 (CardService.java)**

```java name=CardService.java
public List<long[]> getRecommendCards(Set<Category> selectedCategories, int minAnnualFee, int maxAnnualFee) {
    // 연회비 필터링을 적용하여 카드 목록 조회
    List<Card> filteredCards = cardRepository.findByAnnualFeeBetween(minAnnualFee, maxAnnualFee);

    // 각 카드에 대해 선택된 카테고리와 일치하는 개수를 계산한 후 추천 점수와 함께 배열 생성
    List<long[]> matchedCards = filteredCards.stream()
            .map(card -> new long[]{card.getId(), countMatchedCategories(card, selectedCategories)})
            .sorted((a, b) -> Long.compare(b[1], a[1])) // 추천 점수를 기준으로 내림차순 정렬
            .limit(4) // 상위 4개 카드만 반환
            .collect(Collectors.toList());

    return matchedCards;
}

// 선택된 카테고리와 카드의 카테고리 일치 개수 계산
private int countMatchedCategories(Card card, Set<Category> selectedCategories) {
    Set<Category> cardCategories = getCardCategories(card);
    return (int) cardCategories.stream()
            .filter(selectedCategories::contains)
            .count();
}

// 카드 엔티티에서 store1, store2, store3 필드를 통해 카테고리 정보 추출
private Set<Category> getCardCategories(Card card) {
    return Stream.of(card.getStore1(), card.getStore2(), card.getStore3())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
}
```

- **세부 의도**:
    1. 사용자가 지정한 연회비 범위 내의 카드들을 조회합니다.
    2. 각각의 카드에 대해 카드가 속한 카테고리(여러 매장 정보)와 사용자가 선택한 카테고리의 일치 개수를 계산합니다.
    3. 계산된 추천 점수를 기준으로 내림차순 정렬 후 상위 4개의 카드를 추천 목록에 포함합니다.
    4. 추천 결과는 각 카드의 ID와 매칭 점수가 포함된 배열 형태로 클라이언트에 반환됩니다.

---

## 2. 카드 기록 API

카드 기록 API는 사용자의 결제 내역 조회, 결제 기록에 분류(Classification) 추가/삭제, 그리고 결제 통계 계산 기능을 제공합니다.  
특히, 결제 통계 계산 기능은 사용자의 전체 결제 금액 대비 특정 분류에 해당하는 결제 비율을 산출하는 등 분석 목적에 특화되어 있습니다.

### 2.1 결제 내역 조회 API

**컨트롤러 (CardHistoryController.java)**

```java name=CardHistoryController.java
@PostMapping("/cardhistories/selected")
public CardHistorySelectedResponse getSelectedMemberCards(
        @RequestBody CardHistorySelectedRequest selectedRequest,
        @RequestParam(required = false, defaultValue = "1") Integer monthOffset,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "13") int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return cardHistoryService.getSelected(selectedRequest, monthOffset, pageable);
}
```

- **의도**:  
  특정 사용자의 카드 결제 내역을 월별 기간에 따라 조회하되, 페이징 처리와 함께 전체 결제액 정보도 함께 반환합니다.

**서비스 (CardHistoryService.java)**

```java name=CardHistoryService.java
public CardHistorySelectedResponse getSelected(CardHistorySelectedRequest selectedRequest, Integer monthOffset, Pageable pageable) {
    // 월별 기간에 따른 결제 내역 페이징 조회
    Page<CardHistory> selectedMemberCards = cardHistoryQueryRepository.findSelectedByMemberIdAndPeriod(
            selectedRequest.uuid(), selectedRequest.memberCardId(), monthOffset, pageable);

    // 해당 기간 내 총 결제 금액 계산
    Integer memberCardsTotalCost = cardHistoryQueryRepository.getMemberCardsTotalAmount(
            selectedRequest.uuid(), selectedRequest.memberCardId(), monthOffset);

    // 결제 기록을 DTO로 변환
    List<CardHistoryResponse> cardHistoryResponses = selectedMemberCards.getContent()
            .stream()
            .map(selectedMemberCard -> new CardHistoryResponse(
                    selectedMemberCard.getMemberCard().getCard().getCardName(),
                    selectedMemberCard.getMemberCard().getCard().getCardCorp(),
                    selectedMemberCard.getStoreName(),
                    selectedMemberCard.getAmount(),
                    selectedMemberCard.getPaymentDatetime(),
                    selectedMemberCard.getCategory(),
                    selectedMemberCard.getClassification() != null ? 
                        selectedMemberCard.getClassification().getTitle() : "-"
            ))
            .toList();

    Paging page = new Paging(
            selectedMemberCards.getNumber() + 1,
            selectedMemberCards.getSize(),
            selectedMemberCards.getTotalPages(),
            selectedMemberCards.getTotalElements()
    );

    return new CardHistorySelectedResponse(cardHistoryResponses, memberCardsTotalCost, page);
}
```

- **세부 의도**:
    1. 사용자의 카드 결제 내역을 월별 기준으로 페이징 조회합니다.
    2. 전체 결제액을 별도로 집계하여 결제 내역과 함께 반환합니다.
    3. 각 결제 기록을 DTO로 변환하여, 클라이언트가 읽기 쉬운 형태로 데이터를 제공합니다.

---

### 2.2 결제 기록 분류 추가/삭제 API

#### 분류 추가 API

**컨트롤러 (CardHistoryController.java)**

```java name=CardHistoryController.java
@PatchMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
public CardHistoryWithClassificationResponse updateClassification(
        @PathVariable Long cardHistoryId,
        @PathVariable Long classificationId) {
    CardHistory updatedHistory = cardHistoryService.updateClassification(cardHistoryId, classificationId);
    return new CardHistoryWithClassificationResponse(updatedHistory);
}
```

**서비스 (CardHistoryService.java)**

```java name=CardHistoryService.java
@Transactional
public CardHistory updateClassification(Long cardHistoryId, Long classificationId) {
    // 카드 결제 기록과 분류 엔티티를 조회하고,
    // 해당 결제 기록에 새로운 분류를 추가하여 저장합니다.
    CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
            .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));
    Classification classification = classificationRepository.findById(classificationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));
    cardHistory.setClassification(classification);
    return cardHistoryRepository.save(cardHistory);
}
```

- **세부 의도**:
    1. 사용자가 지정한 결제 기록에 분류를 설정하여, 이후 분석이나 분류 기반 필터링에 활용할 수 있도록 합니다.

#### 분류 삭제 API

**컨트롤러 (CardHistoryController.java)**

```java name=CardHistoryController.java
@DeleteMapping("/cardhistories/{cardHistoryId}/classification/{classificationId}")
public CardHistoryWithClassificationResponse deleteClassification(
        @PathVariable Long cardHistoryId,
        @PathVariable Long classificationId) {
    CardHistory updatedHistory = cardHistoryService.deleteClassification(cardHistoryId, classificationId);
    return new CardHistoryWithClassificationResponse(updatedHistory);
}
```

**서비스 (CardHistoryService.java)**

```java name=CardHistoryService.java
@Transactional
public CardHistory deleteClassification(Long cardHistoryId, Long classificationId) {
    // 결제 기록에서 해당 분류 정보를 제거하고 저장합니다.
    CardHistory cardHistory = cardHistoryRepository.findById(cardHistoryId)
            .orElseThrow(() -> new IllegalArgumentException("결제 기록을 찾을 수 없습니다."));
    Classification classification = classificationRepository.findById(classificationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));
    if (cardHistory.getClassification() != null && cardHistory.getClassification().equals(classification)) {
        cardHistory.setClassification(null);
    } else {
        throw new IllegalArgumentException("이 결제 기록에 해당 Classification이 연결되어 있지 않습니다.");
    }
    return cardHistoryRepository.save(cardHistory);
}
```

- **세부 의도**:
    1. 잘못 지정되었거나 불필요한 분류 정보를 결제 기록에서 제거하여 데이터의 정확성을 유지합니다.

---

### 2.3 결제 통계 및 분석 API

**컨트롤러 (CardHistoryController.java)**

```java name=CardHistoryController.java
@GetMapping("/cardhistories/classification")
public CardHistoryResultPageResponse calculatePayments(
        @PathVariable String uuid,
        @RequestParam List<Long> memberCardIds,
        @RequestParam(required = false) Integer monthOffset,
        @RequestParam List<Long> classificationIds,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "13") int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return cardHistoryService.calculateClassificationPayments(uuid, memberCardIds, monthOffset, classificationIds, pageable);
}
```

**서비스 (CardHistoryService.java)**

```java name=CardHistoryService.java
@Transactional
public CardHistoryResultPageResponse calculateClassificationPayments(
        String uuid, List<Long> memberCardIds, Integer monthOffset,
        List<Long> classificationIds, Pageable pageable) {

    // 1. 해당 사용자의 전체 결제 금액 계산
    Integer totalAmount = cardHistoryQueryRepository.getMemberCardsTotalAmount(uuid, memberCardIds, monthOffset);

    // 2. 특정 분류에 해당하는 결제 기록 조회
    Page<CardHistory> cardHistories = cardHistoryRepository.findByClassificationIdIn(classificationIds, pageable);

    double selectedAmount = 0;
    List<CardHistoryResponse> filteredCardHistories = new ArrayList<>();

    // 3. 분류 기준으로 필터링하면서 선택된 결제 금액 누적 및 DTO 변환
    for (CardHistory history : cardHistories) {
        if (classificationIds.contains(history.getClassification().getId())) {
            filteredCardHistories.add(new CardHistoryResponse(
                    history.getMemberCard().getCard().getCardName(),
                    history.getMemberCard().getCard().getCardCorp(),
                    history.getStoreName(),
                    history.getAmount(),
                    history.getPaymentDatetime(),
                    history.getCategory(),
                    history.getClassification() != null ? history.getClassification().getTitle() : "-"
            ));
            selectedAmount += history.getAmount();
        }
    }

    Paging paging = new Paging(
            cardHistories.getNumber(),
            cardHistories.getSize(),
            cardHistories.getTotalPages(),
            cardHistories.getTotalElements()
    );

    // 4. 전체 결제 금액 대비 선택된 금액의 비율 산출 (소수점 2자리까지 반올림)
    double percentage = totalAmount > 0 ? (selectedAmount / totalAmount) * 100 : 0;
    BigDecimal percentageDecimal = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);

    return new CardHistoryResultPageResponse(
            new CardHistoryResultResponse(filteredCardHistories, totalAmount, selectedAmount, percentageDecimal.doubleValue()),
            paging
    );
}
```

- **세부 의도**:
    1. 사용자가 가진 전체 결제 금액 대비 지정된 분류에 해당하는 결제 금액의 비율을 산출합니다.
    2. 이 과정을 위해 전체 금액과 선택된 금액을 별도로 계산 및 누적 처리하고, DTO로 변환합니다.
    3. 계산된 비율(백분율)과 페이징 정보를 포함한 분석 결과를 클라이언트에 반환하여 데이터 분석 및 시각화에 활용할 수 있게 합니다.

---

## 3. 분류 관리 API

분류 관리 API는 결제 기록을 분류(Classification)로 관리하여 데이터를 보다 체계적인 방식으로 분석할 수 있게 합니다.

### 3.1 분류 생성, 조회, 삭제

**컨트롤러 (ClassificationController.java)**

```java name=ClassificationController.java
@RestController
public class ClassificationController {

    private ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    // 분류 생성
    @PostMapping("/classifications")
    public ResponseEntity<Map<String, Long>> createClassification(@RequestBody CreateClassificationRequest request) {
        Long classificationId = classificationService.createClassification(request);
        return ResponseEntity.ok(Map.of("id", classificationId));
    }

    // 분류 목록 조회
    @GetMapping("/classifications")
    public List<ClassificationResponse> getClassificationList() {
        return classificationService.getClassificationList();
    }

    // 분류 삭제
    @DeleteMapping("/classifications/{classificationId}")
    public void deleteClassification(@PathVariable Long classificationId) {
        classificationService.deleteClassification(classificationId);
    }
}
```

**서비스 (ClassificationService.java)**

```java name=ClassificationService.java
@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final CardHistoryRepository cardHistoryRepository;

    public ClassificationService(ClassificationRepository classificationRepository, CardHistoryRepository cardHistoryRepository) {
        this.classificationRepository = classificationRepository;
        this.cardHistoryRepository = cardHistoryRepository;
    }

    // 분류 생성
    @Transactional
    public Long createClassification(CreateClassificationRequest request) {
        Classification classification = new Classification(request.title());
        classificationRepository.save(classification);
        return classification.getId();
    }

    // 분류 목록 조회
    public List<ClassificationResponse> getClassificationList() {
        List<Classification> classifications = classificationRepository.findAll();
        return classifications.stream()
                .map(classification -> new ClassificationResponse(classification.getTitle()))
                .collect(Collectors.toList());
    }

    // 분류 삭제
    @Transactional
    public void deleteClassification(Long classificationId) {
        Classification classification = classificationRepository.findById(classificationId)
                .orElseThrow(() -> new RuntimeException("없는 분류"));
        classificationRepository.deleteById(classificationId);
    }
}
```

- **세부 의도**:
    - 사용자가 결제 기록 데이터를 원하는 범주로 분류할 수 있도록 분류 항목을 생성하고 관리합니다.
    - 분류 생성시 제목을 받아 엔티티로 저장하며, 이후 목록 조회 및 삭제를 통해 분류 정보를 유지보수할 수 있도록 합니다.

---

## 4. 회원 카드 API

회원 카드 API는 사용자의 카드 정보를 관리하여,  
① 사용자가 소지한 전체 카드 목록을 조회하고,  
② 분석 대상이 되는 특정 카드들을 선택하며,  
③ 선택된 카드들의 결제 내역(일별 그룹화) 정보를 제공하는 역할을 합니다.

### 4.1 회원 카드 목록 조회 API

**컨트롤러 (MemberCardController.java)**

```java name=MemberCardController.java
@GetMapping("/membercards/{uuid}")
public List<CardBasicInfoResponse> getAllMemberCardBasicInfo(@PathVariable String uuid) {
    return memberCardService.getAllMemberCardBasicInfoByUserId(uuid);
}
```

- **의도**:  
  사용자의 UUID를 기반으로, 해당 사용자가 소지하거나 등록한 모든 카드의 기본 정보를 조회합니다.  
  이 정보는 카드 이름, 발급사, 이미지 URL 및 기타 요약 정보를 포함하여 클라이언트에게 반환됩니다.

**서비스 (MemberCardService.java)**

```java name=MemberCardService.java
public List<CardBasicInfoResponse> getAllMemberCardBasicInfoByUserId(String uuid) {
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
            .collect(Collectors.toList());
}
```

- **세부 의도**:
    1. `memberCardRepository.findByUuid(uuid)`를 통해 특정 사용자의 카드 엔티티를 모두 조회합니다.
    2. 각 회원 카드 엔티티를 `CardBasicInfoResponse` DTO로 변환하여, 카드 이름, 발급사, 이미지 URL 등의 정보를 요약합니다.
    3. 최종적으로 모든 카드 정보를 리스트로 클라이언트에 반환합니다.

---

### 4.2 선택된 카드 목록(분석 대상 카드 조회) API

**컨트롤러 (MemberCardController.java)**

```java name=MemberCardController.java
@PostMapping("/membercards/select")
public List<CardBasicInfoResponse> selectCardsByIds(@RequestBody List<Long> memberCardIds) {
    return memberCardService.selectCardsByIds(memberCardIds);
}
```

- **의도**:  
  사용자가 선택한 카드 ID 목록을 받아, 이들에 해당하는 카드의 기본 정보를 조회합니다.  
  이를 통해 분석 대상 카드 목록을 구성하고, 후속 분석 또는 리포트 생성을 위한 자료로 사용합니다.

**서비스 (MemberCardService.java)**

```java name=MemberCardService.java
public List<CardBasicInfoResponse> selectCardsByIds(List<Long> memberCardId) {
    List<MemberCard> memberCards = memberCardRepository.findAllByIdIn(memberCardId);
    return memberCards.stream()
            .map(memberCard -> new CardBasicInfoResponse(
                    memberCard.getId(),
                    memberCard.getCard().getCardName(),
                    memberCard.getCard().getImgUrl(),
                    memberCard.getCard().getCardCorp(),
                    memberCard.getId(),
                    memberCard.getCard().getAltTxt()
            ))
            .collect(Collectors.toList());
}
```

- **세부 의도**:
    1. `memberCardRepository.findAllByIdIn(memberCardId)`를 사용하여 선택된 카드 ID에 맞는 회원 카드 데이터를 조회합니다.
    2. 각 회원 카드 데이터를 `CardBasicInfoResponse` DTO로 매핑하여, 클라이언트가 쉽게 분석할 수 있도록 간략한 카드 정보를 구성합니다.
    3. 최종적으로 이 리스트를 클라이언트에 반환합니다.

---

### 4.3 일별 결제 내역 조회 API

**컨트롤러 (MemberCardController.java)**

```java name=MemberCardController.java
@PostMapping("/membercards/daily")
public DailyCardHistoryPageResponse getCardsHistories(
        @RequestBody CardHistorySelectedRequest selectedRequest,
        @RequestParam(required = false, defaultValue = "1") int monthOffset,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "13") int size) {
    return memberCardService.getCardsHistories(selectedRequest, monthOffset, page, size);
}
```

- **의도**:  
  사용자가 요청한 카드의 결제 내역을 조회합니다.  
  결제 내역은 지정된 월(offset) 기준으로 검색되며, 결과는 일자별로 그룹화되어 각 날짜별 결제 내용과 총 결제 금액을 계산하여 클라이언트에 전달됩니다.

**서비스 (MemberCardService.java)**

```java name=MemberCardService.java
public DailyCardHistoryPageResponse getCardsHistories(CardHistorySelectedRequest selectedRequest, Integer monthOffset, int page, int size) {
    List<CardHistory> cardHistories = cardHistoryQueryRepository.oderByPaymentDateTimeAndPaging(
            selectedRequest.memberCardId(), monthOffset, page, size);
    int totalCount = cardHistoryQueryRepository.getTotalCount(selectedRequest.memberCardId(), monthOffset);

    // 각 결제 기록을 DTO로 변환
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

    // 일별 그룹화: 각 날짜별 결제 내역들을 묶고, 각 그룹의 총 결제 금액을 산출
    List<DailyCardHistoryResponse> dailyCardHistoryResponses = responses.stream()
            .collect(Collectors.groupingBy(
                    response -> response.paymentDatetime().toLocalDate(),
                    LinkedHashMap::new,
                    Collectors.toList()
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new DailyCardHistoryResponse(
                    entry.getKey(),
                    entry.getValue(),
                    entry.getValue().stream().mapToInt(CardHistoryResponse::amount).sum()
            ))
            .toList();

    Integer totalCost = cardHistoryQueryRepository.getMemberCardsTotalAmount(
            selectedRequest.uuid(), selectedRequest.memberCardId(), monthOffset);
    int totalPages = (totalCount + size - 1) / size;

    return new DailyCardHistoryPageResponse(
            dailyCardHistoryResponses, totalCost, page, totalPages, size, totalCount
    );
}
```

- **세부 의도**:
    1. `cardHistoryQueryRepository.oderByPaymentDateTimeAndPaging(...)`를 통해 선택된 카드의 결제 내역을 월별 기준으로 페이징 처리하여 조회합니다.
    2. 각 결제 내역을 `CardHistoryResponse` DTO로 변환 후, 결제 날짜별로 그룹화합니다.
    3. 그룹화된 각 날짜별로 총 결제 금액을 계산하여 `DailyCardHistoryResponse` DTO에 포함합니다.
    4. 전체 결제 금액 및 페이징 정보를 포함한 `DailyCardHistoryPageResponse` 형태로 클라이언트에 반환합니다.

---

## 결론

CardFit 서비스의 각 기능별 API와 서비스 로직은 아래와 같은 주요 목적과 의도를 가지고 설계되었습니다.

- **카드 API**:
    - 전체 카드 정보 조회, 상세 조회, 및 추천 정보를 통해 사용자가 보유한 카드와 관련 혜택을 효율적으로 확인할 수 있습니다.

- **카드 기록 API**:
    - 사용자의 결제 내역을 월별, 일별로 정리하며, 분류 추가/삭제 기능을 통해 데이터를 세분화하여 재무 분석 및 통계 자료로 활용할 수 있습니다.

- **분류 관리 API**:
    - 결제 기록의 분류 설정을 통해 데이터를 체계적으로 관리, 분류 생성/조회/삭제 기능으로 사용자의 데이터 분석 효율성을 증대시킵니다.

- **회원 카드 API**:
    - 회원의 카드 목록 조회, 선택된 카드 조회, 및 결제 내역 조회 기능을 제공하여 개인 맞춤형 데이터 제공과 후속 분석 자료로 활용됩니다.

이와 같이 각 API는 컨트롤러와 서비스 레이어가 협력하여 클린하고 유지보수하기 쉬운 구조로 구현되어 있습니다.