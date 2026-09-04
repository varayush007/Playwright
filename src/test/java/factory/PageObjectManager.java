package factory;

import com.microsoft.playwright.Page;
import pages.LoginPage;

public class PageObjectManager {

    private final Page page;
    private LoginPage loginPage;

    public PageObjectManager(Page page) {
        this.page = page;
    }

    public LoginPage loginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(page);
        }
        return loginPage;
    }
}