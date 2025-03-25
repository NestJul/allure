package ru.netology.delivery.test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import io.qameta.allure.selenide.AllureSelenide;


class DeliveryTest {
    @BeforeAll
    static void setUpAll (){
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        SelenideElement form = $("form");
        var validUser = DataGenerator.Registration.generateUser("ru");
        eraseAndSendKeys(form.$("[data-test-id=city] input"), validUser.getCity());
        eraseAndSendKeys(form.$("[data-test-id=date] input"), firstMeetingDate);
        eraseAndSendKeys(form.$("[data-test-id=name] input"), validUser.getName()); //Использование Ё вызывает ошибку "используйте только русские буквы"
        eraseAndSendKeys(form.$("[data-test-id=phone] input"), validUser.getPhone());
        form.$("[data-test-id=agreement]").click();
        form.$(By.className("button_theme_alfa-on-white")).click();
        $("[data-test-id=success-notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content").shouldHave(text(firstMeetingDate));
        eraseAndSendKeys(form.$("[data-test-id=date] input"), secondMeetingDate);
        form.$(By.className("button_theme_alfa-on-white")).click();
        SelenideElement buttonReplan = $("[data-test-id=replan-notification] .button__text");
        buttonReplan.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Перепланировать"));
        buttonReplan.click();
        $("[data-test-id=success-notification] .notification__title").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content").shouldHave(text(secondMeetingDate));
    }

    private void eraseAndSendKeys(SelenideElement element, String data) {
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.setValue(data);
    }
}
