package user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Epic("Диплом. Тестирование API.")
@DisplayName("Авторизация пользователя")
public class LoginUserTests {

    private UserApi userApi;
    private User user;
    private String accessToken;

    @Before
    @Step("Подготовка данных пользователя и получение токена")
    public void setUp() {
        userApi = new UserApi();
        user = User.getUser();
        accessToken = userApi.getToken(user);
    }

    @After
    @Step("Удаление пользователя")
    public void cleanUp() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }


    @Test
    @DisplayName("Вход существующего пользователя")
    @Description("Успешный вход для пользователя с валидными данными")
    public void existUserLoginTest() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        accessToken = userApi.validateLoginResponse(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Невозможно осуществить вход в неверным паролем")
    public void wrongPasswordLoginTest() {
        User testUser = new User(user.getEmail(), "123");
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginIncorrectPasswordResponse(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным email")
    @Description("Невозможно осуществить вход в неверным email")
    public void wrongEmailLoginTest() {
        User testUser = new User("sejh43jh", user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginIncorrectEmailResponse(loginResponse);
    }
}
