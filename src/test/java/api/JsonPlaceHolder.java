package api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.*;


public class JsonPlaceHolder {
    private static final String endpoint = "https://jsonplaceholder.typicode.com/users";
    private static final String endpoint1  = "https://jsonplaceholder.typicode.com/posts";
    Map<Integer,String>userMap = new HashMap<>();
    List<Integer> userIds = new ArrayList<>();
    Map<Integer, List<Map<String, Object>>> postsByUser = new HashMap<>();
    List<Map<String, Object>> postDetails = new ArrayList<>();
    int keywordSearchCount;

    private void validateNotBlank(Map<String,?>user, String field){
        Assert.assertNotNull(user.get(field),field + " should not be null");
        Assert.assertFalse(user.get(field).toString().isBlank(),field + " should not be blank");
    }

    //Part 1
    @Test
    public void validateUserDetails(){
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract().response();
        List<Map<String,Object>>users = response.jsonPath().getList("$");
        Assert.assertNotNull(users,"user list should not be null");
        Assert.assertFalse(users.isEmpty(),"Users list should not be empty");
        Assert.assertEquals(users.size(),10,"user list size mismatch");

        for(Map<String,Object>user : users){
            Assert.assertNotNull(user.get("id"),"id should not be null");
            validateNotBlank(user,"name");
            validateNotBlank(user,"username");
            validateNotBlank(user,"email");
            validateNotBlank(user,"phone");
            validateNotBlank(user,"website");
            Assert.assertNotNull(user.get("address"),"address should not be null");
            Assert.assertNotNull(user.get("company"),"company should not be null");
            Assert.assertTrue(user.get("email").toString().contains("@"),"email should contains @");
            userMap.put((Integer) user.get("id"), (String) user.get("name"));
        }
    }

    //Part 2
    @Test
    public void validateDataFields() {
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(endpoint1)
                .then()
                .statusCode(200)
                .extract().response();
        postDetails = response.jsonPath().getList("$");
        Assert.assertNotNull(postDetails, "post details should not be null");
        Assert.assertEquals(postDetails.size(), 100, "posts count mismatch");

        for (Map<String, Object> post : postDetails) {
            Assert.assertNotNull(post.get("userId"), "userid should not be null");
            Assert.assertNotNull(post.get("id"), "id should not be null");
            validateNotBlank(post, "title");
            validateNotBlank(post, "body");
            Assert.assertTrue(post.get("title").toString().length() > 5, "title length should be greater than 5");
            Assert.assertTrue(post.get("body").toString().length() > 20, "body length should be greater than 20");
            Integer userid = (Integer) post.get("userId");
            userIds.add(userid);
            postsByUser.computeIfAbsent(userid, k -> new ArrayList<>()).add(post);

            //or

//            if(!postsByUser.containsKey(userid)){
//                postsByUser.put(userid,new ArrayList<>());
//            }
//            postsByUser.get(userid).add(post);
        }
    }

    //Part 3
    @Test(dependsOnMethods = {"validateUserDetails", "validateDataFields"})
    public void crossAPIValidation(){
         for(Integer postUserID : userIds){
            Assert.assertTrue(userMap.containsKey(postUserID),"Post userId "+ postUserID + " does not exist in users api");
        }
    }

    //Part 4
    @Test(dependsOnMethods = {"validateUserDetails", "validateDataFields"})
    public void groupPostByUser(){
        int maximum = 0;
        for(Integer userId: postsByUser.keySet()){
            String userName = userMap.get(userId);
            int totalPosts = postsByUser.get(userId).size();
            System.out.println("User ID: " + userId);
            System.out.println("User Name: " + userName);
            System.out.println("Total Posts: " + totalPosts);
            System.out.println("--------------------------------");
//            Part 5: Post Count Validation
//            Validate that each user has exactly 10 posts.
            Assert.assertEquals(totalPosts,10,"Expected 10 posts for user " + userName + " but found " + totalPosts);
            maximum = Integer.max(maximum,totalPosts);
        }

//        Part 6: Maximum Posts
//        Find the user or users with the maximum number of posts.
        for(Integer userId: postsByUser.keySet()){
            String userName = userMap.get(userId);
            int totalPosts = postsByUser.get(userId).size();
            if(totalPosts == maximum) {
                System.out.println("User ID: " + userId);
                System.out.println("User Name: " + userName);
                System.out.println("Post Count: " + totalPosts);
            }
        }

    }

    //Part 7
    @Test(dependsOnMethods = {"validateUserDetails", "validateDataFields"})
    public void keywordSearchInPosts() {
        String keyword = "qui";
        keywordSearchCount = 0;

        for (Map<String, Object> post : postDetails) {
            String title = post.get("title").toString();
            String body = post.get("body").toString();

            if (title.contains(keyword) || body.contains(keyword)) {
                Integer postId = (Integer) post.get("id");
                Integer userId = (Integer) post.get("userId");
                String userName = userMap.get(userId);

                System.out.println("Post ID: " + postId);
                System.out.println("User ID: " + userId);
                System.out.println("User Name: " + userName);
                System.out.println("Title: " + title);
                System.out.println("--------------------------------");
                keywordSearchCount++;
            }
        }

        Assert.assertTrue(keywordSearchCount > 0, "At least one post should match the keyword: " + keyword);
    }

    //Part 8
    @Test(dependsOnMethods = {"validateUserDetails", "validateDataFields", "crossAPIValidation", "groupPostByUser", "keywordSearchInPosts"})
    public void finalSummary() {
        boolean usersHavingExactlyTenPosts = true;

        for (Integer userId : userMap.keySet()) {
            int totalPosts = postsByUser.getOrDefault(userId, Collections.emptyList()).size();
            if (totalPosts != 10) {
                usersHavingExactlyTenPosts = false;
                break;
            }
        }

        System.out.println("Total Users: " + userMap.size());
        System.out.println("Total Posts: " + postDetails.size());
        System.out.println("Users with valid post mapping: " + postsByUser.size());
        System.out.println("Users having exactly 10 posts: " + usersHavingExactlyTenPosts);
        System.out.println("Keyword Search Count: " + keywordSearchCount);
    }

}
