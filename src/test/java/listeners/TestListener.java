package listeners;

import base.BaseTest;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static final Logger logger =
            LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("STARTED: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASSED: {}", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {

        logger.error("FAILED: {}", result.getName());

        Page page = BaseTest.getPage();

        if (page != null) {
            String screenshotPath =
                    ScreenshotUtils.captureScreenshot(page, result.getName());

            logger.info("Screenshot captured at: {}", screenshotPath);
        }

        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            logger.error("Failure Reason:", throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("SKIPPED: {}", result.getName());
    }
}