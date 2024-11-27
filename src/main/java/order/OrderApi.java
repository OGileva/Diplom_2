package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import client.Specification;

import static client.Constants.ORDER_PATH;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderApi extends Specification {

    @Step("Создание заказа авторизованным пользователем")
    public ValidatableResponse createOrderWithAuth(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Создание заказа неавторизованным пользователем")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Получение заказа авторизованным пользователем")
    public ValidatableResponse getOrderWithAuth(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Получение заказа неавторизованным пользователем")
    public ValidatableResponse getOrderWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then()
                .log().all();
    }


    @Step("Проверка ответа при создании заказа")
    public void validateCreateOrderResponse(ValidatableResponse orderResponse) {
        orderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());
    }

    @Step("Проверка ответа при создании заказа без ингредиентов")
    public void validateCreateOrderWithoutIngredientsResponse(ValidatableResponse orderResponse) {
        orderResponse.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Проверка успешного ответа при получении заказа")
    public void validateGetOrderResponse(ValidatableResponse getOrderResponse) {
        getOrderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Step("Проверка ответа при к получению заказа без авторизации")
    public void validateGetOrderResponseWithoutAuth(ValidatableResponse getOrderResponse) {
        getOrderResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
