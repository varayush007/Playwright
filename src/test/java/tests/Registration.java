package tests;

import Base.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;

import static factory.PlaywrightFactory.getPage;


public class Registration extends Base {
    private static final Logger log = LoggerFactory.getLogger(Registration.class);
    LoginPage loginPage;

    @BeforeMethod
    public void initializePages(){
        loginPage = new LoginPage(getPage());
    }

    @Test
    public void newUserRegistration(){
        loginPage.navigateToURL();
        loginPage.clickNewUser();
        loginPage.fillNewUserForm("Ayush","Varshney","varayush","aaaaaa");
    }
}
