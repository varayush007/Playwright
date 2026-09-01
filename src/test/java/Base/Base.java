package Base;
import factory.PlaywrightFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class Base {


    @BeforeMethod
    public void beforeMethod() {
        PlaywrightFactory.initBrowser();
        PlaywrightFactory.createContextAndPage();
    }


    @AfterMethod
    public void afterMethod() {
        PlaywrightFactory.closeContext();
        PlaywrightFactory.closeBrowser();
    }

}
