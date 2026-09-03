package pages;
import com.microsoft.playwright.Page;
import org.testng.asserts.SoftAssert;

public class LoginPage {

    private final Page page;

    private static String newUser = "//button[@id='newUser']";
    private static String firstName = "//input[@id='firstname']";
    private static String lastName = "//input[@id='lastname']";
    private static String userName = "//input[@id='userName']";
    private static String password = "//input[@id='password']";
    private static String registerBtn = "//button[@id='register']";
    private static String goToLogin = "//button[@id='gotologin']";
    private static String loginBtn = "//button[@id='login']";

    public LoginPage(Page page){
        this.page = page;
    }
    public void clickNewUser(){
        page.locator(newUser).click();
    }
    public void fillFirstName(String firstname){
        page.locator(firstName).fill(firstname);
    }
    public void fillLastName(String lastname){
        page.locator(lastName).fill(lastname);
    }
    public void fillUserName(String username){
        page.locator(userName).fill(username);
    }
    public void fillPassword(String pass){
        page.locator(password).fill(pass);
    }
    public void clickRegisterBtn(){
        page.locator(registerBtn).click();
    }

    public void backToLogin(){
        page.locator(goToLogin).click();
    }

    public void clickLoginBtn(){
        page.locator(loginBtn).click();
    }

    public void fillNewUserForm(String firstname, String lastname, String username, String pass){
        fillFirstName(firstname);
        fillLastName(lastname);
        fillUserName(username);
        fillPassword(pass);
    }
    public void navigateToURL(){
        page.navigate("https://demoqa.com/login");
    }

    public void fillLoginInfo(String userName, String password){
        fillUserName(userName);
        fillPassword(password);
    }

    public void dummy(SoftAssert sa){
        page.navigate("https://www.saucedemo.com/");
        page.locator("#user-name").fill("standard_user");
        page.locator("#password").fill("secret_sauce");
        page.locator("#login-button").click();
        sa.assertTrue(
                page.locator(".title").isVisible()
        );
    }
    public void producttest(SoftAssert sa){
        page.navigate("https://www.saucedemo.com/");

        page.locator("#user-name").fill("standard_user");
        page.locator("#password").fill("secret_sauce");
        page.locator("#login-button").click();

        sa.assertTrue(
                page.locator(".inventory_list").isVisible()
        );
    }
}
