package Base;
import factory.PlaywrightFactory;
import factory.PageObjectManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class Base {
    private final ThreadLocal<PageObjectManager> pageObjectManager = new ThreadLocal<>();
    @BeforeMethod
    public void beforeMethod() {
        PlaywrightFactory.initBrowser();
        PlaywrightFactory.createContextAndPage();
        pageObjectManager.set(new PageObjectManager(PlaywrightFactory.getPage()));
    }

    protected PageObjectManager pages(){
        return pageObjectManager.get();
    }

    @AfterMethod
    public void afterMethod() {
        pageObjectManager.remove();
        PlaywrightFactory.closeContext();
        PlaywrightFactory.closeBrowser();
    }

}
