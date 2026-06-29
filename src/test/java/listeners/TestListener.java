package listeners;

import base.BaseTest;
import com.microsoft.playwright.Page;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("STARTED: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("FAILED: " + result.getName());

        Page page = BaseTest.getPage();

        if (page != null) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(page, result.getName());
            System.out.println("Screenshot captured: " + screenshotPath);
        }

        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("SKIPPED: " + result.getName());
    }
}