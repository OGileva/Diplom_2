package order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;

@Epic("Диплом. Тестирование API.")
@DisplayName("Создание заказа")
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
    @Step("Подготовка данных пользователя")
    public void setUp() {
        userApi = new UserApi();
        user = User.getUser();
        accessToken = userApi.getToken(user);
        orderApi = new OrderApi();
    }

    @After
    @Step("Удаление пользователя")
    public void cleanUp() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Заказ можно создать")
    public void createOrderWithAuthTest() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginResponse(loginResponse);

        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderApi.validateCreateOrderResponse(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа пользователем без авторизации")
    @Description("Заказ можно создать")
    public void createOrderWithoutAuthTest() {
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithoutAuth(order);
        orderApi.validateCreateOrderResponse(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов авторизованным пользователем")
    @Description("Заказ нельзя создать. Появляется сообщение об ошибке")
    public void createOrderWithoutIngredientsTest() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginResponse(loginResponse);
        order = new Order(null);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderApi.validateCreateOrderWithoutIngredientsResponse(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов авторизованным пользователем")
    @Description("Заказ нельзя создать. Появляется сообщение об ошибке")
    public void createOrderWithWrongHashIngredientTest() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginResponse(loginResponse);
        ingredients.set(0, "12345");
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}