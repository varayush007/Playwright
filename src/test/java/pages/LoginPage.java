package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class LoginPage {

    private final Page page;

    private final String loginButton = "#login_Layer";
    private final String emailInput =
            "input[placeholder='Enter your active Email ID / Username']";
    private final String passwordInput =
            "input[placeholder='Enter your password']";
    private final String submitButton = "button[type='submit']";
    private final String errorMessage = ".server-err";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate(ConfigReader.get("baseUrl"));
    }

    public void clickLoginButton() {
        page.locator(loginButton).click();
    }

    public void enterEmail(String email) {
        page.locator(emailInput).fill(email);
    }

    public void enterPassword(String password) {
        page.locator(passwordInput).fill(password);
    }

    public void clickSubmitButton() {
        page.locator(submitButton).click();
    }

    // High-level reusable flow
    public void login(String email, String password) {
        clickLoginButton();
        enterEmail(email);
        enterPassword(password);
        clickSubmitButton();
    }

    public boolean isLoginButtonVisible() {
        return page.locator(loginButton).isVisible();
    }

    public boolean isErrorMessageVisible() {
        return page.locator(errorMessage).filter(new Locator.FilterOptions()
                .setHasText("Invalid details")).isVisible();
    }

    public String getCurrentUrl() {
        return page.url();
    }
}