import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpoonacularAPITest {
    static Map<String, String> apikey = new HashMap<>();

    @BeforeAll
    static void setUp() {
        RestAssured.filters(new AllureRestAssured());
        apikey.put("apiKey", "3cf53b00bbd84081b6d72bc0c99d25f9");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void complexSearchAuth() {
        Props.recipeID1 = given()
                .queryParams(apikey)
                .expect()
                .body("totalResults", equalTo(5226))
                .when()
                .get(Props.url + "complexSearch")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getInt("results[8].id");
    }

    @Test
    @Order(2)
    void searchByRecipeIDAuth() {
        Props.recipe1Title = given()
                .params("includeNutrition", "false")
                .queryParams(apikey)
                .expect()
                .body("id", equalTo(Props.recipeID1))
                .when()
                .get(Props.url + Props.recipeID1 + "/information")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getString("title");
    }

    @Test
    @Order(3)
    void getSimilarForRecipeID1() {
        Props.recipeID2 = given()
                .queryParams(apikey)
                .expect()
                .body("[2].title", equalTo("Slow Cooker Beef Stew"))
                .when()
                .get(Props.url + Props.recipeID1 + "/similar")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getInt("[6].id");
    }

    @Test
    void answerByTitle() {
        String result = given()
                .params("q","how much calories in" + Props.recipe1Title)
                .queryParams(apikey)
                .when()
                .get(Props.url + "quickAnswer")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("type");
        assertThat(result, equalTo("NUTRITION"));
    }

    @Test
    void getRecipeInformationBulk() {
        String result = given()
                .params("ids", Props.recipeID1 + "," + Props.recipeID2)
                .params("includeNutrition", "false")
                .queryParams(apikey)
                .when()
                .get(Props.url + "informationBulk")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("[1].title");
        assertThat(result, equalTo("Slow Cooker Beef Stew"));
    }

    @Test
    void convertAmounts() {
        String result = given()
                .params("ingredientName", "flour")
                .params("sourceAmount", 2.5)
                .params("sourceUnit", "cups")
                .params("targetUnit", "grams")
                .queryParams(apikey)
                .when()
                .get(Props.url + "convert")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("type");
        assertThat(result, equalTo("CONVERSION"));
    }

    @Test
    void nutritionWidget() {
        String result = given()
                .queryParams(apikey)
                .when()
                .get(Props.url + Props.recipeID2 + "/nutritionWidget.json")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("good[22].title");
        assertThat(result, equalTo("Vitamin D"));
    }

    @Test
    void getAnalyzedRecipeInstructions() {
        String result = given()
                .params("stepBreakdown", "true")
                .queryParams(apikey)
                .when()
                .get(Props.url + Props.recipeID2 + "/analyzedInstructions")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("[0].steps[7].ingredients[0].localizedName");
        assertThat(result, equalTo("bacon"));
    }

    @Test
    void equipmentWidget() {
        given()
                .queryParams(apikey)
                .expect()
                .body("equipment[0].name", equalTo("slow cooker"))
                .when()
                .get(Props.url + Props.recipeID1 + "/equipmentWidget.json")
                .then()
                .statusCode(200);
    }

    @Test
    void getRandomRecipes() {
        given()
                .params("number", 3)
                .queryParams(apikey)
                .expect()
                .body("recipes[1].imageType", equalTo("jpg"))
                .when()
                .get(Props.url + "random")
                .then()
                .statusCode(200);
    }
}
