package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie.Movie;
import movie.InputMovie;
import user.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public final class Database {
    private List<User> users = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();

    public List<Action> initDatabaseFromJSON(String filePath) throws IOException {

        /*
          JSON ObjectMapper preparing.
         */
        ObjectMapper objectMapper = new ObjectMapper();

        /*
          Read from JSON.
          java.io.File File = Paths.get("checker/resources/in/basic_10.json").toFile();
         */
        java.io.File myFile = Paths.get(filePath).toFile();
        JsonNode jsonNode = objectMapper.readTree(myFile);

        String arrayString = jsonNode.get("users").toString();
        List<InputUser> inputUsers = objectMapper.readValue(arrayString, new TypeReference<>() {});

        for (InputUser inputUser : inputUsers) {
            users.add(new User(inputUser.getCredentials()));
        }

        arrayString = jsonNode.get("movies").toString();
        List<InputMovie> inputMovies = objectMapper.readValue(arrayString, new TypeReference<>() {});

        for (InputMovie movie : inputMovies) {
            movies.add(new Movie(movie));
        }

        arrayString = jsonNode.get("actions").toString();

        return objectMapper.readValue(arrayString, new TypeReference<>() {});
    }

    public void addUser(Credentials credentials) {
        users.add(new User(credentials));
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
