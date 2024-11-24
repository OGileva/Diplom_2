package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.apache.http.HttpStatus.*;

public class GetOrderTests {

    private UserApi userApi;
    private OrderApi orderApi;
    private User user;
    private Order order;
    List<String> ingredients = Arrays.asList(
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa7a");
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        user = User.getUser();
        userApi.createUser(user);
        orderApi = new OrderApi();
    }

    @After
    public void tearDown() {
        try {
            userApi.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrderWithAuth() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(user);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());

        ValidatableResponse getOrderResponseWithAuth = orderApi.getOrderWithAuth(accessToken);
        getOrderResponseWithAuth.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказа неавторизованного пользователя")
    public void getOrderWithoutAuth() {
        order = new Order(ingredients);
        ValidatableResponse orderResponseWithoutAuth = orderApi.createOrderWithoutAuth(order);
        orderResponseWithoutAuth.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());

        ValidatableResponse getOrderResponseWithoutAuth = orderApi.getOrderWithoutAuth();
        getOrderResponseWithoutAuth.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
