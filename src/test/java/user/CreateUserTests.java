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
@DisplayName("Создание пользователя")
public class CreateUserTests {

    private UserApi userApi;
    private User user;
    private String accessToken;

    @Step("Подготовка данных пользователя")
    @Before
    public void setUp() {
        userApi = new UserApi();
        user = user.getUser();
    }

    @Step("Удаление пользователя")
    @After
    public void cleanUp() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Пользователя можно создать")
    public void createNewUserTest() {
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserResponse(createResponse);
        accessToken = createResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Нельзя создать двух одинаковых пользователей")
    public void createExistingUserTest() {
        ValidatableResponse createResponseFirst = userApi.createUser(user);
        ValidatableResponse createResponseSecond = userApi.createUser(user);
        userApi.validateCreateExistingUserResponse(createResponseSecond);
        accessToken = createResponseFirst.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем name")
    @Description("Нельзя создать пользователя без поля имя")
    public void createUserWithoutNameTest() {
        user.setName(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем email")
    @Description("Нельзя создать пользователя без поля email")
    public void createUserWithoutEmailTest() {
        user.setEmail(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем password")
    @Description("Нельзя создать пользователя без поля пароль")
    public void createUserWithoutPasswordTest() {
        user.setPassword(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }
}