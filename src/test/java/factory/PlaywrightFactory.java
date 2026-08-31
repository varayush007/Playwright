package factory;
import com.microsoft.playwright.*;
import utils.ConfigReader;

import java.io.FileNotFoundException;

public class PlaywrightFactory {
    private static final ThreadLocal<Playwright>playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser>browser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>browserContext = new ThreadLocal<>();
    private static final ThreadLocal<Page>page= new ThreadLocal<>();
    public static void initBrowser() throws FileNotFoundException {
        String browserName = ConfigReader.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));
        Playwright pw = Playwright.create();

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);
        Browser b = switch (browserName.toLowerCase()){
            case "chromium" -> pw.chromium().launch(options);
            case "firefox"-> pw.firefox().launch(options);
            case "webkit" -> pw.webkit().launch(options);
            default -> throw new IllegalStateException("Unexpected value: " + browserName.toLowerCase());
        };
        playwright.set(pw);
        browser.set(b);
    }

    public static Browser getBrowser() throws FileNotFoundException {
        if(browser.get() == null) {
            initBrowser();
        }
        return browser.get();
    }

    public static void setContext(BrowserContext ctx) {
        browserContext.set(ctx);
    }

    public static BrowserContext getContext() {
        return browserContext.get();
    }

    public static void setPage(Page p) {
        page.set(p);
    }

    public static Page getPage() {
        return page.get();
    }

    public static void removePage() {
        page.remove();
    }

    public static void removeContext() {
        browserContext.remove();
    }

    public static void closeBrowser(){
        Page p = page.get();
        if (p != null) {
            p.close();
        }
        BrowserContext ctx = browserContext.get();
        if (ctx != null) {
            ctx.close();
        }
        Browser b = browser.get();
        if (b != null) {
            b.close();
        }
        Playwright pw = playwright.get();
        if (pw != null) {
            pw.close();
        }
        page.remove();
        browserContext.remove();
        browser.remove();
        playwright.remove();
    }
}
