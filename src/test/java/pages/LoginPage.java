package pages;
import com.microsoft.playwright.Page;
public class LoginPage {

    private final Page page;

    private static String newUser = "//button[@id='newUser']";
    private static String firstName = "//input[@id='firstname']";
    private static String lastName = "//input[@id='lastname']";
    private static String userName = "//input[@id='userName']";
    private static String password = "//input[@id='password']";

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

    public void fillNewUserForm(String firstname, String lastname, String username, String pass){
        fillFirstName(firstname);
        fillLastName(lastname);
        fillUserName(username);
        fillPassword(pass);
    }
    public void navigateToURL(){
        page.navigate("https://demoqa.com/login");
    }
}
