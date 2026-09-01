package Base;
import com.microsoft.playwright.Page;
import factory.PlaywrightFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.nio.file.Paths;

public class Base {


    @BeforeMethod
    public void beforeMethod() {
        PlaywrightFactory.initBrowser();
        PlaywrightFactory.createContextAndPage();
    }


    @AfterMethod
    public void afterMethod(ITestResult result) {

//        if (!result.isSuccess()) {
//            PlaywrightFactory.getPage().screenshot(
//                    new Page.ScreenshotOptions()
//                            .setPath(Paths.get("screenshots/" + result.getName() + ".png"))
//            );
//        }

        PlaywrightFactory.closeContext();
        PlaywrightFactory.closeBrowser();
    }

}
