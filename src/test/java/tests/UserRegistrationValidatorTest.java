package tests;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.Validator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class UserRegistrationValidatorTest {
    private Validator validate;

    @BeforeMethod
    public void beforeMethod(){
        validate = new Validator();
    }

    @Test
    public void validUserReg(){
        Assert.assertTrue(validate.isValid("Ayush","varayush@gmail.com",22));
    }

    //Edge Cases username
    @DataProvider(name = "usernameEdgeCases")
    public Object[][] usernameCases(){
        return new Object[][]{
                {"ab","varayush@gmail.com",22,false},
                {"abc","varayush@gmail.com",22,true},
                {"abcd","varayush@gmail.com",22,true},
                {"","varayush@gmail.com",22,false},
                {null,"varayush@gmail.com",22,false},
                {"#&^&#^","varayush@gmail.com",22,true},
                {"qwertyuiopasdfg","varayush@gmail.com",22,true},
                {"qwertyuiopasdfgh","varayush@gmail.com",22,false},
                {"    ","varayush@gmail.com",22,false}
        };
    }

    @Test(dataProvider =  "usernameEdgeCases")
    public void validateUsername(String username, String email, int age, boolean expected){
        Assert.assertEquals(validate.isValid(username,email,age),expected);
    }

    @DataProvider(name = "emailEdgeCases")
    public Object[][] emailCases(){
        return new Object[][]{
                {"abc","varayush@gmail.com",22,true},
                {"abc","varayushgmail.com",22,false},
                {"abc","",22,false},
                {"abc","  ",22,false},
                {"abc",null,22,false},
                {"abc","@sdbhsbch",22,true},
                {"abc","ndsbhbj@",22,true},
                {"abc","bhasbdj@bhjb.c",22,true},
                {"abc","@bhjbj.c@",22,true}
        };
    }

    @Test(dataProvider =  "emailEdgeCases")
    public void validateEmailId(String username, String email, int age, boolean expected){
        Assert.assertEquals(validate.isValid(username,email,age),expected);
    }


    @DataProvider(name = "ageEdgeCases")
    public Object[][] ageCases(){
        return new Object[][]{
                {"abc","varayush@gmail.com",1,false},
                {"abc","varayush@gmail.com",22,true},
                {"abc","varayush@gmail.com",17,false},
                {"abc","varayush@gmail.com",59,true},
                {"abc","varayush@gmail.com",60,true},
                {"abc","varayush@gmail.com",61,false},
                {"abc","varayush@gmail.com",0,false},
        };
    }

    @Test(dataProvider =  "ageEdgeCases")
    public void validateAge(String username, String email, int age, boolean expected){
        Assert.assertEquals(validate.isValid(username,email,age),expected);
    }

    @DataProvider(name = "finalPrice")
    public Object[][] finalPrices(){
        return new Object[][]{
                {1000,0,1000},
                {1000,25,750},
                {1000,100,0},
                {0,10,0},
                {99.99,10,89.991}
        };
    }

    @Test(dataProvider =  "finalPrice")
    public void validateCorrectPrice(double price,int discount, double expected){
        Assert.assertEquals(validate.calculateFinalPrice(price,discount),expected,0.001);
    }


    @DataProvider(name = "invalidTotalPrice")
    public Object[][] invalidTotalPrice(){
        return new Object[][]{
                {-1000,0},
                {1000,-25},
                {1000,110},
                {-122,-11}
        };
    }

    @Test(dataProvider =  "invalidTotalPrice",expectedExceptions = IllegalArgumentException.class)
    public void validateInvalidTotalPrice(double price,int discount){
        validate.calculateFinalPrice(price,discount);
    }

    @DataProvider(name = "validPassword")
    public Object[][] validPassword(){
        return new Object[][]{
                {"", false},
                {null, false},
                {"abc", false},
                {"123", false},
                {"abc1234", false},      // length 7
                {"abc12345", true},      // length 8
                {"abcdefgh", false},     // no digit
                {"12345678", true},      // only digits
                {"saxnjhasbxh6767", true},
                {"%@^&%@&^%@", false},
                {"@@1 11", false},       // length 6
                {"abc def1", true},      // space allowed by current implementation
                {"        ", false}      // spaces only
        };
    }

    @Test(dataProvider =  "validPassword")
    public void validPassword(String password,boolean expected){
        Assert.assertEquals(validate.isValid(password),expected);
    }

    @Test
    public void addOneItem(){
        validate.addItem("Apple",2);
        Assert.assertEquals(validate.getQuantity("Apple"),2);
        Assert.assertEquals(validate.getTotalItems(),2);
    }

    @Test
    public void addNewItem(){
        validate.addItem("Apple",2);
        validate.addItem("      ",12);
        Assert.assertEquals(validate.getQuantity("      "),12);
        Assert.assertEquals(validate.getTotalItems(),14);
    }

    @Test
    public void addSameItem(){
        validate.addItem("Apple",2);
        validate.addItem("Apple",6);
        Assert.assertEquals(validate.getQuantity("Apple"),8);
        Assert.assertEquals(validate.getTotalItems(),8);
    }

    @Test
    public void getRandomItem(){
        Assert.assertEquals(validate.getQuantity("Apple"),0);
    }

    @Test
    public void getRandomTotalItems(){
        Assert.assertEquals(validate.getTotalItems(),0);
    }

    @DataProvider(name = "invalidQuantitiesTestCase")
    public Object[][] invalidQuanities(){
        return new Object[][]{
                {0},
                {-1}
        };
    }

    @Test(dataProvider =  "invalidQuantitiesTestCase", expectedExceptions = IllegalArgumentException.class)
    public void negativeQuantityTestCases(int quantity){
        validate.addItem("Apple",quantity);
    }

//    Whitespace-only item name: isEmpty() does not reject spaces.
//    Use isBlank() if whitespace-only names are invalid.

    @DataProvider(name = "invalidItemNames")
    public Object[][] invalidItemNames(){
        return new Object[][]{
                {null},
                {""},
                {"    "} // this will break since this is valid
        };
    }

    @Test(dataProvider = "invalidItemNames", expectedExceptions = IllegalArgumentException.class)
    public void invalidItemNamesTC(String item){
        validate.addItem(item,1);
    }

    //Integer overflow: Java int arithmetic wraps around silently.
    // Use Math.addExact() to detect overflow, and test that the existing state remains unchanged.
    @Test
    public void shouldPreserveCartAfterInvalidAddition(){
        validate.addItem("Apple",1);
        Assert.expectThrows(IllegalArgumentException.class,()->validate.addItem("Apple",0));
        Assert.assertEquals(validate.getQuantity("Apple"),1);
        Assert.assertEquals(validate.getTotalItems(),1);

        validate.addItem("Apple",Integer.MAX_VALUE);
        Assert.assertEquals(validate.getQuantity("Apple"),Integer.MIN_VALUE);
    }

    @Test
    public void shouldCalcTotalAfterMultipleAdditions(){
        validate.addItem("Apple",2);
        validate.addItem("Orange",10);
        validate.addItem("Apple",2);
        validate.addItem("Green Apple",2);

        Assert.assertEquals(validate.getQuantity("Apple"),4);
        Assert.assertEquals(validate.getQuantity("Orange"),10);
        Assert.assertEquals(validate.getQuantity("Green Apple"),2);
        Assert.assertEquals(validate.getTotalItems(),16);

    }
    // Retry Mechanism

    @Test
    public void successOnFirstAttempt(){
        AtomicInteger count = new AtomicInteger(0);
        Supplier<Boolean>task = () -> {
            count.incrementAndGet();
            return true;
        };

        boolean result = validate.execute(task,3);
        Assert.assertTrue(result);
        Assert.assertEquals(count.get(),1);
    }

    @Test
    public void successOnThirdAttempt(){
        AtomicInteger count = new AtomicInteger(0);
        Supplier<Boolean>task = () -> count.incrementAndGet() == 3;

        boolean result = validate.execute(task,5);
        Assert.assertTrue(result);
        Assert.assertEquals(count.get(),3);
    }

    @Test
    public void failEveryAttempt(){
        AtomicInteger count = new AtomicInteger(0);
        Supplier<Boolean>task = () -> {
            count.incrementAndGet();
            return false;
        };

        boolean result = validate.execute(task,3);
        Assert.assertFalse(result);
        Assert.assertEquals(count.get(),3);
    }

    @Test
    public void nullTaskAttempt(){
        AtomicInteger count = new AtomicInteger(0);
        Supplier<Boolean>task = () -> {
            count.incrementAndGet();
            return null;
        };

        boolean result = validate.execute(task,3);
        Assert.assertFalse(result);
        Assert.assertEquals(count.get(),3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectNullTask(){
        validate.execute(null,3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void zeroMaxAttemptTask(){
        validate.execute(()->true,0);
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void negMaxAttemptTask(){
        validate.execute(()->true,-1);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldPropagateTaskException() {
        AtomicInteger count = new AtomicInteger(0);

        Supplier<Boolean> task = () -> {
            count.incrementAndGet();
            throw new RuntimeException("Task failed");
        };

        validate.execute(task, 3);
    }

}
