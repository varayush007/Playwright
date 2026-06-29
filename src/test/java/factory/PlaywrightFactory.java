package factory;

import com.microsoft.playwright.*;
import utils.ConfigReader;

import java.util.concurrent.CompletableFuture;

public class PlaywrightFactory {

    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();

    public static void initBrowserforCurrentThread() {
        String browserName = ConfigReader.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));

        Playwright pw = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

        Browser launchedBrowser = switch (browserName.toLowerCase()){
            case "chromium" -> pw.chromium().launch(options);
            case "firefox" -> pw.firefox().launch(options);
            case "webkit" -> pw.webkit().launch(options);
            default -> throw new RuntimeException("Invalid Browser: "+ browserName);
        };
        playwright.set(pw);
        browser.set(launchedBrowser);
    }

    public static Browser getBrowser() {
        if(browser.get() == null) {
            initBrowserforCurrentThread();
        }
        return browser.get();
    }

    public static void closeBrowserforCurrentThread() {
        Browser b = browser.get();
        if (b != null) {
            b.close();
        }
        Playwright pw = playwright.get();
        if (pw != null) {
            pw.close();
        }
        playwright.remove();
        browser.remove();
    }
}