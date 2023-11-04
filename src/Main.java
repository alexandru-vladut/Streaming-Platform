import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import movie.Movie;
import movie.InputMovie;
import movie.OutputMovie;
import utils.sortingComparators.*;
import user.*;
import utils.Action;
import utils.Database;
import utils.Filters;
import utils.Output;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {
    static final int TEN = 10;
    static final int TWO = 2;
    static final int FIVE = 5;

    public static void errorOutput(final List<Output> outputs) {
        Output currentOutput = new Output("Error", Collections.emptyList(), null);
        outputs.add(currentOutput);
    }

    public static void successOutput(final List<Output> outputs, final List<Movie> movies, final User user) {

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

    public static void main(final String[] args) throws IOException {

        /*
          Initialising database with default users and movies information, and returning actions List.
         */
        Database database = new Database();
        List<Action> actions = database.initDatabaseFromJSON(args[0]); // "checker/resources/in/basic_10.json"

        /*
          Initialising auxiliary variables for current (active) information.
         */
        String currentPage = "homepage neautentificat";
        Stack<String> pagesStack = new Stack<>();
        User connectedUser = null;
        List<Movie> displayedMovies = new ArrayList<>();
        Movie actualMovie = null;

        /*
          Initialising auxiliary variable for outputs storing.
         */
        List<Output> outputs = new ArrayList<>();

        /*
          Iterating through the actions.
         */
        for (Action action : actions) {

            if (!action.getType().equals("change page") && !action.getType().equals("on page")
                    && !action.getType().equals("back") && !action.getType().equals("database")) {
                errorOutput(outputs);
                break;
            }

            switch (action.getType()) {

                case "change page" -> {
                    String newPage = action.getPage();
                    switch (newPage) {

                        case "login" -> {
                            if (!currentPage.equals("homepage neautentificat")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "login";
                        }

                        case "register" -> {
                            if (!currentPage.equals("homepage neautentificat")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "register";
                        }

                        case "homepage autentificat" -> {
                            if (!currentPage.equals("movies")
                                    && !currentPage.equals("see details")
                                    && !currentPage.equals("upgrades")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "homepage autentificat";
                            pagesStack.push(currentPage);
                        }

                        case "upgrades" -> {
                            if (!currentPage.equals("see details")
                                    && !currentPage.equals("homepage autentificat")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "upgrades";
                            pagesStack.push(currentPage);
                        }

                        case "logout" -> {
                            if (!currentPage.equals("see details") && !currentPage.equals("movies")
                                    && !currentPage.equals("upgrades")
                                    && !currentPage.equals("homepage autentificat")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "homepage neautentificat";
                            while (!pagesStack.empty()) {
                                pagesStack.pop();
                            }

                            connectedUser = null;
                            displayedMovies = new ArrayList<>();
                            actualMovie = null;
                        }

                        case "movies" -> {
                            if (!currentPage.equals("homepage autentificat")
                                    && !currentPage.equals("see details")
                                    && !currentPage.equals("upgrades")
                                    && !currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                        /*
                          Updating currentMovies variable in case other actions modified it without
                          rolling back to the original version afterwards (e.g. See Details page).
                         */
                            displayedMovies = notBanned(database.getMovies(), connectedUser);

                            currentPage = "movies";
                            pagesStack.push(currentPage);

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "see details" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
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
                                errorOutput(outputs);
                                break;
                            }

                            displayedMovies = new ArrayList<>();
                            displayedMovies.add(new Movie(actualMovie));

                            currentPage = "see details";
                            pagesStack.push(currentPage);

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        default -> { }
                    }
                }

                case "on page" -> {
                    String feature = action.getFeature();
                    switch (feature) {

                        case "register" -> {
                            if (!currentPage.equals("register")) {
                                errorOutput(outputs);
                                break;
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
                                errorOutput(outputs);
                                break;
                            }

                            database.addUser(newCredentials);

                            connectedUser = database.getUsers().get(database.getUsers().size() - 1);
                            displayedMovies = notBanned(database.getMovies(), connectedUser);

                            currentPage = "homepage autentificat";
                            pagesStack.push(currentPage);

                            successOutput(outputs, Collections.emptyList(), connectedUser);
                        }

                        case "login" -> {
                            if (!currentPage.equals("login")) {
                                errorOutput(outputs);
                                break;
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
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "homepage autentificat";
                            pagesStack.push(currentPage);

                            successOutput(outputs, Collections.emptyList(), connectedUser);
                        }

                        // resets and modifies displayedMovies
                        case "search" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
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

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        // resets and modifies displayedMovies
                        case "filter" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                        /*
                          Updating currentMovies variable in case other actions modified it without
                          rolling back to the original version afterwards (e.g. See Details page).
                         */

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

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "buy tokens" -> {
                            if (!currentPage.equals("upgrades")) {
                                errorOutput(outputs);
                                break;
                            }

                            int balance = parseInt(connectedUser.getCredentials().getBalance());
                            int tokensWanted = parseInt(action.getCount());

                            if (balance < tokensWanted) {
                                errorOutput(outputs);
                                break;
                            }

                            connectedUser.getCredentials().modifyBalance(-tokensWanted);
                            connectedUser.modifyTokensCount(tokensWanted);

                        }

                        case "buy premium account" -> {
                            if (!currentPage.equals("upgrades")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (!connectedUser.getCredentials().getAccountType().equals("standard")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (connectedUser.getTokensCount() < TEN) {
                                errorOutput(outputs);
                                break;
                            }

                            connectedUser.modifyTokensCount(-TEN);
                            connectedUser.getCredentials().setAccountType("premium");
                        }

                        case "purchase" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            // if wrong name, error
                            if (action.getMovie() != null) {
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            // if already purchased, error
                            boolean alreadyPurchased = false;
                            for (Movie movie : connectedUser.getPurchasedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    alreadyPurchased = true;
                                    break;
                                }
                            }
                            if (alreadyPurchased) {
                                errorOutput(outputs);
                                break;
                            }

                            if (connectedUser.getCredentials().getAccountType().equals("standard")) {

                                if (connectedUser.getTokensCount() < TWO) {
                                    errorOutput(outputs);
                                    break;
                                }

                                connectedUser.modifyTokensCount(-TWO);

                            } else if (connectedUser.getCredentials().getAccountType().equals("premium")) {

                                if (connectedUser.getNumFreePremiumMovies() == 0 &&
                                    connectedUser.getTokensCount() < TWO) {

                                    errorOutput(outputs);
                                    break;
                                }

                                if (connectedUser.getNumFreePremiumMovies() > 0) {
                                    connectedUser.modifyFreePremiumMovies(-1);
                                } else if (connectedUser.getTokensCount() >= TWO) {
                                    connectedUser.modifyTokensCount(-TWO);
                                }
                            }

                            // works?
                            connectedUser.getPurchasedMovies().add(new Movie(actualMovie));

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "watch" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            // if wrong name, error
                            if (action.getMovie() != null) {
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean purchasedMovie = false;
                            for (Movie movie : connectedUser.getPurchasedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    purchasedMovie = true;
                                    break;
                                }
                            }
                            if (!purchasedMovie) {
                                errorOutput(outputs);
                                break;
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

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "like" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean alreadyLiked = false;
                            for (Movie movie : connectedUser.getLikedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    alreadyLiked = true;
                                    break;
                                }
                            }
                            if (alreadyLiked) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean watchedMovieForLike = false;
                            for (Movie movie : connectedUser.getWatchedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    watchedMovieForLike = true;
                                    break;
                                }
                            }
                            if (!watchedMovieForLike) {
                                errorOutput(outputs);
                                break;
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

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "rate" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            if (action.getRate() < 1 || action.getRate() > FIVE) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean watchedMovieForRate = false;
                            for (Movie movie : connectedUser.getWatchedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    watchedMovieForRate = true;
                                    break;
                                }
                            }
                            if (!watchedMovieForRate) {
                                errorOutput(outputs);
                                break;
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

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        case "subscribe" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean found = false;
                            for (String genre : actualMovie.getGenres()) {
                                if (action.getSubscribedGenre().equals(genre)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean alreadySubscribed = false;
                            for (String genre : connectedUser.getSubscribedGenres()) {
                                if (action.getSubscribedGenre().equals(genre)) {
                                    alreadySubscribed = true;
                                    break;
                                }
                            }
                            if (alreadySubscribed) {
                                errorOutput(outputs);
                                break;
                            }

                            connectedUser.getSubscribedGenres().add(action.getSubscribedGenre());

                        }

                        default -> { }
                    }
                }

                case "back" -> {
                    if (connectedUser == null || pagesStack.size() < 2) {
                        errorOutput(outputs);
                        break;
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

                            /*
                              Updating currentMovies variable in case other actions
                              modified it without rolling back to the original version
                              afterwards (e.g. See Details page).
                             */
                            displayedMovies = notBanned(database.getMovies(), connectedUser);

                            successOutput(outputs, displayedMovies, connectedUser);
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
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "see details";
                            pagesStack.push(currentPage);

                            displayedMovies = new ArrayList<>();
                            displayedMovies.add(new Movie(actualMovie));

                            successOutput(outputs, displayedMovies, connectedUser);
                        }

                        default -> { }
                    }
                }

                case "database" -> {
                    String feature = action.getFeature();
                    switch (feature) {
                        case "add" -> {

                            InputMovie newMovie = action.getAddedMovie();

                            boolean alreadyExists = false;
                            for (Movie movie : database.getMovies()) {
                                if (newMovie.getName().equals(movie.getName())) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                            if (alreadyExists) {
                                errorOutput(outputs);
                                break;
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

                        case "delete" -> {

                            String deletedMovieName = action.getDeletedMovie();

                            int index = -1;
                            for (int i = 0; i < database.getMovies().size(); i++) {
                                if (deletedMovieName.equals(database.getMovies().get(i).getName())) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index == -1) {
                                errorOutput(outputs);
                                break;
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
                                    user.modifyTokensCount(+2);
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

                        default -> { }
                    }
                }
                default -> { }
            }
        }

        /*
          Write to JSON.
          java.io.File resultFile = Paths.get("redundantFiles/outputExample.json").toFile();
          objectWriter.writeValue(resultFile, outputs);
         */
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        objectWriter.writeValue(new File(args[1]), outputs);
    }
}


