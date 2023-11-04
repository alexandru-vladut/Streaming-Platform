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

    public void initUsers(List<InputUser> inputUsers) {
        for (InputUser inputUser : inputUsers) {
            users.add(new User(inputUser.getCredentials()));
        }
    }

    public void initMovies(List<InputMovie> inputMovies) {
        for (InputMovie movie : inputMovies) {
            movies.add(new Movie(movie));
        }
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
