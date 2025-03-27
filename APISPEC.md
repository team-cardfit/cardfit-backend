### **CardController API 스펙**

- **`/cards`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**: 없음  
  - **응답**: 카드 목록 (`List<CardResponse>`)

- **`/cards/{cardId}`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**: `cardId`: 카드 ID (`Long`)  
  - **응답**: 카드 상세 정보 (`CardDetailResponse`)

- **`/cards/recommend`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**:  
    - `minAnnualFee`: 최소 연회비 (`int`)  
    - `maxAnnualFee`: 최대 연회비 (`int`)  
    - `storeCategories`: 상점 카테고리 목록 (`Set<Category>`)  
  - **응답**: 추천 카드 목록 (`List<long[]>`)

| **엔드포인트**                | **HTTP 메서드** | **요청 파라미터**                                         | **응답**                                  |
|--------------------------|-----------------|-------------------------------------------------------|----------------------------------------|
| `/cards`                 | `GET`           | 없음                                                  | 카드 목록 (`List<CardResponse>`)       |
| `/cards/{cardId}`        | `GET`           | `cardId`: 카드 ID (`Long`)                              | 카드 상세 정보 (`CardDetailResponse`)  |
| `/cards/recommend`       | `GET`           | `minAnnualFee`: 최소 연회비 (`int`),<br> `maxAnnualFee`: 최대 연회비 (`int`),<br> `storeCategories`: 상점 카테고리 목록 (`Set<Category>`) | 추천 카드 목록 (`List<long[]>`)        |

---

### **CardHistoryController API 스펙**

- **`/membercardhistories/{uuid}/selected`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**:  
    - `uuid`: 사용자 UUID (`String`)  
    - `memberCardIds`: 카드 ID 목록 (`List<Long>`, 선택)  
    - `monthOffset`: 월 오프셋 (`Integer`, 선택)  
    - `page`: 페이지 번호 (`int`, 기본값: 1)  
    - `size`: 페이지 크기 (`int`, 기본값: 13)  
  - **응답**: 사용 내역 조회 결과 (`FindAllResponse`)

- **`/cardhistory/{cardHistoryId}/classification/{classificationId}`**  
  - **HTTP 메서드**: `PATCH`  
  - **요청 파라미터**:  
    - `cardHistoryId`: 결제 기록 ID (`Long`)  
    - `classificationId`: 분류 ID (`Long`)  
  - **응답**: 업데이트된 결제 기록 정보 (`CardHistoryWithClassificationResponse`)

- **`/cardhistory/{cardHistoryId}/classification/{classificationId}`**  
  - **HTTP 메서드**: `DELETE`  
  - **요청 파라미터**:  
    - `cardHistoryId`: 결제 기록 ID (`Long`)  
    - `classificationId`: 분류 ID (`Long`)  
  - **응답**: 삭제된 결제 기록 정보 (`CardHistoryWithClassificationResponse`)

- **`/cardhistory/classification`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**:  
    - `uuid`: 사용자 UUID (`String`)  
    - `memberCardIds`: 카드 ID 목록 (`List<Long>`)  
    - `monthOffset`: 월 오프셋 (`Integer`, 선택)  
    - `classificationIds`: 분류 ID 목록 (`List<Long>`)  
  - **응답**: 결제 금액 계산 결과 (`CardHistoryResultResponse`)

