import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class MyTest {
    static Map<String, String> headers = new HashMap<>();
    static Properties prop = new Properties();

    @BeforeAll
    static void setUp() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        headers.put("Authorization", "Bearer 9d236of77fa45acbbe39df15c8cf710fb9592fc");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        FileInputStream fis;
        fis = new FileInputStream("src/test/resources/my.properties");
        prop.load(fis);
    }

    @Test
    void getAccountInfoTest() {
        given().log().all() //логи запроса всегда
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/account/<username>")
                .prettyPeek() //логи ответа
                .then()
                .statusCode(403);
    }

    @Test
    void getEasy() {
        RestAssured.get("https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats")
                .then()
                .statusCode(200);
    }

    @Test
    void getStatistics() {
        given()
                .request("GET", "https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats")
                .then()
                .statusCode(200);
    }

    @Test
    void getStatistics2() {
        String result = given()
                .when()
                .get("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search?page={page}", 1)
                .then().statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("status");
        assertThat(result, equalTo("success"));
    }

    @Test
    void getStatistics3() {
        String result = given()
                .when()
                .get("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search?page={page}", 1)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("status");
        assertThat(result, equalTo("success"));
    }

    @Test
    void getStatistics4() {
        given()
                .expect()
                .body("status",equalTo("success"))
                .when()
                .get("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search?page={page}", 1)
                .then()
                .statusCode(200);
    }

    @Test
    void getStatistics5() {
        given()
                .log().all()
                .when()
                .get((String) prop.get("geturl"))
                .prettyPeek()
                .then()
                .statusCode(200).and().body("status",equalTo("success")).and().time(lessThan(5L), TimeUnit.SECONDS);
    }
}
