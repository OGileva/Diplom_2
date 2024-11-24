package order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;

import java.util.Arrays;
import java.util.List;

@Epic("Диплом. Тестирование API.")
@DisplayName("Получение заказа")
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
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Список заказов можно получить")
    public void getOrderWithAuthTest() {
        User testUser = new User(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(testUser);
        userApi.validateLoginResponse(loginResponse);
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderApi.createOrderWithAuth(order, accessToken);
        orderApi.validateCreateOrderResponse(orderResponse);

        ValidatableResponse getOrderResponseWithAuth = orderApi.getOrderWithAuth(accessToken);
        orderApi.validateGetOrderResponse(getOrderResponseWithAuth);
    }

    @Test
    @DisplayName("Получение заказа неавторизованного пользователя")
    @Description("Список заказов нельзя получит получить. Появляется сообщение об ошибке")
    public void getOrderWithoutAuthTest() {
        order = new Order(ingredients);
        ValidatableResponse orderResponseWithoutAuth = orderApi.createOrderWithoutAuth(order);
        orderApi.validateGetOrderResponse(orderResponseWithoutAuth);
        ValidatableResponse getOrderResponseWithoutAuth = orderApi.getOrderWithoutAuth();
        orderApi.validateGetOrderResponseWithoutAuth(getOrderResponseWithoutAuth);
    }
}
