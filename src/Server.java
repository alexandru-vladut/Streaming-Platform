import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import movie.InputMovie;
import movie.Movie;
import movie.OutputMovie;
import user.*;
import utils.Action;
import utils.Database;
import utils.Filters;
import utils.Output;
import utils.sortingComparators.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Server {

    static Database database;
    static List<Action> actions;
    static String currentPage;
    static Stack<String> pagesStack;
    static User connectedUser;
    static List<Movie> displayedMovies;
    static Movie actualMovie;
    static List<Output> outputs;

    public static void resetCurrentInfo() {
        currentPage = "homepage neautentificat";
        outputs = new ArrayList<>();
        pagesStack = new Stack<>();
        connectedUser = null;
        displayedMovies = new ArrayList<>();
        actualMovie = null;
    }

    public static void readFromJSON(String filePath) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        java.io.File myFile = Paths.get(filePath).toFile();
        JsonNode jsonNode = objectMapper.readTree(myFile);

        database = new Database();

        String arrayString = jsonNode.get("users").toString();
        List<InputUser> inputUsers = objectMapper.readValue(arrayString, new TypeReference<>() {});
        database.initUsers(inputUsers);

        arrayString = jsonNode.get("movies").toString();
        List<InputMovie> inputMovies = objectMapper.readValue(arrayString, new TypeReference<>() {});
        database.initMovies(inputMovies);

        arrayString = jsonNode.get("actions").toString();
        actions = objectMapper.readValue(arrayString, new TypeReference<>() {});
    }

    public static void writeToJSON(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        java.io.File resultFile = Paths.get(filePath).toFile();
        objectWriter.writeValue(resultFile, outputs);
    }

    public static void generateErrorOutput() {
        Output currentOutput = new Output("Error", Collections.emptyList(), null);
        outputs.add(currentOutput);
    }

    public static void generateSuccessOutput(List<Movie> movies, User user) {

        List<OutputMovie> outputMovies = new ArrayList<>();
        for (Movie movie : movies) {
            outputMovies.add(new OutputMovie(movie));
        }

        OutputUser outputUser = new OutputUser(user);

        Output currentOutput = new Output(null, outputMovies, outputUser);
        outputs.add(currentOutput);
    }

    public static List<Movie> notBanned(List<Movie> movies, User user) {
        List<Movie> notBannedMovies = new ArrayList<>();
        for (Movie movie : movies) {
            boolean isBanned = false;
            for (String bannedCountry : movie.getCountriesBanned()) {
                String aux = user.getCredentials().getCountry();
                if (aux.equals(bannedCountry)) {
                    isBanned = true;
                    break;
                }
            }

            if (!isBanned) {
                notBannedMovies.add(new Movie(movie));
            }
        }
        return notBannedMovies;
    }

    public static void iterateThroughActions() {

        for (Action action : Server.actions) {

            switch (action.getType()) {

                case "change page" -> {
                    String newPage = action.getPage();
                    switch (newPage) {
                        case "login" -> changePageLogin();
                        case "register" -> changePageRegister();
                        case "homepage autentificat" -> changePageHomepage();
                        case "upgrades" -> changePageUpgrades();
                        case "logout" -> changePageLogout();
                        case "movies" -> changePageMovies();
                        case "see details" -> changePageSeeDetails(action);
                        default -> generateErrorOutput();
                    }
                }

                case "on page" -> {
                    String feature = action.getFeature();
                    switch (feature) {
                        case "register" -> onPageRegister(action);
                        case "login" -> onPageLogin(action);
                        case "search" -> onPageSearch(action);
                        case "filter" -> onPageFilter(action);
                        case "buy tokens" -> onPageBuyTokens(action);
                        case "buy premium account" -> onPageBuyPremiumAccount();
                        case "purchase" -> onPagePurchase(action);
                        case "watch" -> onPageWatch(action);
                        case "like" -> onPageLike(action);
                        case "rate" -> onPageRate(action);
                        case "subscribe" -> onPageSubscribe(action);
                        default -> generateErrorOutput();
                    }
                }

                case "back" -> back(action);

                case "database" -> {
                    String feature = action.getFeature();
                    switch (feature) {
                        case "add" -> databaseAdd(action);
                        case "delete" -> databaseDelete(action);
                        default -> generateErrorOutput();
                    }
                }

                default -> generateErrorOutput();
            }
        }
    }

    public static void changePageLogin() {
        if (!currentPage.equals("homepage neautentificat")) {
            generateErrorOutput();
            return;
        }

        currentPage = "login";
    }

    public static void changePageRegister() {
        if (!currentPage.equals("homepage neautentificat")) {
            generateErrorOutput();
            return;
        }

        currentPage = "register";
    }

    public static void changePageHomepage() {
        if (!currentPage.equals("movies")
                && !currentPage.equals("see details")
                && !currentPage.equals("upgrades")) {
            generateErrorOutput();
            return;
        }

        currentPage = "homepage autentificat";
        pagesStack.push(currentPage);
    }

    public static void changePageUpgrades() {
        if (!currentPage.equals("see details")
                && !currentPage.equals("homepage autentificat")) {
            generateErrorOutput();
            return;
        }

        currentPage = "upgrades";
        pagesStack.push(currentPage);
    }

    public static void changePageLogout() {
        if (!currentPage.equals("see details") && !currentPage.equals("movies")
                && !currentPage.equals("upgrades")
                && !currentPage.equals("homepage autentificat")) {
            generateErrorOutput();
            return;
        }

        currentPage = "homepage neautentificat";
        while (!pagesStack.empty()) {
            pagesStack.pop();
        }

        connectedUser = null;
        displayedMovies = new ArrayList<>();
        actualMovie = null;
    }

    public static void changePageMovies() {
        if (!currentPage.equals("homepage autentificat")
                && !currentPage.equals("see details")
                && !currentPage.equals("upgrades")
                && !currentPage.equals("movies")) {
            generateErrorOutput();
            return;
        }

        displayedMovies = notBanned(database.getMovies(), connectedUser);

        currentPage = "movies";
        pagesStack.push(currentPage);

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void changePageSeeDetails(Action action) {
        if (!currentPage.equals("movies")) {
            generateErrorOutput();
            return;
        }

        boolean found = false;
        for (Movie movie : displayedMovies) {
            if (action.getMovie().equals(movie.getName())) {
                found = true;
                actualMovie = new Movie(movie);

                break;
            }
        }

        if (!found) {
            generateErrorOutput();
            return;
        }

        displayedMovies = new ArrayList<>();
        displayedMovies.add(new Movie(actualMovie));

        currentPage = "see details";
        pagesStack.push(currentPage);

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageRegister(Action action) {
        if (!currentPage.equals("register")) {
            generateErrorOutput();
            return;
        }

        Credentials newCredentials = action.getCredentials();
        String newName = newCredentials.getName();

        boolean alreadyExists = false;
        for (User user : database.getUsers()) {
            if (newName.equals(user.getCredentials().getName())) {
                alreadyExists = true;
                break;
            }
        }

        if (alreadyExists) {
            currentPage = "homepage neautentificat";
            generateErrorOutput();
            return;
        }

        database.addUser(newCredentials);

        connectedUser = database.getUsers().get(database.getUsers().size() - 1);
        displayedMovies = notBanned(database.getMovies(), connectedUser);

        currentPage = "homepage autentificat";
        pagesStack.push(currentPage);

        generateSuccessOutput(Collections.emptyList(), connectedUser);
    }

    public static void onPageLogin(Action action) {
        if (!currentPage.equals("login")) {
            generateErrorOutput();
            return;
        }

        Credentials credentials = action.getCredentials();
        String name = credentials.getName();
        String password = credentials.getPassword();

        boolean authSuccess = false;
        for (User user : database.getUsers()) {

            if (name.equals(user.getCredentials().getName()) &&
                    password.equals(user.getCredentials().getPassword())) {

                authSuccess = true;

                // was deep-copy
                connectedUser = user;

                displayedMovies = notBanned(database.getMovies(), connectedUser);

                break;
            }
        }

        if (!authSuccess) {
            currentPage = "homepage neautentificat";
            generateErrorOutput();
            return;
        }

        currentPage = "homepage autentificat";
        pagesStack.push(currentPage);

        generateSuccessOutput(Collections.emptyList(), connectedUser);
    }

    public static void onPageSearch(Action action) {
        if (!currentPage.equals("movies")) {
            generateErrorOutput();
            return;
        }

        int length = action.getStartsWith().length();

        displayedMovies = new ArrayList<>();
        for (Movie movie : notBanned(database.getMovies(), connectedUser)) {
            if (movie.getName().length() >= length) {
                String nameStartsWith =
                        movie.getName().substring(0, length);

                if (nameStartsWith.equals(action.getStartsWith())) {
                    displayedMovies.add(movie);
                }
            }
        }

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageFilter(Action action) {
        if (!currentPage.equals("movies")) {
            generateErrorOutput();
            return;
        }

        displayedMovies = notBanned(database.getMovies(), connectedUser);

        Filters.Contains contains = action.getFilters().getContains();
        if (contains != null) {
            if (contains.getActors() != null) {
                Iterator<Movie> iForContains = displayedMovies.iterator();
                while (iForContains.hasNext()) {
                    Movie movie = iForContains.next();

                    List<String> movieActors = movie.getActors();
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
                Iterator<Movie> iForContains = displayedMovies.iterator();
                while (iForContains.hasNext()) {
                    Movie movie = iForContains.next();

                    List<String> movieGenres = movie.getGenres();
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

        Filters.Sort sort = action.getFilters().getSort();
        if (sort != null) {
            if (sort.getRating().equals("increasing")
                    && sort.getDuration() == null) {
                displayedMovies.sort(new IncreasingNull());
            } else if (sort.getRating().equals("decreasing")
                    && sort.getDuration() == null) {
                displayedMovies.sort(new DecreasingNull());
            } else if (sort.getRating() == null
                    && sort.getDuration().equals("increasing")) {
                displayedMovies.sort(new NullIncreasing());
            } else if (sort.getRating() == null
                    && sort.getDuration().equals("decreasing")) {
                displayedMovies.sort(new NullDecreasing());
            } else if (sort.getRating().equals("increasing")
                    && sort.getDuration().equals("increasing")) {
                displayedMovies.sort(new IncreasingIncreasing());
            } else if (sort.getRating().equals("increasing")
                    && sort.getDuration().equals("decreasing")) {
                displayedMovies.sort(new IncreasingDecreasing());
            } else if (sort.getRating().equals("decreasing")
                    && sort.getDuration().equals("increasing")) {
                displayedMovies.sort(new DecreasingIncreasing());
            } else if (sort.getRating().equals("decreasing")
                    && sort.getDuration().equals("decreasing")) {
                displayedMovies.sort(new DecreasingDecreasing());
            }
        }

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageBuyTokens(Action action) {
        if (!currentPage.equals("upgrades")) {
            generateErrorOutput();
            return;
        }

        assert connectedUser != null;
        int balance = parseInt(connectedUser.getCredentials().getBalance());
        int tokensWanted = parseInt(action.getCount());

        if (balance < tokensWanted) {
            generateErrorOutput();
            return;
        }

        connectedUser.getCredentials().modifyBalance(-tokensWanted);
        connectedUser.modifyTokensCount(tokensWanted);
    }

    public static void onPageBuyPremiumAccount() {
        if (!currentPage.equals("upgrades")) {
            generateErrorOutput();
            return;
        }

        assert connectedUser != null;
        if (!connectedUser.getCredentials().getAccountType().equals("standard")) {
            generateErrorOutput();
            return;
        }

        if (connectedUser.getTokensCount() < Main.TEN) {
            generateErrorOutput();
            return;
        }

        connectedUser.modifyTokensCount(-Main.TEN);
        connectedUser.getCredentials().setAccountType("premium");
    }

    public static void onPagePurchase(Action action) {
        if (!currentPage.equals("see details")) {
            generateErrorOutput();
            return;
        }

        // if wrong name, error
        if (action.getMovie() != null) {
            assert actualMovie != null;
            if (!action.getMovie().equals(actualMovie.getName())) {
                generateErrorOutput();
                return;
            }
        }

        // if already purchased, error
        boolean alreadyPurchased = false;
        assert connectedUser != null;
        for (Movie movie : connectedUser.getPurchasedMovies()) {
            assert actualMovie != null;
            if (actualMovie.getName().equals(movie.getName())) {
                alreadyPurchased = true;
                break;
            }
        }
        if (alreadyPurchased) {
            generateErrorOutput();
            return;
        }

        if (connectedUser.getCredentials().getAccountType().equals("standard")) {

            if (connectedUser.getTokensCount() < Main.TWO) {
                generateErrorOutput();
                return;
            }

            connectedUser.modifyTokensCount(-Main.TWO);

        } else if (connectedUser.getCredentials().getAccountType().equals("premium")) {

            if (connectedUser.getNumFreePremiumMovies() == 0 &&
                    connectedUser.getTokensCount() < Main.TWO) {

                generateErrorOutput();
                return;
            }

            if (connectedUser.getNumFreePremiumMovies() > 0) {
                connectedUser.modifyFreePremiumMovies(-1);
            } else if (connectedUser.getTokensCount() >= Main.TWO) {
                connectedUser.modifyTokensCount(-Main.TWO);
            }
        }

        connectedUser.getPurchasedMovies().add(new Movie(actualMovie));

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageWatch(Action action) {
        if (!currentPage.equals("see details")) {
            generateErrorOutput();
            return;
        }

        // if wrong name, error
        if (action.getMovie() != null) {
            assert actualMovie != null;
            if (!action.getMovie().equals(actualMovie.getName())) {
                generateErrorOutput();
                return;
            }
        }

        boolean purchasedMovie = false;
        assert connectedUser != null;
        for (Movie movie : connectedUser.getPurchasedMovies()) {
            assert actualMovie != null;
            if (actualMovie.getName().equals(movie.getName())) {
                purchasedMovie = true;
                break;
            }
        }
        if (!purchasedMovie) {
            generateErrorOutput();
            return;
        }

        boolean watchedMovie = false;
        for (Movie movie : connectedUser.getWatchedMovies()) {
            if (actualMovie.getName().equals(movie.getName())) {
                watchedMovie = true;
                break;
            }
        }

        if (!watchedMovie) {
            connectedUser.getWatchedMovies().add(new Movie(actualMovie));
        }

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageLike(Action action) {
        if (!currentPage.equals("see details")) {
            generateErrorOutput();
            return;
        }

        if (action.getMovie() != null) {
            assert actualMovie != null;
            if (!action.getMovie().equals(actualMovie.getName())) {
                generateErrorOutput();
                return;
            }
        }

        boolean alreadyLiked = false;
        assert connectedUser != null;
        for (Movie movie : connectedUser.getLikedMovies()) {
            assert actualMovie != null;
            if (actualMovie.getName().equals(movie.getName())) {
                alreadyLiked = true;
                break;
            }
        }
        if (alreadyLiked) {
            generateErrorOutput();
            return;
        }

        boolean watchedMovieForLike = false;
        for (Movie movie : connectedUser.getWatchedMovies()) {
            assert actualMovie != null;
            if (actualMovie.getName().equals(movie.getName())) {
                watchedMovieForLike = true;
                break;
            }
        }
        if (!watchedMovieForLike) {
            generateErrorOutput();
            return;
        }

        connectedUser.getLikedMovies().add(new Movie(actualMovie));

        actualMovie.incrementNumLikes();

        displayedMovies = new ArrayList<>();
        displayedMovies.add(new Movie(actualMovie));

        for (Movie movie : database.getMovies()) {
            if (actualMovie.getName().equals(movie.getName())) {
                movie.incrementNumLikes();
                break;
            }
        }

        // was for connectedUser only
        for (User user : database.getUsers()) {
            for (Movie movie : user.getPurchasedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.incrementNumLikes();
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getWatchedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.incrementNumLikes();
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getLikedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.incrementNumLikes();
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getRatedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.incrementNumLikes();
                    break;
                }
            }
        }

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageRate(Action action) {
        if (!currentPage.equals("see details")) {
            generateErrorOutput();
            return;
        }

        if (action.getMovie() != null) {
            assert actualMovie != null;
            if (!action.getMovie().equals(actualMovie.getName())) {
                generateErrorOutput();
                return;
            }
        }

        if (action.getRate() < 1 || action.getRate() > Main.FIVE) {
            generateErrorOutput();
            return;
        }

        boolean watchedMovieForRate = false;
        assert connectedUser != null;
        for (Movie movie : connectedUser.getWatchedMovies()) {
            assert actualMovie != null;
            if (actualMovie.getName().equals(movie.getName())) {
                watchedMovieForRate = true;
                break;
            }
        }
        if (!watchedMovieForRate) {
            generateErrorOutput();
            return;
        }

        boolean alreadyRated = connectedUser.getUserRatings().containsKey(actualMovie.getName());
        int oldValue = (alreadyRated) ? connectedUser.getUserRatings().get(actualMovie.getName()) : 0;
        int newValue = action.getRate();

        if (!alreadyRated) {
            connectedUser.getRatedMovies().add(new Movie(actualMovie));
        }

        connectedUser.getUserRatings().put(actualMovie.getName(), newValue);

        actualMovie.updateRating(oldValue, newValue, alreadyRated);

        displayedMovies = new ArrayList<>();
        displayedMovies.add(new Movie(actualMovie));

        for (Movie movie : database.getMovies()) {
            if (actualMovie.getName().equals(movie.getName())) {
                movie.updateRating(oldValue, newValue, alreadyRated);
                break;
            }
        }

        // was for connectedUser only
        for (User user : database.getUsers()) {
            for (Movie movie : user.getPurchasedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.updateRating(oldValue, newValue, alreadyRated);
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getWatchedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.updateRating(oldValue, newValue, alreadyRated);
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getLikedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.updateRating(oldValue, newValue, alreadyRated);
                    break;
                }
            }
        }

        for (User user : database.getUsers()) {
            for (Movie movie : user.getRatedMovies()) {
                if (actualMovie.getName().equals(movie.getName())) {
                    movie.updateRating(oldValue, newValue, alreadyRated);
                    break;
                }
            }
        }

        generateSuccessOutput(displayedMovies, connectedUser);
    }

    public static void onPageSubscribe(Action action) {
        if (!currentPage.equals("see details")) {
            generateErrorOutput();
            return;
        }

        boolean found = false;
        assert actualMovie != null;
        for (String genre : actualMovie.getGenres()) {
            if (action.getSubscribedGenre().equals(genre)) {
                found = true;
                break;
            }
        }
        if (!found) {
            generateErrorOutput();
            return;
        }

        boolean alreadySubscribed = false;
        assert connectedUser != null;
        for (String genre : connectedUser.getSubscribedGenres()) {
            if (action.getSubscribedGenre().equals(genre)) {
                alreadySubscribed = true;
                break;
            }
        }
        if (alreadySubscribed) {
            generateErrorOutput();
            return;
        }

        connectedUser.getSubscribedGenres().add(action.getSubscribedGenre());
    }

    public static void back(Action action) {
        if (connectedUser == null || pagesStack.size() < 2) {
            generateErrorOutput();
            return;
        }

        pagesStack.pop();
        String backPage = pagesStack.pop();

        switch (backPage) {

            case "homepage autentificat" -> {
                currentPage = "homepage autentificat";
                pagesStack.push(currentPage);
            }

            case "upgrades" -> {
                currentPage = "upgrades";
                pagesStack.push(currentPage);
            }

            case "movies" -> {
                currentPage = "movies";
                pagesStack.push(currentPage);

                displayedMovies = notBanned(database.getMovies(), connectedUser);

                generateSuccessOutput(displayedMovies, connectedUser);
            }

            case "see details" -> {

                boolean found = false;
                for (Movie movie : displayedMovies) {
                    if (action.getMovie().equals(movie.getName())) {
                        found = true;
                        actualMovie = new Movie(movie);

                        break;
                    }
                }

                if (!found) {
                    generateErrorOutput();
                    break;
                }

                currentPage = "see details";
                pagesStack.push(currentPage);

                displayedMovies = new ArrayList<>();
                displayedMovies.add(new Movie(actualMovie));

                generateSuccessOutput(displayedMovies, connectedUser);
            }

            default -> {
            }
        }
    }

    public static void databaseAdd(Action action) {

        InputMovie newMovie = action.getAddedMovie();

        boolean alreadyExists = false;
        for (Movie movie : database.getMovies()) {
            if (newMovie.getName().equals(movie.getName())) {
                alreadyExists = true;
                break;
            }
        }
        if (alreadyExists) {
            generateErrorOutput();
            return;
        }

        database.getMovies().add(new Movie(newMovie));

        for (User user : database.getUsers()) {

            boolean found = false;
            for (String userGenre : user.getSubscribedGenres()) {
                for (String newMovieGenre : newMovie.getGenres()) {
                    if (userGenre.equals(newMovieGenre)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }

            if (found) {

                boolean banned = false;
                for (String bannedCountry : newMovie.getCountriesBanned()) {
                    if (user.getCredentials().getCountry().equals(bannedCountry)) {
                        banned = true;
                        break;
                    }
                }

                if (!banned) {
                    user.getNotifications().add(new Notification(newMovie.getName(), "ADD"));
                }

            }
        }
    }

    public static void databaseDelete(Action action) {

        String deletedMovieName = action.getDeletedMovie();

        int index = -1;
        for (int i = 0; i < database.getMovies().size(); i++) {
            if (deletedMovieName.equals(database.getMovies().get(i).getName())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            generateErrorOutput();
            return;
        }

        database.getMovies().remove(index);

        for (User user : database.getUsers()) {

            boolean found = false;
            for (Movie movie : user.getPurchasedMovies()) {
                if (deletedMovieName.equals(movie.getName())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                continue;
            }

            if (user.getCredentials().getAccountType().equals("premium")) {
                user.modifyFreePremiumMovies(+1);
            } else {
                user.modifyTokensCount(+Main.TWO);
            }

            int purchasedIndex = -1;
            for (int i = 0; i < user.getPurchasedMovies().size(); i++) {
                if (deletedMovieName.equals(user.getPurchasedMovies().get(i).getName())) {
                    purchasedIndex = i;
                    break;
                }
            }
            if (purchasedIndex != -1) {
                user.getPurchasedMovies().remove(purchasedIndex);
            }

            int watchedIndex = -1;
            for (int i = 0; i < user.getWatchedMovies().size(); i++) {
                if (deletedMovieName.equals(user.getWatchedMovies().get(i).getName())) {
                    watchedIndex = i;
                    break;
                }
            }
            if (watchedIndex != -1) {
                user.getWatchedMovies().remove(watchedIndex);
            }

            int likedIndex = -1;
            for (int i = 0; i < user.getLikedMovies().size(); i++) {
                if (deletedMovieName.equals(user.getLikedMovies().get(i).getName())) {
                    likedIndex = i;
                    break;
                }
            }
            if (likedIndex != -1) {
                user.getLikedMovies().remove(likedIndex);
            }

            int ratedIndex = -1;
            for (int i = 0; i < user.getRatedMovies().size(); i++) {
                if (deletedMovieName.equals(user.getRatedMovies().get(i).getName())) {
                    ratedIndex = i;
                    break;
                }
            }
            if (ratedIndex != -1) {
                user.getRatedMovies().remove(ratedIndex);
            }

            user.getNotifications().add(new Notification(deletedMovieName, "DELETE"));
        }
    }
}