| **엔드포인트**                    | **HTTP 메서드** | **요청 파라미터**                                                                 | **응답**                                      |
|------------------------------|-----------------|---------------------------------------------------------------------------|--------------------------------------------|
| `/membercardhistories/{uuid}/selected` | `GET`           | `uuid`: 사용자 UUID (`String`),<br> `memberCardIds`: 카드 ID 목록 (`List<Long>`, 선택),<br> `monthOffset`: 월 오프셋 (`Integer`, 선택),<br> `page`: 페이지 번호 (`int`, 기본값: 1),<br> `size`: 페이지 크기 (`int`, 기본값: 13) | 사용 내역 조회 결과 (`FindAllResponse`)       |
| `/cardhistory/{cardHistoryId}/classification/{classificationId}` | `PATCH`         | `cardHistoryId`: 결제 기록 ID (`Long`),<br> `classificationId`: 분류 ID (`Long`) | 업데이트된 결제 기록 정보 (`CardHistoryWithClassificationResponse`) |
| `/cardhistory/{cardHistoryId}/classification/{classificationId}` | `DELETE`        | `cardHistoryId`: 결제 기록 ID (`Long`),<br> `classificationId`: 분류 ID (`Long`) | 삭제된 결제 기록 정보 (`CardHistoryWithClassificationResponse`) |
| `/cardhistory/classification`         | `GET`           | `uuid`: 사용자 UUID (`String`),<br> `memberCardIds`: 카드 ID 목록 (`List<Long>`),<br> `monthOffset`: 월 오프셋 (`Integer`, 선택),<br> `classificationIds`: 분류 ID 목록 (`List<Long>`) | 결제 금액 계산 결과 (`CardHistoryResultResponse`)  |

---

### **ClassificationController API 스펙**

- **`/classifications`**  
  - **HTTP 메서드**: `POST`  
  - **요청 파라미터**: `request`: 생성할 분류 정보 (`CreateClassificationRequest`)  
  - **응답**: 생성된 분류 ID (`Map<String, Long>`)

- **`/classifications`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**: 없음  
  - **응답**: 분류 목록 (`List<ClassificationResponse>`)

- **`/classifications/{classificationId}`**  
  - **HTTP 메서드**: `DELETE`  
  - **요청 파라미터**: `classificationId`: 분류 ID (`Long`)  
  - **응답**: 없음 (삭제 후 응답 없음)

| **엔드포인트**              | **HTTP 메서드** | **요청 파라미터**                    | **응답**                                        |
|------------------------|-----------------|----------------------------------|----------------------------------------------|
| `/classifications`      | `POST`          | `request`: 생성할 분류 정보 (`CreateClassificationRequest`) | 생성된 분류 ID (`Map<String, Long>`)           |
| `/classifications`      | `GET`           | 없음                             | 분류 목록 (`List<ClassificationResponse>`)    |
| `/classifications/{classificationId}` | `DELETE`        | `classificationId`: 분류 ID (`Long`) | 없음 (삭제 후 응답 없음)                     |

---

### **MemberCardController API 스펙**

- **`/membercard/{uuid}`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**: `uuid`: 사용자 UUID (`String`)  
  - **응답**: 카드 목록 (`List<CardBasicInfoResponse>`)

- **`/api/cards/select`**  
  - **HTTP 메서드**: `POST`  
  - **요청 파라미터**: `memberCardIds`: 선택된 카드 ID 목록 (`List<Long>`)  
  - **응답**: 선택된 카드 목록 (`List<CardBasicInfoResponse>`)

- **`/membercard/cards/history`**  
  - **HTTP 메서드**: `GET`  
  - **요청 파라미터**:  
    - `memberCardIds`: 카드 ID 목록 (`List<Long>`)  
    - `month`: 월 (`int`)  
  - **응답**: 카드 결제 내역 (`List<DailyCardHistoryResponse>`)

| **엔드포인트**                       | **HTTP 메서드** | **요청 파라미터**                                          | **응답**                                          |
|---------------------------------|-----------------|------------------------------------------------------|-----------------------------------------------|
| `/membercard/{uuid}`            | `GET`           | `uuid`: 사용자 UUID (`String`)                             | 카드 목록 (`List<CardBasicInfoResponse>`)         |
| `/api/cards/select`             | `POST`          | `memberCardIds`: 선택된 카드 ID 목록 (`List<Long>`)           | 선택된 카드 목록 (`List<CardBasicInfoResponse>`)   |
| `/membercard/cards/history`     | `GET`           | `memberCardIds`: 카드 ID 목록 (`List<Long>`),<br> `month`: 월 (`int`) | 카드 결제 내역 (`List<DailyCardHistoryResponse>`)   |
