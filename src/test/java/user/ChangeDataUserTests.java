package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.apache.http.HttpStatus.*;

public class ChangeDataUserTests {

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
    @DisplayName("Изменение данных авторизированного пользователя")
    public void changeDataUserWithAuth() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(user);
        accessToken = loginResponse.extract().path("accessToken");
        ValidatableResponse updateResponse = userApi.updateUserWithAuth(User.getUser(), accessToken);
        updateResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных неавторизированного пользователя")
    public void changeDataUserWithoutAuth() {
        ValidatableResponse updateResponse = userApi.updateUserWithoutAuth(User.getUser());
        updateResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
