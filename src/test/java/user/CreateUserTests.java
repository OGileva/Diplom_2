package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.apache.http.HttpStatus.*;

public class CreateUserTests {

    private UserApi userApi;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        user = user.getUser();
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
    @DisplayName("Создание уникального пользователя")
    public void createNewUser() {
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = userApi.validateCreateUserResponse(createResponse);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        ValidatableResponse createResponseFirst = userApi.createUser(user);
        ValidatableResponse createResponseSecond = userApi.createUser(user);
        userApi.validateCreateExistingUserResponse(createResponseSecond);

        accessToken = createResponseFirst.extract().path("accessToken");

    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем name")
    public void createUserWithoutName() {
        user.setName(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем email")
    public void createUserWithoutEmail() {
        user.setEmail(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем password")
    public void createUserWithoutPassword() {
        user.setPassword(null);
        ValidatableResponse createResponse = userApi.createUser(user);
        userApi.validateCreateUserWithoutFieldResponse(createResponse);
    }
}