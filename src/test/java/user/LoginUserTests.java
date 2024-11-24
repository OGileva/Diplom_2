package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.apache.http.HttpStatus.*;

public class LoginUserTests {

    private UserApi userApi;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        user = User.getUser();
        userApi.createUser(user);
    }

    @After
    public void cleanUp() {
        try {
            userApi.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Вход существующего пользователя")
    public void loginExistingUser() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        accessToken = userApi.validateLoginResponse(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void loginWrongPassword() {
        User testUser = new User(user.getEmail(), "123");
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginIncorrectPasswordResponse(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void loginWrongEmail() {
        User testUser = new User("sejh43jh", user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
       userApi.validateLoginIncorrectEmailResponse(loginResponse);
    }
}
