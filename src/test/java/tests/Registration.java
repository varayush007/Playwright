package tests;

import Base.Base;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.lang.reflect.Method;

import org.apache.commons.lang3.RandomStringUtils;

public class Registration extends Base {

    @BeforeMethod
    public void initializePages(Method method) {
        System.out.println(
                "Test: " + method.getName() +
                        " | Thread ID: " + Thread.currentThread().getId()
        );
    }

    @Test
    public void newUserRegistration() {
        SoftAssert sa = new SoftAssert();
        pages().loginPage().navigateToURL();
        pages().loginPage().clickNewUser();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password = RandomStringUtils.randomAlphanumeric(6);
        pages().loginPage().fillNewUserForm("Ayush", "Varshney", username, password);
        pages().loginPage().clickRegisterBtn();
        pages().loginPage().backToLogin();
        pages().loginPage().fillLoginInfo(username, password);
        pages().loginPage().clickLoginBtn();
        sa.assertAll();
    }

    @Test
    public void validLoginTest() {
        SoftAssert sa = new SoftAssert();
        pages().loginPage().dummy(sa);
    }

    @Test
    public void verifyProductPage() {
        SoftAssert sa = new SoftAssert();
        pages().loginPage().producttest(sa);
    }
}