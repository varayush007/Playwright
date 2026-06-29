package base;

import com.microsoft.playwright.*;
import factory.PlaywrightFactory;
import org.testng.annotations.*;
import utils.ConfigReader;
import manager.DriverManager;

public class BaseTest {

    @BeforeSuite
    public void beforeSuite() {
        ConfigReader.initConfig();
    }

    @BeforeMethod
    public void beforeMethod() {
        BrowserContext context = PlaywrightFactory.getBrowser().newContext(
                new Browser.NewContextOptions().setViewportSize(null)
        );

        Page page = context.newPage();

        page.setDefaultTimeout(Double.parseDouble(ConfigReader.get("timeout")));
        page.setDefaultNavigationTimeout(Double.parseDouble(ConfigReader.get("navigationTimeout")));

        DriverManager.setContext(context);
        DriverManager.setPage(page);
    }

    public static Page getPage() {
        return DriverManager.getPage();
    }

    public static BrowserContext getContext() {
        return DriverManager.getContext();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        try {
            BrowserContext context = getContext();
            if (context!=null) {
                context.close();
            }
        } finally {
            DriverManager.unload();
            PlaywrightFactory.closeBrowserforCurrentThread();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
    }
}