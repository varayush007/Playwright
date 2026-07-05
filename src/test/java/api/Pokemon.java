package api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;


public class Pokemon {


    public static  String fetchPokemonList(){
        String endpoint = "https://pokeapi.co/api/v2/pokemon?limit=151&offset=0";

        Response response1 = given()
                .relaxedHTTPSValidation()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response1.asString();
    }


    public static  String fetchPokemonFromID(int id ){
        String endpoint = "https://pokeapi.co/api/v2/pokemon/"+ id;

        Response response1 = given()
                .relaxedHTTPSValidation()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response1.asString();
    }




    public static void main(String[] args) throws IOException {
        String pokemonList = fetchPokemonList();
        JsonPath jsonPath = new JsonPath(pokemonList);
        List<Map<String, String>> results = jsonPath.getList("results");
        Map<String, String> pokemonMap = new HashMap<>();
        if (results.size() != 151) {
            throw new RuntimeException("Expected 151 entries but found " + results.size());
        }

        for (Map<String, String> pokemon : results) {
            String name = pokemon.get("name");
            String url = pokemon.get("url");

            Assert.assertNotNull(name, "Pokemon name is missing");
            Assert.assertFalse(name.isBlank(), "Pokemon name is blank");

            Assert.assertNotNull(url, "Pokemon url is missing for: " + name);
            Assert.assertFalse(url.isBlank(), "Pokemon url is blank for: " + name);

            Assert.assertTrue(
                    url.startsWith("https://pokeapi.co/api/v2/pokemon/"),
                    "Invalid url for " + name + ": " + url
            );
            pokemonMap.put(name, url);
            System.out.println("This case passed for " + name);
        }

        Set<Integer> excludedIds = Set.of(144, 145, 146, 150, 151);
        Map<String, Map<String, Object>> pokemonDetailsMap = new HashMap<>();
        for (Map.Entry<String, String> entry : pokemonMap.entrySet()) {
            String name = entry.getKey();
            String url = entry.getValue();

            String[] parts = url.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);

            if (excludedIds.contains(id)) {
                continue;
            }
            String pokemonById = fetchPokemonFromID(id);

            JsonPath pokemonJsonPath = new JsonPath(pokemonById);

            List<Map<String, Object>> stats = pokemonJsonPath.getList("stats");
            List<Map<String, Object>> types = pokemonJsonPath.getList("types");

            Map<String, Object> pokemonDetails = new HashMap<>();
            pokemonDetails.put("name", name);
            pokemonDetails.put("url", url);
            pokemonDetails.put("id", id);
            pokemonDetails.put("stats", stats);
            pokemonDetails.put("types", types);

            pokemonDetailsMap.put(name, pokemonDetails);
        }
        for (Map.Entry<String, Map<String, Object>> entry : pokemonDetailsMap.entrySet()) {
            Map<String, Object> details = entry.getValue();

            System.out.println("Name: " + details.get("name"));
            System.out.println("ID: " + details.get("id"));
            System.out.println("URL: " + details.get("url"));
            System.out.println("Stats: " + details.get("stats"));
            System.out.println("Types: " + details.get("types"));
            System.out.println("----------------------");
        }
    }
}
