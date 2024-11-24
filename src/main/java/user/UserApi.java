package user;

import client.Specification;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static client.Constants.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserApi extends Specification {

    @Step("Регистрация пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_REGISTER)
                .then()
                .log().all();

    }

    @Step("Вход пользователя")
    public ValidatableResponse loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_LOGIN)
                .then()
                .log().all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .body(accessToken)
                .when()
                .delete(USER_DELETE)
                .then()
                .log().all();
    }

    @Step("Обновление данных для авторизованного пользователя")
    public ValidatableResponse updateUserWithAuth(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER_UPDATE)
                .then()
                .log().all();
    }

    @Step("Обновление данных для неавторизованного пользователя")
    public ValidatableResponse updateUserWithoutAuth(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_UPDATE)
                .then()
                .log().all();
    }

    @Step("Проверка ответа на вход существующего пользователя")
    public String validateLoginResponse(ValidatableResponse loginResponse) {
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        return loginResponse.extract().path("accessToken");
    }

    @Step("Проверка ответа на вход с неверным паролем")
    public void validateLoginIncorrectPasswordResponse(ValidatableResponse loginResponse) {
        loginResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Проверка ответа на вход с неверным email")
    public void validateLoginIncorrectEmailResponse(ValidatableResponse loginResponse) {
        // Проверка статус-кода и сообщения в теле ответа
        loginResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Проверка ответа на создание пользователя")
    public String validateCreateUserResponse(ValidatableResponse createResponse) {
        createResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        return createResponse.extract().path("accessToken");
    }

    @Step("Проверка ответа при попытке создания существующего пользователя")
    public void validateCreateExistingUserResponse(ValidatableResponse createResponse) {
        createResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Step("Проверка ответа для пользователя с не заполненным полем")
    public void validateCreateUserWithoutFieldResponse(ValidatableResponse createResponse) {
        createResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Проверка ответа на обновление данных авторизованного пользователя")
    public void validateUpdateUserResponse(ValidatableResponse updateResponse) {
        updateResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Step("Проверка ответа на обновление данных неавторизованного пользователя")
    public void validateUpdateUserWithoutAuthResponse(ValidatableResponse updateResponse) {
        updateResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}