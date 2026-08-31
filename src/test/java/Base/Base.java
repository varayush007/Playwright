package Base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import factory.PlaywrightFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.ConfigReader;

import java.io.FileNotFoundException;

public class Base {
    @BeforeSuite
    public void beforeSuite() throws FileNotFoundException {
        ConfigReader.initConfig();
    }

    @BeforeMethod
    public void beforeMethod() throws FileNotFoundException {
        Browser browser = PlaywrightFactory.getBrowser();
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(null)
        );

        Page page = context.newPage();

        PlaywrightFactory.setContext(context);
        PlaywrightFactory.setPage(page);
    }

    @AfterMethod
    public void afterMethod() {

        Page page = PlaywrightFactory.getPage();
        if (page != null) {
            page.close();
        }

        BrowserContext context = PlaywrightFactory.getContext();
        if (context != null) {
            context.close();
        }

        PlaywrightFactory.removePage();
        PlaywrightFactory.removeContext();
    }
    @AfterSuite
    public void afterSuite(){
        PlaywrightFactory.closeBrowser();
    }

}
