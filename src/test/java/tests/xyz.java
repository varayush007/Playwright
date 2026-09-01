package tests;
import Base.Base;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import static factory.PlaywrightFactory.getPage;
import java.lang.reflect.Method;

import org.apache.commons.lang3.RandomStringUtils;

public class xyz extends Base {
    private final ThreadLocal<LoginPage> loginPage = new ThreadLocal<>();
    private final ThreadLocal<SoftAssert> sa = new ThreadLocal<>();

    @BeforeMethod
    public void initializePages(Method method) {
        System.out.println(
                "Test: " + method.getName() +
                        " | Thread ID: " + Thread.currentThread().getId()
        );
        loginPage.set(new LoginPage(getPage()));
        sa.set(new SoftAssert());
    }

    @Test
    public void newUserRegistration() {
        LoginPage login = loginPage.get();
        SoftAssert softAssert = sa.get();
        login.navigateToURL();
        login.clickNewUser();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password = RandomStringUtils.randomAlphanumeric(6);
        login.fillNewUserForm("Ayush", "Varshney", username, password);
        login.clickRegisterBtn();
        login.backToLogin();
        login.fillLoginInfo(username, password);
        login.clickLoginBtn();
        softAssert.assertAll();
    }

    @Test
    public void validLoginTest() {
        LoginPage login = loginPage.get();
        SoftAssert softAssert = sa.get();
        login.dummy(softAssert);
    }

    @Test
    public void verifyProductPage() {
        LoginPage login = loginPage.get();
        SoftAssert softAssert = sa.get();
        login.producttest(softAssert);
    }
}