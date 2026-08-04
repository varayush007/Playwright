package api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;



public class Users_Todo {
    private static final String userAPI = "https://jsonplaceholder.typicode.com/users";
    private static final String todoAPI = "https://jsonplaceholder.typicode.com/todos";
    Map<Integer,String>userMap = new HashMap<>(); // id -> name
    List<Integer>userIds = new ArrayList<>(); // List of userId from todo api
    Map<Integer, List<Map<String, Object>>> todosByUser = new HashMap<>();
    Map<Integer, Integer> completedTodosByUser = new HashMap<>();
    int totalCompletedTodos;
    int totalPendingTodos;
    int keywordSearchCount;
    @Test
    public void getUsersData(){
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(userAPI)
                .then()
                .statusCode(200)
                .extract().response();
        Assert.assertNotNull(response,"Response should be not null");
        Assert.assertFalse(response.jsonPath().getList("$").toString().isBlank(),"Response should not be blank");
        List<Map<String,Object>>users = response.jsonPath().getList("$");
        Assert.assertEquals(users.size(),10,"Users response does not contains 10 users");

        for(Map<String,Object>user : users){
            Assert.assertNotNull(user.get("id"),"id is null");
            Assert.assertNotNull(user.get("name"),"name is null");
            Assert.assertNotNull(user.get("username"),"username is null");
            Assert.assertFalse(user.get("username").toString().isBlank(),"username is blank");
            Assert.assertNotNull(user.get("email"),"email is null");
            Assert.assertFalse(user.get("email").toString().isBlank(),"email is blank");
            Assert.assertNotNull(user.get("phone"),"phone is null");
            Assert.assertFalse(user.get("phone").toString().isBlank(),"phone is blank");
            Assert.assertNotNull(user.get("website"),"website is null");
            Assert.assertFalse(user.get("website").toString().isBlank(),"website is blank");
            Assert.assertNotNull(user.get("address"),"id is null");
            Assert.assertNotNull(user.get("company"),"id is null");
            Assert.assertTrue(user.get("email").toString().contains("@"),"email does not contains @");
            userMap.put((Integer) user.get("id"), (String) user.get("name"));
        }
    }

    @Test
    public void getTodosData(){
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(todoAPI)
                .then()
                .statusCode(200)
                .extract().response();
        Assert.assertNotNull(response,"Response should be not null");
        Assert.assertFalse(response.jsonPath().getList("$").toString().isBlank(),"Response should not be blank");
        List<Map<String,Object>>todos = response.jsonPath().getList("$");
        Assert.assertEquals(todos.size(),200,"Todos response does not contains 200 todos");

        for(Map<String,Object>todo : todos){
            Assert.assertNotNull(todo.get("userId"),"userId is null");
            Assert.assertNotNull(todo.get("id"),"id is null");
            Assert.assertNotNull(todo.get("title"),"title is null");
            Assert.assertFalse(todo.get("title").toString().isBlank(),"title is blank");
            Assert.assertNotNull(todo.get("completed"),"completed is null");
            Assert.assertTrue(todo.get("completed") instanceof Boolean,"completed is not boolean value");
            userIds.add((Integer) todo.get("userId"));
            todosByUser.computeIfAbsent((Integer) todo.get("userId"), k->new ArrayList<>()).add(todo);
        }
    }

    @Test(dependsOnMethods = {"getUsersData","getTodosData"})
    public void crossApiValidation(){
        for(Integer id : userIds){
            Assert.assertTrue(userMap.containsKey(id),"Todo Id "+ id + " not present in user api");
        }
    }

    @Test(dependsOnMethods = {"getUsersData","getTodosData"})
    public void getTodosByUser(){
        for(Integer id: userMap.keySet()){
            String username = userMap.get(id);
            int totalTodos = todosByUser.get(id).size();
            System.out.println("User ID " + id);
            System.out.println("User Name " + username);
            System.out.println("Total Todos " + totalTodos);
        }
    }

    @Test(dependsOnMethods = {"getUsersData","getTodosData"})
    public void completedTodoCountPerUser(){
        int maximum = 0;
        totalCompletedTodos = 0;
        totalPendingTodos = 0;

        for(Integer id: userMap.keySet()){
            List<Map<String,Object>>todos = todosByUser.get(id);
            int completedCount = 0;
            int pendingCount = 0;
            for(Map<String,Object>todo : todos){
                Boolean completed = (Boolean) todo.get("completed");
                if(completed)
                    completedCount++;
                else
                    pendingCount++;
            }
            String username = userMap.get(id);
            int totalTodos = todosByUser.get(id).size();
            System.out.println("User ID " + id);
            System.out.println("User Name " + username);
            System.out.println("Total Todos " + totalTodos);
            System.out.println("Completed Todos " + completedCount);
            System.out.println("Pending Todos " + pendingCount);
            System.out.println(".................");
            Assert.assertEquals(totalTodos,20,"Expected 20 todos for user" + username + "but found" + totalTodos);
            completedTodosByUser.put(id, completedCount);
            totalCompletedTodos += completedCount;
            totalPendingTodos += pendingCount;
            maximum = Integer.max(maximum,completedCount);
        }

        for (Integer id : userMap.keySet()) {
            int completedCount = completedTodosByUser.get(id);
            if (completedCount == maximum) {
            System.out.println("User ID " + id);
            System.out.println("User Name " + userMap.get(id));
            System.out.println("Completed Todos " + completedCount);
            System.out.println(".................");
            }
        }
    }

    @Test(dependsOnMethods = {"getUsersData","getTodosData"})
    public void searchKeyword(){
        String keyword = "delectus";
        keywordSearchCount = 0;
        for(Integer id: userMap.keySet()){
            List<Map<String,Object>>todos = todosByUser.get(id);
            for(Map<String,Object>todo : todos){
                if(todo.get("title").toString().contains(keyword)){
                    System.out.println("Todo ID " + todo.get("id"));
                    System.out.println("User ID " + id);
                    System.out.println("User Name " + userMap.get(id));
                    System.out.println("Title " + todo.get("title"));
                    System.out.println("Completed Status " + todo.get("completed"));
                    keywordSearchCount++;
                }
            }
        }
        Assert.assertTrue(keywordSearchCount > 0, "At least one post should match the keyword: " + keyword);

    }

    @Test(dependsOnMethods = {"getUsersData", "getTodosData", "crossApiValidation", "getTodosByUser", "completedTodoCountPerUser", "searchKeyword"})
    public void finalSummary() {
        boolean usersHavingExactlyTwentyTodos = true;

        for (Integer id : userMap.keySet()) {
            int totalTodos = todosByUser.get(id).size();
            if (totalTodos != 20) {
                usersHavingExactlyTwentyTodos = false;
                break;
            }
        }

        System.out.println("Total Users: " + userMap.size());
        System.out.println("Total Todos: " + userIds.size());
        System.out.println("Users with valid todo mapping: " + todosByUser.size());
        System.out.println("Users having exactly 20 todos: " + usersHavingExactlyTwentyTodos);
        System.out.println("Total Completed Todos: " + totalCompletedTodos);
        System.out.println("Total Pending Todos: " + totalPendingTodos);
        System.out.println("Keyword Search Count: " + keywordSearchCount);
    }
}
