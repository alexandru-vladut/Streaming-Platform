import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Integer.parseInt;

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
        List<Output> outputs = new ArrayList<Output>();
        String currentPage = new String("homepage neautentificat");
        currentUser currentUser = null;
        List<currentMovie> currentMovies = new ArrayList<>();

        HashMap<String, Double> ratingsSum = new HashMap<String, Double>();
        for (Movie movie : movies) {
            ratingsSum.put(movie.getName(), 0.00);
        }

        HashMap<String, Integer> ratingsNum = new HashMap<String, Integer>();
        for (Movie movie : movies) {
            ratingsNum.put(movie.getName(), 0);
        }

        currentMovie actualMovie = null;

        for (Action action : actions) {

//          ******************** CHANGE PAGE ************************************************
            if (action.getType().equals("change page")) {

                String newPage = action.getPage();
                switch (newPage) {
                    case "login":
                        if (!currentPage.equals("homepage neautentificat")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "login";
                        break;

                    case "register":
                        if (!currentPage.equals("homepage neautentificat")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "register";
                        break;

                    case "homepage autentificat":
                        if (!currentPage.equals("movies") && !currentPage.equals("see details") && !currentPage.equals("upgrades")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "homepage autentificat";
                        break;

                    case "upgrades":
                        if (!currentPage.equals("see details") && !currentPage.equals("homepage autentificat")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "upgrades";
                        break;

                    case "logout":
                        if (!currentPage.equals("see details") && !currentPage.equals("movies") &&
                                !currentPage.equals("upgrades") && !currentPage.equals("homepage autentificat")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "homepage neautentificat";
                        currentUser = null;
                        break;

                    case "movies":
                        if (!currentPage.equals("homepage autentificat") &&
                                !currentPage.equals("see details") && !currentPage.equals("upgrades")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "movies";

                        List<currentMovie> resetMovies = new ArrayList<>();

                        for (Movie movie : movies) {
                            boolean isBanned = false;
                            for (String bannedCountry : movie.getCountriesBanned()) {
                                if (currentUser.getCredentials().getCountry().equals(bannedCountry)) {
                                    isBanned = true;
                                    break;
                                }
                            }

                            if (!isBanned) {
                                boolean ok = false;
                                for (currentMovie movie1 : currentMovies) {
                                    if (movie.getName().equals(movie1.getName())) {

                                        ok = true;
                                        currentMovie currentMovie = new currentMovie(movie1);
                                        resetMovies.add(currentMovie);

                                        break;
                                    }
                                }

                                if (!ok) {

                                    currentMovie currentMovie = new currentMovie(movie);
                                    resetMovies.add(currentMovie);
                                }

                            }
                        }

                        successOutput(outputs, resetMovies, currentUser);

                        currentMovies = new ArrayList<>();
                        for (currentMovie movie : resetMovies) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMovies.add(movieCopy);
                        }
                        break;

                    case "see details":
                        if (!currentPage.equals("movies")) {
                            errorOutput(outputs);
                            break;
                        }

                        int index = -1;
                        for (int i = 0; i < currentMovies.size(); i++) {
                            if (action.getMovie().equals(currentMovies.get(i).getName())) {

                                index = i;
                                actualMovie = new currentMovie(currentMovies.get(i));
                                break;
                            }
                        }

                        if (index == -1) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "see details";

                        List<currentMovie> seeDetailsMovie = new ArrayList<>();
                        seeDetailsMovie.add(currentMovies.get(index));

                        successOutput(outputs, seeDetailsMovie, currentUser);

                        currentMovies = new ArrayList<>();
                        for (currentMovie movie : seeDetailsMovie) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMovies.add(movieCopy);
                        }
                        break;

                    default:
                        errorOutput(outputs);
                        break;

                }

            }

//          *********************** ON PAGE ************************************************
            else if (action.getType().equals("on page")){

                String feature = action.getFeature();
                switch (feature) {
                    case "register":
                        if (!currentPage.equals("register")) {
                            errorOutput(outputs);
                            break;
                        }

                        Credentials newCredentials = action.getCredentials();
                        String newName = newCredentials.getName();

                        boolean alreadyExists = false;
                        for (User user : users) {
                            if (newName.equals(user.getCredentials().getName())) {
                                alreadyExists = true;
                                break;
                            }
                        }

                        if (alreadyExists) {
                            currentPage = "homepage neautentificat";
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "homepage autentificat";

                        User newUser = new User(newCredentials);
                        users.add(newUser);

                        currentUser = new currentUser(newCredentials);

                        successOutput(outputs, currentMovies, currentUser);
                        break;

                    case "login":
                        if (!currentPage.equals("login")) {
                            errorOutput(outputs);
                            break;
                        }

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

                        if (!authSuccess) {
                            currentPage = "homepage neautentificat";
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "homepage autentificat";

                        successOutput(outputs, Collections.emptyList(), currentUser);
                        break;

                    case "search":
                        if (!currentPage.equals("movies")) {
                            errorOutput(outputs);
                            break;
                        }

                        int length = action.getStartsWith().length();

                        List<currentMovie> currentMoviesCopyForSearch = new ArrayList<>();
                        for (currentMovie movie : currentMovies) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMoviesCopyForSearch.add(movieCopy);
                        }

                        Iterator<currentMovie> iForSearch = currentMoviesCopyForSearch.iterator();
                        while (iForSearch.hasNext()) {
                            currentMovie currentMovie = iForSearch.next();

                            if (currentMovie.getName().length() >= length) {
                                String nameStartsWith = currentMovie.getName().substring(0, length);

                                if (!nameStartsWith.equals(action.getStartsWith())) {
                                    iForSearch.remove();
                                }
                            } else {
                                iForSearch.remove();
                            }
                        }

                        successOutput(outputs, currentMoviesCopyForSearch, currentUser);
                        break;

                    case "filter":
                        if (!currentPage.equals("movies")) {
                            errorOutput(outputs);
                            break;
                        }

                        List<currentMovie> currentMoviesCopyForFilter = new ArrayList<>();
                        for (currentMovie movie : currentMovies) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMoviesCopyForFilter.add(movieCopy);
                        }

                        Contains contains = action.getFilters().getContains();
                        if (contains != null) {
                            if (contains.getActors() != null) {
                                Iterator<currentMovie> iForContains = currentMoviesCopyForFilter.iterator();
                                while (iForContains.hasNext()) {
                                    currentMovie currentMovie = iForContains.next();

                                    List<String> movieActors = currentMovie.getActors();
                                    boolean hasAllActors = true;
                                    for (String actor : contains.getActors()) {
                                        if (!movieActors.contains(actor)) {
                                            hasAllActors = false;
                                            break;
                                        }
                                    }

                                    if (!hasAllActors) {
                                        iForContains.remove();
                                    }
                                }
                            }

                            if (contains.getGenre() != null) {
                                Iterator<currentMovie> iForContains = currentMoviesCopyForFilter.iterator();
                                while (iForContains.hasNext()) {
                                    currentMovie currentMovie = iForContains.next();

                                    List<String> movieGenres = currentMovie.getGenres();
                                    boolean hasAllGenres = true;
                                    for (String genre : contains.getGenre()) {
                                        if (!movieGenres.contains(genre)) {
                                            hasAllGenres = false;
                                            break;
                                        }
                                    }

                                    if (!hasAllGenres) {
                                        iForContains.remove();
                                    }
                                }
                            }
                        }

                        Sort sort = action.getFilters().getSort();
                        if (sort != null) {
                            if (sort.getRating().equals("increasing") && sort.getDuration() == null) {
                                currentMoviesCopyForFilter.sort(new increasingNull());
                            }
                            else if (sort.getRating().equals("decreasing") && sort.getDuration() == null) {
                                currentMoviesCopyForFilter.sort(new decreasingNull());
                            }
                            else if (sort.getRating() == null && sort.getDuration().equals("increasing")) {
                                currentMoviesCopyForFilter.sort(new nullIncreasing());
                            }
                            else if (sort.getRating() == null && sort.getDuration().equals("decreasing")) {
                                currentMoviesCopyForFilter.sort(new nullDecreasing());
                            }
                            else if (sort.getRating().equals("increasing") && sort.getDuration().equals("increasing")) {
                                currentMoviesCopyForFilter.sort(new increasingIncreasing());
                            }
                            else if (sort.getRating().equals("increasing") && sort.getDuration().equals("decreasing")) {
                                currentMoviesCopyForFilter.sort(new increasingDecreasing());
                            }
                            else if (sort.getRating().equals("decreasing") && sort.getDuration().equals("increasing")) {
                                currentMoviesCopyForFilter.sort(new decreasingIncreasing());
                            }
                            else if (sort.getRating().equals("decreasing") && sort.getDuration().equals("decreasing")) {
                                currentMoviesCopyForFilter.sort(new decreasingDecreasing());
                            }
                        }

                        successOutput(outputs, currentMoviesCopyForFilter, currentUser);

                        currentMovies = new ArrayList<>();
                        for (currentMovie movie : currentMoviesCopyForFilter) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMovies.add(movieCopy);
                        }
                        break;

                    case "buy tokens":
                        if (!currentPage.equals("upgrades")) {
                            errorOutput(outputs);
                            break;
                        }

                        Credentials credentialsCopyForTokens = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForTokens = new currentUser(credentialsCopyForTokens, currentUser);

                        int tokensWanted = parseInt(action.getCount());
                        int currentBalanceForTokens = parseInt(currentUserCopyForTokens.getCredentials().getBalance());
                        int currentTokensForTokens = currentUserCopyForTokens.getTokensCount();

                        if (currentBalanceForTokens < tokensWanted) {
                            errorOutput(outputs);
                            break;
                        }

                        currentBalanceForTokens -= tokensWanted;
                        currentTokensForTokens += tokensWanted;

                        currentUserCopyForTokens.getCredentials().setBalance(Integer.toString(currentBalanceForTokens));
                        currentUserCopyForTokens.setTokensCount(currentTokensForTokens);

                        Credentials credentialsForTokens = new Credentials(currentUserCopyForTokens.getCredentials());
                        currentUser = new currentUser(credentialsForTokens, currentUserCopyForTokens);
                        break;

                    case "buy premium account":
                        if (!currentPage.equals("upgrades")) {
                            errorOutput(outputs);
                            break;
                        }

                        if (!currentUser.getCredentials().getAccountType().equals("standard")) {
                            errorOutput(outputs);
                            break;
                        }

                        Credentials credentialsCopyForPremium = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForPremium = new currentUser(credentialsCopyForPremium, currentUser);

                        int currentTokensForPremium = currentUserCopyForPremium.getTokensCount();

                        if (currentTokensForPremium < 10) {
                            errorOutput(outputs);
                            break;
                        }

                        currentTokensForPremium -= 10;
                        currentUserCopyForPremium.setTokensCount(currentTokensForPremium);
                        currentUserCopyForPremium.getCredentials().setAccountType("premium");

                        Credentials credentialsForPremium = new Credentials(currentUserCopyForPremium.getCredentials());
                        currentUser = new currentUser(credentialsForPremium, currentUserCopyForPremium);
                        break;

                    case "purchase":
                        if (!currentPage.equals("see details")) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getMovie() != null && !action.getMovie().equals(actualMovie.getName())) {
                            errorOutput(outputs);
                            break;
                        }

                        boolean found = false;
                        for (currentMovie movie : currentUser.getPurchasedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            errorOutput(outputs);
                            break;
                        }

                        Credentials credentialsCopyForPurchase = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForPurchase = new currentUser(credentialsCopyForPurchase, currentUser);

                        if (currentUserCopyForPurchase.getCredentials().getAccountType().equals("standard")) {

                            int currentTokens = currentUserCopyForPurchase.getTokensCount();

                            if (currentTokens < 2) {
                                errorOutput(outputs);
                                break;
                            }

                            currentTokens -= 2;
                            currentUserCopyForPurchase.setTokensCount(currentTokens);

                            currentUserCopyForPurchase.getPurchasedMovies().add(actualMovie);

                            successOutput(outputs, currentMovies, currentUserCopyForPurchase);

                            Credentials credentialsForPurchase = new Credentials(currentUserCopyForPurchase.getCredentials());
                            currentUser = new currentUser(credentialsForPurchase, currentUserCopyForPurchase);

                        } else if (currentUserCopyForPurchase.getCredentials().getAccountType().equals("premium")) {

                            int currentNumFreePremiumMovies = currentUserCopyForPurchase.getNumFreePremiumMovies();
                            int currentTokens = currentUserCopyForPurchase.getTokensCount();

                            if (currentNumFreePremiumMovies == 0 && currentTokens < 2) {
                                errorOutput(outputs);
                                break;
                            }

                            if (currentNumFreePremiumMovies > 0) {

                                currentNumFreePremiumMovies -= 1;
                                currentUserCopyForPurchase.setNumFreePremiumMovies(currentNumFreePremiumMovies);

                            } else if (currentTokens >= 2) {

                                currentTokens -= 2;
                                currentUserCopyForPurchase.setTokensCount(currentTokens);

                            }

                            currentUserCopyForPurchase.getPurchasedMovies().add(actualMovie);

                            successOutput(outputs, currentMovies, currentUserCopyForPurchase);

                            Credentials credentialsForPurchase = new Credentials(currentUserCopyForPurchase.getCredentials());
                            currentUser = new currentUser(credentialsForPurchase, currentUserCopyForPurchase);
                        }
                        break;

                    case "watch":
                        if (!currentPage.equals("see details")) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getMovie() != null && !action.getMovie().equals(actualMovie.getName())) {
                            errorOutput(outputs);
                            break;
                        }

                        boolean purchasedMovie = false;
                        for (currentMovie movie : currentUser.getPurchasedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {
                                purchasedMovie = true;
                                break;
                            }
                        }

                        if (!purchasedMovie) {
                            errorOutput(outputs);
                            break;
                        }

                        Credentials credentialsCopyForWatch = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForWatch = new currentUser(credentialsCopyForWatch, currentUser);

                        currentUserCopyForWatch.getWatchedMovies().add(actualMovie);

                        successOutput(outputs, currentMovies, currentUserCopyForWatch);

                        Credentials credentialsForWatch = new Credentials(currentUserCopyForWatch.getCredentials());
                        currentUser = new currentUser(credentialsForWatch, currentUserCopyForWatch);
                        break;

                    case "like":
                        if (!currentPage.equals("see details")) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getMovie() != null && !action.getMovie().equals(actualMovie.getName())) {
                            errorOutput(outputs);
                            break;
                        }

                        boolean likedMovie = false;
                        for (currentMovie movie : currentUser.getLikedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {
                                likedMovie = true;
                                break;
                            }
                        }

                        if (likedMovie) {
                            errorOutput(outputs);
                            break;
                        }

                        List<currentMovie> currentMoviesCopyForLike = new ArrayList<>();
                        for (currentMovie movie : currentMovies) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMoviesCopyForLike.add(movieCopy);
                        }

                        Credentials credentialsCopyForLike = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForLike = new currentUser(credentialsCopyForLike, currentUser);

                        int currentNumLikes = 0;
                        boolean watchedMovieForLike = false;
                        for (currentMovie movie : currentUserCopyForLike.getWatchedMovies()) {

                            if (actualMovie.getName().equals(movie.getName())) {

                                watchedMovieForLike = true;

                                currentNumLikes = movie.getNumLikes();
                                currentNumLikes += 1;
                                movie.setNumLikes(currentNumLikes);

                                break;
                            }
                        }

                        if (!watchedMovieForLike) {
                            errorOutput(outputs);
                            break;
                        }

                        for (currentMovie movie : currentUserCopyForLike.getPurchasedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumLikes(currentNumLikes);
                                break;
                            }
                        }

                        for (currentMovie movie : currentMoviesCopyForLike) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumLikes(currentNumLikes);
                                break;
                            }
                        }

                        for (currentMovie movie : currentUserCopyForLike.getRatedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumLikes(currentNumLikes);
                                break;
                            }
                        }

                        currentMovie actualMovieCopyForLike = new currentMovie(actualMovie);
                        actualMovieCopyForLike.setNumLikes(currentNumLikes);
                        currentUserCopyForLike.getLikedMovies().add(actualMovieCopyForLike);
                        actualMovie = new currentMovie(actualMovieCopyForLike);

                        successOutput(outputs, currentMoviesCopyForLike, currentUserCopyForLike);

                        Credentials credentialsForLike = new Credentials(currentUserCopyForLike.getCredentials());
                        currentUser = new currentUser(credentialsForLike, currentUserCopyForLike);

                        currentMovies = new ArrayList<>();
                        for (currentMovie movie : currentMoviesCopyForLike) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMovies.add(movieCopy);
                        }

                        break;

                    case "rate":
                        if (!currentPage.equals("see details")) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getMovie() != null && !action.getMovie().equals(actualMovie.getName())) {
                            errorOutput(outputs);
                            break;
                        }

                        boolean ratedMovie = false;
                        for (currentMovie movie : currentUser.getRatedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {
                                ratedMovie = true;
                                break;
                            }
                        }

                        if (ratedMovie) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getRate() < 1 || action.getRate() > 5) {
                            errorOutput(outputs);
                            break;
                        }

                        List<currentMovie> currentMoviesCopyForRate = new ArrayList<>();
                        for (currentMovie movie : currentMovies) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMoviesCopyForRate.add(movieCopy);
                        }

                        Credentials credentialsCopyForRate = new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForRate = new currentUser(credentialsCopyForRate, currentUser);

                        Double currentRatingsSum = ratingsSum.get(actualMovie.getName());
                        currentRatingsSum += action.getRate();
                        ratingsSum.put(actualMovie.getName(), currentRatingsSum);

                        Integer currentRatingsNum = ratingsNum.get(actualMovie.getName());
                        currentRatingsNum += 1;
                        ratingsNum.put(actualMovie.getName(), currentRatingsNum);

                        double newRating = currentRatingsSum / (double) currentRatingsNum;

                        boolean watchedMovieForRate = false;
                        for (currentMovie movie : currentUserCopyForRate.getWatchedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                watchedMovieForRate = true;
                                movie.setNumRatings(currentRatingsNum);
                                movie.setRating(newRating);

                                break;
                            }
                        }

                        if (!watchedMovieForRate) {
                            errorOutput(outputs);
                            break;
                        }

                        for (currentMovie movie2 : currentUserCopyForRate.getPurchasedMovies()) {
                            if (actualMovie.getName().equals(movie2.getName())) {

                                movie2.setNumRatings(currentRatingsNum);
                                movie2.setRating(newRating);
                                break;
                            }
                        }

                        for (currentMovie movie4 : currentMoviesCopyForRate) {
                            if (actualMovie.getName().equals(movie4.getName())) {

                                movie4.setNumRatings(currentRatingsNum);
                                movie4.setRating(newRating);
                                break;
                            }
                        }

                        for (currentMovie movie5 : currentUserCopyForRate.getLikedMovies()) {
                            if (actualMovie.getName().equals(movie5.getName())) {

                                movie5.setNumRatings(currentRatingsNum);
                                movie5.setRating(newRating);
                                break;
                            }
                        }

                        currentMovie actualMovieCopyForRate = new currentMovie(actualMovie);
                        actualMovieCopyForRate.setNumRatings(currentRatingsNum);
                        actualMovieCopyForRate.setRating(newRating);
                        currentUserCopyForRate.getRatedMovies().add(actualMovieCopyForRate);
                        actualMovie = new currentMovie(actualMovieCopyForRate);

                        successOutput(outputs, currentMoviesCopyForRate, currentUserCopyForRate);

                        Credentials credentialsForRate = new Credentials(currentUserCopyForRate.getCredentials());
                        currentUser = new currentUser(credentialsForRate, currentUserCopyForRate);

                        currentMovies = new ArrayList<>();
                        for (currentMovie movie : currentMoviesCopyForRate) {
                            currentMovie movieCopy = new currentMovie(movie);
                            currentMovies.add(movieCopy);
                        }
                        break;

                    default:
                        break;
                }

            } else {
                // EROARE, nu e de tip "change page" sau "on page"
                errorOutput(outputs);
            }
        }

