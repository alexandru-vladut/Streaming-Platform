import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

//******************* JSON ObjectMapper preparing. **********************************
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());


//******************* Read from JSON. ***************************************
//        java.io.File File = Paths.get("checker/resources/in/basic_5.json").toFile();
        java.io.File File = Paths.get(args[0]).toFile();
        JsonNode jsonNode = objectMapper.readTree(File);

        String arrayString = jsonNode.get("users").toString();
        List<User> users = objectMapper.readValue(arrayString, new TypeReference<List<User>>() {});

        arrayString = jsonNode.get("movies").toString();
        List<Movie> movies = objectMapper.readValue(arrayString, new TypeReference<List<Movie>>() {});

        arrayString = jsonNode.get("actions").toString();
        List<Action> actions = objectMapper.readValue(arrayString, new TypeReference<List<Action>>() {});


//******************* Iterating through the actions. ************************************
        List<Output> outputs = new ArrayList<Output>(); // vom apela outputs.add(newOutput) cand e cazul
        String currentPage = new String("homepage neautentificat");
        currentUser currentUser = null;
        List<currentMovie> currentMovies = new ArrayList<>();

        for (Action action : actions) {
            System.out.println("currentPage: " + currentPage);
//          ******************** CHANGE PAGE ************************************************
            if (action.getType().equals("change page")) {

                System.out.println("Action is change page");
                System.out.println("getPage(): " + action.getPage());
                System.out.println("\n");

                String newPage = action.getPage();
//                boolean doError = false;
                switch (newPage) {
                    case "login":
                        if (currentPage.equals("homepage neautentificat")) {
                            currentPage = "login";
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "register":
                        if (currentPage.equals("homepage neautentificat")) {
                            currentPage = "register";
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "homepage autentificat":
                        if (currentPage.equals("movies") || currentPage.equals("see details") || currentPage.equals("upgrades")) {
                            currentPage = "homepage autentificat";
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "upgrades":
                        if (currentPage.equals("see details") || currentPage.equals("homepage autentificat")) {
                            currentPage = "upgrades";
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "logout":
                        if (currentPage.equals("see details") || currentPage.equals("movies") || currentPage.equals("upgrades") || currentPage.equals("homepage autentificat")) {
                            currentPage = "homepage neautentificat";
                            currentUser = null;
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "movies":
                        if (currentPage.equals("homepage autentificat") || currentPage.equals("see details") || currentPage.equals("upgrades")) {
                            currentPage = "movies";

                            for (Movie movie : movies) {
                                boolean isBanned = false;
                                for (String bannedCountry : movie.getCountriesBanned()) {
                                    if (currentUser.getCredentials().getCountry().equals(bannedCountry)) {
                                        isBanned = true;
                                        break;
                                    }
                                }

                                if (!isBanned) {
                                    currentMovie currentMovie = new currentMovie(movie.getName(), movie.getYear(), movie.getDuration(),
                                            movie.getGenres(), movie.getActors(), movie.getCountriesBanned());
                                    currentMovies.add(currentMovie);
                                }
                            }

                            Output currentOutput = new Output(null, currentMovies, currentUser);
                            outputs.add(currentOutput);
                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "see details":
                        if (currentPage.equals("movies")) {
                            currentPage = "see details";

                            System.out.println("currentMovies.size() before: " + currentMovies.size());
                            int index = -1;
                            for (int i = 0; i < currentMovies.size(); i++) {
                                if (action.getMovie().equals(currentMovies.get(i).getName())) {
                                    index = i;
                                    break;
                                }
                            }
                            System.out.println("currentMovies.size() after: " + currentMovies.size());
                            System.out.println("index: " + index);

                            if (index != -1) {
                                List<currentMovie> seeDetailsMovie = new ArrayList<>();
                                seeDetailsMovie.add(currentMovies.get(index));

                                Output currentOutput = new Output(null, seeDetailsMovie, currentUser);
                                outputs.add(currentOutput);
                            } else {
                                Output currentOutput = new Output("Error", Collections.emptyList(), null);
                                outputs.add(currentOutput);
                            }

                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                            outputs.add(currentOutput);
                        }
                        break;

                    default:
                        Output currentOutput = new Output("Error", Collections.emptyList(), currentUser);
                        outputs.add(currentOutput);
                        break;

                }

//                if (doError) {
//                    Output currentOutput = new Output("Error", currentMovies, currentUser);
//                    outputs.add(currentOutput);
//                }


            }

//          *********************** ON PAGE ************************************************
            else if (action.getType().equals("on page")){
                System.out.println("Action is on page");
                System.out.println("getFeature(): " + action.getFeature());
                System.out.println("\n");

                String feature = action.getFeature();
                switch (feature) {
                    case "register":
                        if (currentPage.equals("register")) {

                            Credentials newCredentials = action.getCredentials();
                            String newName = newCredentials.getName();

                            boolean alreadyExists = false;
                            for (User user : users) {
                                if (newName.equals(user.getCredentials().getName())) {
                                    alreadyExists = true;
                                    break;
                                }
                            }

                            if (!alreadyExists) {
                                User newUser = new User(newCredentials);
                                users.add(newUser);

                                currentUser = new currentUser(newCredentials);

                                currentPage = "homepage autentificat";

                                Output currentOutput = new Output(null, Collections.emptyList(), currentUser);
                                outputs.add(currentOutput);

                            } else {
                                currentPage = "homepage neautentificat";

                                Output currentOutput = new Output("Error", Collections.emptyList(), null);
                                outputs.add(currentOutput);
                            }

                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "login":
//                        System.out.println("am ajuns aici");
                        if (currentPage.equals("login")) {
//                            System.out.println("am ajuns aici2");
                            Credentials loginCredentials = action.getCredentials();
                            String loginName = loginCredentials.getName();
                            String loginPassword = loginCredentials.getPassword();

                            boolean authSuccess = false;
                            for (User user : users) {
                                if (loginName.equals(user.getCredentials().getName()) &&
                                        loginPassword.equals(user.getCredentials().getPassword())) {
                                    authSuccess = true;
                                    currentUser = new currentUser(user.getCredentials());
                                    break;
                                }
                            }

                            if (authSuccess) {

                                currentPage = "homepage autentificat";

                                Output currentOutput = new Output(null, Collections.emptyList(), currentUser);
                                outputs.add(currentOutput);

                            } else {
                                currentPage = "homepage neautentificat";

                                Output currentOutput = new Output("Error", Collections.emptyList(), null);
                                outputs.add(currentOutput);
                            }


                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "search":

                        if (currentPage.equals("movies")) {

                            int length = action.getStartsWith().length();

                            List<currentMovie> currentMoviesCopy = new ArrayList<>(currentMovies);
                            Iterator<currentMovie> i = currentMoviesCopy.iterator();
                            while (i.hasNext()) {
                                currentMovie currentMovie = i.next();

                                if (currentMovie.getName().length() >= length) {
                                    String nameStartsWith = currentMovie.getName().substring(0, length);

                                    if (!nameStartsWith.equals(action.getStartsWith())) {
                                        i.remove();
                                    }
                                } else {
                                    i.remove();
                                }
                            }

                            Output currentOutput = new Output(null, currentMoviesCopy, currentUser);
                            outputs.add(currentOutput);

                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    case "filter":
                        if (currentPage.equals("movies")) {

                            List<currentMovie> currentMoviesCopy = new ArrayList<>(currentMovies);

                            Contains contains = action.getFilters().getContains();
                            if (contains != null) {
                                if (contains.getActors() != null) {
                                    Iterator<currentMovie> i = currentMoviesCopy.iterator();
                                    while (i.hasNext()) {
                                        currentMovie currentMovie = i.next();

                                        List<String> movieActors = currentMovie.getActors();
                                        boolean hasAllActors = true;
                                        for (String actor : contains.getActors()) {
                                            if (!movieActors.contains(actor)) {
                                                hasAllActors = false;
                                                break;
                                            }
                                        }

                                        if (!hasAllActors) {
                                            i.remove();
                                        }
                                    }
                                }

                                if (contains.getGenre() != null) {
                                    Iterator<currentMovie> i = currentMoviesCopy.iterator();
                                    while (i.hasNext()) {
                                        currentMovie currentMovie = i.next();

                                        List<String> movieGenres = currentMovie.getGenres();
                                        boolean hasAllGenres = true;
                                        for (String genre : contains.getGenre()) {
                                            if (!movieGenres.contains(genre)) {
                                                hasAllGenres = false;
                                                break;
                                            }
                                        }

                                        if (!hasAllGenres) {
                                            i.remove();
                                        }
                                    }
                                }
                            }

                            Sort sort = action.getFilters().getSort();
                            if (sort != null) {
                                if (sort.getRating().equals("increasing") && sort.getDuration() == null) {
                                    currentMoviesCopy.sort(new increasingNull());
                                }
                                else if (sort.getRating().equals("decreasing") && sort.getDuration() == null) {
                                    currentMoviesCopy.sort(new decreasingNull());
                                }
                                else if (sort.getRating() == null && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopy.sort(new nullIncreasing());
                                }
                                else if (sort.getRating() == null && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopy.sort(new nullDecreasing());
                                }
                                else if (sort.getRating().equals("increasing") && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopy.sort(new increasingIncreasing());
                                }
                                else if (sort.getRating().equals("increasing") && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopy.sort(new increasingDecreasing());
                                }
                                else if (sort.getRating().equals("decreasing") && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopy.sort(new decreasingIncreasing());
                                }
                                else if (sort.getRating().equals("decreasing") && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopy.sort(new decreasingDecreasing());
                                }
                            }

                            Output currentOutput = new Output(null, currentMoviesCopy, currentUser);
                            outputs.add(currentOutput);

                        } else {
                            Output currentOutput = new Output("Error", Collections.emptyList(), null);
                            outputs.add(currentOutput);
                        }
                        break;

                    default:
                        break;
                }

            } else {
                // EROARE, nu e de tip "change page" sau "on page"
                Output currentOutput = new Output("Error", Collections.emptyList(), null);
                outputs.add(currentOutput);
            }
        }

//******************* Write to JSON. ********************************************
//        java.io.File resultFile = Paths.get("redundantFiles/outputExample.json").toFile();
//        objectWriter.writeValue(resultFile, outputs);

        System.out.println(args[0]);
        objectWriter.writeValue(new File(args[1]), outputs);

    }
}

// rating: increasing
// duration: increasing
class increasingIncreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            if (cM1.getDuration() == cM2.getDuration()) {
                return 0;
            }
            else if (cM1.getDuration() < cM2.getDuration()) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

// rating: increasing
// duration: decreasing
class increasingDecreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            if (cM1.getDuration() == cM2.getDuration()) {
                return 0;
            }
            else if (cM1.getDuration() < cM2.getDuration()) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

// rating: decreasing
// duration: increasing
class decreasingIncreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            if (cM1.getDuration() == cM2.getDuration()) {
                return 0;
            }
            else if (cM1.getDuration() < cM2.getDuration()) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}

// rating: decreasing
// duration: decreasing
class decreasingDecreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            if (cM1.getDuration() == cM2.getDuration()) {
                return 0;
            }
            else if (cM1.getDuration() < cM2.getDuration()) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}

// rating: increasing
// duration: null
class increasingNull implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            return 0;
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

// rating: decreasing
// duration: null
class decreasingNull implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getRating() == cM2.getRating()) {
            return 0;
        }
        else if (cM1.getRating() < cM2.getRating()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}

// rating: null
// duration: increasing
class nullIncreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getDuration() == cM2.getDuration()) {
            return 0;
        }
        else if (cM1.getDuration() < cM2.getDuration()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

// rating: null
// duration: decreasing
class nullDecreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getDuration() == cM2.getDuration()) {
            return 0;
        }
        else if (cM1.getDuration() < cM2.getDuration()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}


