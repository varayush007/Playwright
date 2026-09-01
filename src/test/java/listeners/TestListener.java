package listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import factory.PlaywrightFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportManager;

import java.nio.file.Paths;

public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = ExtentReportManager
                .getReportObject()
                .createTest(result.getMethod().getMethodName());

        test.set(extentTest);
    }
    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String screenshotPath = "screenshots/"
                + result.getName()
                + "_"
                + System.currentTimeMillis()
                + ".png";

        try {
            Page page = PlaywrightFactory.getPage();
            if (page != null) {
                page.screenshot(
                        new Page.ScreenshotOptions()
                                .setPath(Paths.get(screenshotPath))
                                .setFullPage(true)
                );

                test.get().addScreenCaptureFromPath("../" + screenshotPath);
            }

            test.get().log(Status.FAIL, result.getThrowable());

        } catch (Exception e) {
            test.get().log(Status.FAIL, "Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.getReportObject().flush();
    }
}