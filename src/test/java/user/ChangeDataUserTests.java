package user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Epic("Диплом. Тестирование API.")
@DisplayName("Изменение данных пользователя")
public class ChangeDataUserTests {

    private UserApi userApi;
    private User user;
    private String accessToken;
    private String email;
    private String name;
    private String password;

    @Before
    public void setUp() {
        userApi = new UserApi();
        user = User.getUser();
        accessToken = userApi.getToken(user);
    }

    @Step("Удаление пользователя")
    @After
    public void cleanUp() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Изменение данных авторизированного пользователя")
    @Description("Данные можно изменить")
    public void changeDataUserWithAuthTest() {
        userApi.loginUser(user);
        ValidatableResponse updateResponse = userApi.updateUserWithAuth(User.getUser(), accessToken);
        userApi.validateUpdateUserResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение email для авторизованного пользователя")
    @Description("Email можно изменить")
    public void changeEmailForAuthUserTest() {
        userApi.loginUser(user);
        user.setEmail(RandomStringUtils.randomAlphabetic(8) + "@new.ru");
        ValidatableResponse updateResponse = userApi.updateUserWithAuth(User.getUser(), accessToken);
        userApi.validateUpdateUserResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение имени для авторизованного пользователя")
    @Description("Имя можно изменить")
    public void changeNameForAuthUserTest() {
        userApi.loginUser(user);
        user.setName(RandomStringUtils.randomAlphabetic(8));
        ValidatableResponse updateResponse = userApi.updateUserWithAuth(User.getUser(), accessToken);
        userApi.validateUpdateUserResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение пароля для авторизованного пользователя")
    @Description("Пароль можно изменить")
    public void changePasswordForAuthUserTest() {
        userApi.loginUser(user);
        user.setPassword(RandomStringUtils.randomAlphabetic(8));
        ValidatableResponse updateResponse = userApi.updateUserWithAuth(User.getUser(), accessToken);
        userApi.validateUpdateUserResponse(updateResponse);
    }


    @Test
    @DisplayName("Изменение данных неавторизованного пользователя")
    public void changeDataUserWithoutAuthTest() {
        ValidatableResponse updateResponse = userApi.updateUserWithoutAuth(User.getUser());
        userApi.validateUpdateUserWithoutAuthResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение email для неавторизованного пользователя")
    @Description("Email нельзя изменить")
    public void changeEmailWithoutAuthUserTest() {
        user.setEmail(RandomStringUtils.randomAlphabetic(8) + "@new.ru");
        ValidatableResponse updateResponse = userApi.updateUserWithoutAuth(User.getUser());
        userApi.validateUpdateUserWithoutAuthResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение имени для неавторизованного пользователя")
    @Description("Имя нельзя изменить")
    public void changeNameWithoutAuthUserTest() {
        userApi.loginUser(user);
        user.setName(RandomStringUtils.randomAlphabetic(8));
        ValidatableResponse updateResponse = userApi.updateUserWithoutAuth(User.getUser());
        userApi.validateUpdateUserWithoutAuthResponse(updateResponse);
    }

    @Test
    @DisplayName("Изменение пароля для неавторизованного пользователя")
    @Description("Пароль нельзя изменить")
    public void changePasswordWithoutAuthUserTest() {
        userApi.loginUser(user);
        user.setPassword(RandomStringUtils.randomAlphabetic(8));
        ValidatableResponse updateResponse = userApi.updateUserWithoutAuth(User.getUser());
        userApi.validateUpdateUserWithoutAuthResponse(updateResponse);
    }
}
