package CardRecommendService;

import CardRecommendService.Classification.CreateClassificationRequest;
import CardRecommendService.cardHistory.CardHistoryResultResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;


public class ApplicationTests extends AcceptanceTest {


    //카드 목록 조회
    @DisplayName("카드 목록 조회")
    @Test
    void 카드목록조회() {
        RestAssured
                .given().log().all()
                .when()
                .get("/cards")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    //카드 상세 조회
    @DisplayName("카드 상세 조회")
    @Test
    void 카드상세조회() {
        RestAssured
                .given().log().all()
                .pathParam("cardId", 1L)
                .when()
                .get("/cards/{cardId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    @DisplayName("선택한 카드 조회")
    @Test
    void 선택한카드조회_결제총액() {

        String uuid = "1";

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .pathParam("uuid", 1L)
                .queryParam("memberCardIds", 3L)
                .queryParam("monthOffset", 1)
                .get("cardhistories/{uuid}/selected")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath();
    }

    //카드 추천 로직 테스트
    @DisplayName("카드 추천 로직 테스트")
    @Test
    void 카드추천로직테스트() {
        RestAssured
                .given().log().all()
                .queryParam("minAnnualFee", 10000) // 최소 연회비
                .queryParam("maxAnnualFee", 100000) // 최대 연회비
                .queryParam("storeCategories", "항공, 온라인쇼핑, 영화, 배달앱, 보험") // 카테고리 3개 선택
                .when()
                .get("/cards/recommend")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }


    // uuid에 해당하는 사용자의 모든 카드 목록 조회
    @DisplayName("uuid에 해당하는 사용자의 모든 카드 목록 조회")
    @Test
    void uuid에사용자의모든카드목록조회() {
        RestAssured
                .given().log().all()
                .pathParam("uuid", 1L)// 카테고리 3개 선택
                .when()
                .get("/membercard/{uuid}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }


    List<Long> memberCardId = Arrays.asList(1L, 2L, 3L); // 예시로  카드 선택

    @Test
    void 카드선택() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(memberCardId)
                .when()
                .post("/api/cards/select")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }


    // 멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링
    @DisplayName("멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링")
    @Test
    void getCardsHistories() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .param("memberCardIds", "1,2,3")  // 쿼리 파라미터로 memberCardIds 추가
                .param("month", "2")
                .when()
                .get("/membercard/cards/history")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }


    // 멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링2 - 3월
    @DisplayName("멤버 카드와 결제 내역을 조회, 결제 내역을 월 단위로 필터링2 - 3월")
    @Test
    void getCardsHistories2MonthOf3() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .param("memberCardIds", "1,2,3")  // 쿼리 파라미터로 memberCardIds 추가
                .param("month", "3")
                .when()
                .get("/membercard/cards/history")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }


    // 분류 생성 테스트
    @DisplayName("분류 생성 테스트")
    @Test
    void 분류생성() {
        CreateClassificationRequest request = new CreateClassificationRequest("새로운 분류");

        RestAssured
                .given().log().all()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    // 분류 목록 조회 테스트
    @DisplayName("분류 목록 조회 테스트")
    @Test
    void 분류목록조회() {

        CreateClassificationRequest request = new CreateClassificationRequest("새로운 분류");

        RestAssured
                .given().log().all()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .when()
                .get("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    // 분류 삭제 테스트
    @DisplayName("분류 삭제 테스트")
    @Test
    void 분류삭제() {
        CreateClassificationRequest request = new CreateClassificationRequest("새로운 분류");

        RestAssured
                .given().log().all()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .when()
                .pathParam("classificationId", 1L)
                .delete("/classifications/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    //기능 1. 결제 기록에 Classification 추가.
    @DisplayName("결제 기록에 Classification 추가")
    @Test
    void 결제기록에Classification추가() {


        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("욜로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("골로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("지옥으로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Response response = RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 1)  // cardHistoryId 먼저
                .pathParam("classificationId", 3)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();  // 응답 객체를 변수로 저장

// 응답 본문을 직접 확인하고 문제를 파악
        System.out.println("응답 본문: " + response.asString());


    }

    //기능 2: 결제 기록에서 Classification 삭제
    @DisplayName("결제 기록에서 Classification 삭제")
    @Test
    void 결제기록에서Classification삭제() {

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("욜로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 1)  // cardHistoryId 먼저
                .pathParam("classificationId", 1)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .pathParam("cardHistoryId", 1)
                .pathParam("classificationId", 1)
                .when()
                .delete("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    //기능 3. 특정 Classification  로 해당 Classification 에 해당하는 결제 기록과 총 결제 금액, 퍼센테이지 표시
    @DisplayName("특정 Classification  로 해당 Classification 에 해당하는 결제 기록과 총 결제 금액, 퍼센테이지 표시")
    @Test
    void getClassificationStatistics() {

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("욜로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateClassificationRequest("골로"))
                .when()
                .post("/classifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 1)  // cardHistoryId 먼저
                .pathParam("classificationId", 1)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 2)  // cardHistoryId 먼저
                .pathParam("classificationId", 1)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 3)  // cardHistoryId 먼저
                .pathParam("classificationId", 1)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .contentType("application/json")

                .pathParam("cardHistoryId", 4)  // cardHistoryId 먼저
                .pathParam("classificationId", 2)  // classificationId 나중에
                .when()
                .patch("/cardhistories/{cardHistoryId}/classification/{classificationId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .contentType("application/json")
                .queryParam("uuid", 1l) // 예시 uuid 값
                .queryParam("memberCardIds", 1, 2, 3) // 예시 memberCardIds 값
                .queryParam("monthOffset", 1) // 예시 monthOffset 값
                .queryParam("classificationIds", 1, 2) // 예시 classificationIds 값
                .queryParam("page", 0) // 페이지 번호
                .queryParam("size", 10) // 페이지 크기
                .when()
                .get("/cardhistories/classification")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


    }
}