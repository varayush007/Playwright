package factory;
import com.microsoft.playwright.*;
import utils.ConfigReader;

import java.io.FileNotFoundException;

public class PlaywrightFactory {
    private static final ThreadLocal<Playwright>playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser>browser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>browserContext = new ThreadLocal<>();
    private static final ThreadLocal<Page>page= new ThreadLocal<>();

    public static void initBrowser()  {
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

    public static Browser getBrowser() {
        if(browser.get() == null) {
            initBrowser();
        }
        return browser.get();
    }

    public static void createContextAndPage()  {

        BrowserContext context = getBrowser().newContext(new Browser.NewContextOptions()
                        .setViewportSize(null));

        Page newPage = context.newPage();

        browserContext.set(context);
        page.set(newPage);
    }


    // =========================
    // Get Context
    // =========================

    public static BrowserContext getContext() {
        return browserContext.get();
    }


    // =========================
    // Get Page
    // =========================

    public static Page getPage() {
        return page.get();
    }


    // =========================
    // Close Context + Page
    // =========================

    public static void closeContext() {

        Page currentPage = getPage();

        if (currentPage != null) {
            currentPage.close();
        }

        BrowserContext currentContext = getContext();

        if (currentContext != null) {
            currentContext.close();
        }

        page.remove();
        browserContext.remove();
    }


    // =========================
    // Close Browser
    // =========================

    public static void closeBrowser() {

        Browser currentBrowser = browser.get();

        if (currentBrowser != null) {
            currentBrowser.close();
        }

        Playwright currentPlaywright = playwright.get();

        if (currentPlaywright != null) {
            currentPlaywright.close();
        }

        browser.remove();
        playwright.remove();
    }
}
