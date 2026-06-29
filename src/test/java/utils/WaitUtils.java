package utils;

import com.microsoft.playwright.*;

public class WaitUtils {

    public static void waitForUrl(Page page, String urlPattern) {
        page.waitForURL(urlPattern);
    }

    public static Response waitForApiResponse(Page page, String urlPart, Runnable action) {
        return page.waitForResponse(
                response -> response.url().contains(urlPart) && response.status() == 200,
                action
        );
    }

    public static void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }
}