//******************* Write to JSON. ********************************************
//        java.io.File resultFile = Paths.get("redundantFiles/outputExample.json").toFile();
//        objectWriter.writeValue(resultFile, outputs);

//        System.out.println(args[0]);
        objectWriter.writeValue(new File(args[1]), outputs);

    }

    public static void errorOutput(List<Output> outputs) {
        Output currentOutput = new Output("Error", Collections.emptyList(), null);
        outputs.add(currentOutput);
    }

    public static void successOutput(List<Output> outputs, List<currentMovie> currentMovies, currentUser currentUser) {
        Output currentOutput = new Output(null, currentMovies, currentUser);
        outputs.add(currentOutput);
    }

}

// rating: increasing
// duration: increasing
class increasingIncreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getDuration() == cM2.getDuration()) {

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
        else if (cM1.getDuration() < cM2.getDuration()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}

// rating: increasing
// duration: decreasing
class increasingDecreasing implements Comparator<currentMovie> {
    public int compare(currentMovie cM1, currentMovie cM2)
    {
        if (cM1.getDuration() == cM2.getDuration()) {
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
        else if (cM1.getDuration() < cM2.getDuration()) {
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
        if (cM1.getDuration() == cM2.getDuration()) {
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
        else if (cM1.getDuration() < cM2.getDuration()) {
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
    public int compare(currentMovie cM1, currentMovie cM2) {
        if (cM1.getDuration() == cM2.getDuration()) {
            if (cM1.getRating() == cM2.getRating()) {
                return 0;
            } else if (cM1.getRating() < cM2.getRating()) {
                return 1;
            } else {
                return -1;
            }
        } else if (cM1.getDuration() < cM2.getDuration()) {
            return 1;
        } else {
            return -1;
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
            return -1;
        }
        else {
            return 1;
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
            return 1;
        }
        else {
            return -1;
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
            return -1;
        }
        else {
            return 1;
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
            return 1;
        }
        else {
            return -1;
        }
    }
}


