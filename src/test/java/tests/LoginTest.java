package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.JsonReader;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void initializePages() {
        loginPage = new LoginPage(getPage());
    }

    @Test
    public void verifyLoginButtonIsVisible() {
        loginPage.navigate();
        Assert.assertTrue(
                loginPage.isLoginButtonVisible(),
                "Login button should be visible."
        );
    }

    @Test
    public void verifyInvalidLoginShowsError() {
        String email = JsonReader.getValue("invalidUser", "email");
        String password = JsonReader.getValue("invalidUser", "password");
        loginPage.navigate();
        loginPage.login(email, password);
        getPage().waitForTimeout(500);
        Assert.assertTrue(
                loginPage.isErrorMessageVisible(),
                "Error message should be displayed."
        );
    }
}