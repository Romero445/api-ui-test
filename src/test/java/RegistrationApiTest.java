import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;


import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.is;
import static io.restassured.RestAssured.given;

public class RegistrationApiTest {


    @Test
    public void subscribeForNews() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("email", "man@off.com")
                .when()
                .post("http://demowebshop.tricentis.com/subscribenewsletter")
                .then()
                .statusCode(200)
                .body("Success", is(true))
                .body("Result", is("Thank you for signing up! A verification email has been sent. We appreciate your interest."));

    }

    @Test
    public void communityPollVoteTest() {

        RestAssured.filters(new AllureRestAssured());

        step("Забираем cookie ", () -> {
            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", "rymba@gmail.com")
                            .formParam("Password", "!HBQz6xE6w#k#3p")
                            .when()
                            .post("http://demowebshop.tricentis.com/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Открываем элемент сайта для подстановки cookie", () ->
                    open("http://demowebshop.tricentis.com/content/images/thumbs/0000224_141-inch-laptop_125.png"));

            step("Подставляем cookie", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });

        step("Открываем главную страницу", () ->
                open("http://demowebshop.tricentis.com/"));

        step("Проверяем, что под нужным аккаунтом", () ->
                $(".account").shouldHave(text("rymba@gmail.com")));


    }

    @Test
    public void noRegisteredVote() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("pollAnswerId", "1")
                .when()
                .post("http://demowebshop.tricentis.com/poll/vote")
                .then()
                .statusCode(200)
                .body("error", is("Only registered users can vote."));


    }


}
