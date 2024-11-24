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

public class CreateOrderTests {

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
    @DisplayName("Создание заказа авторизованным пользователем")
    public void createOrderWithAuth() {
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
    }

    @Test
    @DisplayName("Создание заказа пользователем без авторизации")
    public void createOrderWithoutAuth() {
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithoutAuth(order);
        orderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов авторизованным пользователем")
    public void createOrderWithoutIngredients() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(user);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(null);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов авторизованным пользователем")
    public void createOrderWithWrongHashIngredient() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(user);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        ingredients.set(0, "12345");
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}