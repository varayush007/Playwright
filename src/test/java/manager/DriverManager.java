package manager;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class DriverManager {

    private static final ThreadLocal<BrowserContext> contextThreadLocal =
            new ThreadLocal<>();

    private static final ThreadLocal<Page> pageThreadLocal =
            new ThreadLocal<>();

    private DriverManager() {
        // Prevent object creation
    }

    public static void setContext(BrowserContext context) {
        contextThreadLocal.set(context);
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static void setPage(Page page) {
        pageThreadLocal.set(page);
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static void unload() {
        pageThreadLocal.remove();
        contextThreadLocal.remove();
    }
}