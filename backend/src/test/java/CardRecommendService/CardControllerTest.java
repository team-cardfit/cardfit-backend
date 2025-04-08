//package CardRecommendService;
//
//import io.restassured.RestAssured;
//import org.junit.jupiter.api.Test;
//import static org.hamcrest.Matchers.*;
//
////H2 데이터베이스 테스트코드임
//public class CardControllerTest extends AcceptanceTest {
//
//    // 테스트용 토큰 (Bearer 접두사 포함) / 유효기간 만료되어서 다시 생성해야 통과
//    private static final String TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ0MDUzMTcwLCJleHAiOjE3NDQwODE5NzB9.eAHDe66dz8RnKiUG9jl7TRam6eqq-rHefQ75e_n_6hI";
//
//    // 모든 카드 목록 조회 테스트
//    @Test
//    public void testGetAllCards() {
//        RestAssured
//                .given()
//                .header("Authorization", TOKEN)
//                .when()
//                .get("/cards")
//                .then()
//                .statusCode(200)
//                .body("size()", greaterThan(0));
//    }
//
//    // 카드 상세 조회 테스트
//    @Test
//    public void testGetCardDetail() {
//        // DataSeeder에 의해 카드 데이터가 시딩되므로, ID 1번 카드가 존재한다고 가정
//        RestAssured
//                .given()
//                .header("Authorization", TOKEN)
//                .when()
//                .get("/cards/{cardId}", 1)
//                .then()
//                .statusCode(200)
//                .body("cardName", notNullValue())
//                .body("cardCorp", notNullValue());
//    }
//
//    // 선택한 카테고리 기반 카드 추천 테스트
//    @Test
//    public void testRecommendCards() {
//        RestAssured
//                .given()
//                .header("Authorization", TOKEN)
//                .param("minAnnualFee", 0)
//                .param("maxAnnualFee", 500000)
//                // 요청 시 Enum 값은 쉼표로 구분된 문자열로 전달합니다.
//                .param("storeCategories", "할인점,스트리밍,배달앱")
//                .when()
//                .get("/cards/recommend")
//                .then()
//                .statusCode(200)
//                // 추천 카드 목록과 선택 카테고리가 응답에 포함되어 있는지 확인
//                .body("recommendedCards.size()", greaterThan(0))
//                .body("selectedCategories.size()", greaterThan(0));
//    }
//
//    // 회원 보유 카드 기반 추천 (동적 쿼리, 기본 카테고리 추출) 테스트
//    @Test
//    public void testRecommendByAmount() {
//        RestAssured
//                .given()
//                .header("Authorization", TOKEN)
//                // 여러 개의 카드 ID를 배열 형태로 전달 (DataSeeder에서 시딩된 값 활용)
//                .param("selectedCardIds", "1", "2", "3")
//                .param("minAnnualFee", 0)
//                .param("maxAnnualFee", 500000)
//                .param("monthOffset", 1)
//                .when()
//                .get("/cards/recommendation")
//                .then()
//                .statusCode(200)
//                .body("size()", greaterThan(0));
//    }
//
//    // 회원 보유 카드 + 외부 제공 카테고리 기반 추천 테스트
//    @Test
//    public void testRecommendByCategoriesAndAmount() {
//        RestAssured
//                .given()
//                .header("Authorization", TOKEN)
//                .param("selectedCardIds", "1", "2", "3")
//                // 제공할 카테고리를 쉼표로 구분하여 전달 (예: "온라인쇼핑", "음식점", "편의점")
//                .param("categories", "온라인쇼핑", "음식점", "편의점")
//                .param("minAnnualFee", 0)
//                .param("maxAnnualFee", 500000)
//                .param("monthOffset", 1)
//                .when()
//                .get("/cards/recommendations")
//                .then()
//                .statusCode(200)
//                .body("size()", greaterThan(0));
//    }
//}
