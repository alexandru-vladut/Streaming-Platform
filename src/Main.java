import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import movieClasses.CurrentMovie;
import movieClasses.Movie;
import sortingComparators.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {
    static final int TEN = 10;
    static final int TWO = 2;
    static final int FIVE = 5;

    public static void main(final String[] args) throws IOException {

        /*
          JSON ObjectMapper preparing.
         */
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());


        /*
          Read from JSON.
          java.io.File File = Paths.get("checker/resources/in/basic_10.json").toFile();
         */
//        java.io.File File = Paths.get("checker/resources/in/basic_4.json").toFile();
        java.io.File myFile = Paths.get(args[0]).toFile();
        JsonNode jsonNode = objectMapper.readTree(myFile);

        String arrayString = jsonNode.get("users").toString();
        List<User> users = objectMapper.readValue(arrayString, new TypeReference<>() {});

        arrayString = jsonNode.get("movies").toString();
        List<Movie> movies = objectMapper.readValue(arrayString, new TypeReference<>() {});

        arrayString = jsonNode.get("actions").toString();
        List<Action> actions = objectMapper.readValue(arrayString, new TypeReference<>() {});

        /*
          Initialising database with default user information, its CurrentMovies List
          and subscribedGenres List.
         */
        List<InputDatabase> inputDatabases = new ArrayList<InputDatabase>();
        for (User user: users) {
            InputDatabase dbEntry = new InputDatabase();

            HashMap<String, Double> newUserRatings = new HashMap<String, Double>();
            for (Movie movie : movies) {
                newUserRatings.put(movie.getName(), 0.00);
            }
            dbEntry.setUserRatings(newUserRatings);

            currentUser newCurrentUser = new currentUser(user.getCredentials());
            dbEntry.setDbUser(newCurrentUser);

            List<CurrentMovie> newCurrentMovies = new ArrayList<>();
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
                    CurrentMovie currentMovie = new CurrentMovie(movie);
                    newCurrentMovies.add(currentMovie);
                }
            }
            dbEntry.setDbCurrentMovies(newCurrentMovies);

            dbEntry.setDbSubscribedGenres(new ArrayList<>());

            inputDatabases.add(dbEntry);
        }


        /*
          Initialising auxiliary variables for current (active) information.
         */
        String currentPage = new String("homepage neautentificat");
        currentUser currentUser = null;
        List<CurrentMovie> currentMovies = new ArrayList<>();
        List<String> subscribedGenres = new ArrayList<>();
        Stack<String> pagesStack = new Stack<>();

        /*
          Initialising auxiliary variable for chosen movie in See Details page.
         */
        CurrentMovie actualMovie = null;

        /*
          Initialising auxiliary variable for outputs storing.
         */
        List<Output> outputs = new ArrayList<Output>();

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

                            currentUser = null;
                        }

                        case "movies" -> {
                            if (!currentPage.equals("homepage autentificat")
                                    && !currentPage.equals("see details")
                                    && !currentPage.equals("upgrades")
                                    && !currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "movies";
                            pagesStack.push(currentPage);

                        /*
                          Updating currentMovies variable in case other actions modified it without
                          rolling back to the original version afterwards (e.g. See Details page).
                         */
                            for (InputDatabase entry : inputDatabases) {

                                assert currentUser != null;
                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    currentMovies = new ArrayList<>();
                                    for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                        CurrentMovie movieCopy = new CurrentMovie(movie);
                                        currentMovies.add(movieCopy);
                                    }

                                    break;
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "see details" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean found = false;
                            for (CurrentMovie movie : currentMovies) {
                                if (action.getMovie().equals(movie.getName())) {
                                    found = true;
                                    actualMovie = new CurrentMovie(movie);

                                    break;
                                }
                            }

                            if (!found) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "see details";
                            pagesStack.push(currentPage);

                            currentMovies = new ArrayList<>();
                            currentMovies.add(actualMovie);

                            successOutput(outputs, currentMovies, currentUser);
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
                            for (InputDatabase entry : inputDatabases) {
                                if (newName.equals(entry.getDbUser().getCredentials().getName())) {
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
                            pagesStack.push(currentPage);

                        /*
                          Creating new database entry for registered user.
                         */
                            InputDatabase dbEntry = new InputDatabase();

                            /*
                          Setting up userRatings field.
                         */
                            HashMap<String, Double> newUserRatings = new HashMap<String, Double>();
                            for (Movie movie : movies) {
                                newUserRatings.put(movie.getName(), 0.00);
                            }
                            dbEntry.setUserRatings(newUserRatings);

                        /*
                          Setting up dbUser field.
                         */
                            currentUser newCurrentUser = new currentUser(newCredentials);
                            dbEntry.setDbUser(newCurrentUser);

                        /*
                          Setting up dbCurrentMovies field.
                         */
                            List<CurrentMovie> newCurrentMovies = new ArrayList<>();
                            for (Movie movie : movies) {

                                boolean isBanned = false;
                                for (String bannedCountry : movie.getCountriesBanned()) {

                                    String aux = newCredentials.getCountry();

                                    if (aux.equals(bannedCountry)) {
                                        isBanned = true;
                                        break;
                                    }
                                }

                                if (!isBanned) {
                                    CurrentMovie currentMovie = new CurrentMovie(movie);
                                    newCurrentMovies.add(currentMovie);
                                }
                            }
                            dbEntry.setDbCurrentMovies(newCurrentMovies);

                        /*
                          Setting up dbCurrentMovies field.
                         */
                            dbEntry.setDbSubscribedGenres(new ArrayList<>());

                        /*
                          Adding new entry to database.
                         */
                            inputDatabases.add(dbEntry);

                            currentUser = new currentUser(dbEntry.getDbUser().getCredentials());
                            currentMovies = new ArrayList<>();
                            for (CurrentMovie movie : dbEntry.getDbCurrentMovies()) {
                                currentMovies.add(new CurrentMovie(movie));
                            }
                            subscribedGenres = new ArrayList<>(dbEntry.getDbSubscribedGenres());

                            successOutput(outputs, Collections.emptyList(), currentUser);
                        }

                        case "login" -> {
                            if (!currentPage.equals("login")) {
                                errorOutput(outputs);
                                break;
                            }

                            Credentials loginCredentials = action.getCredentials();
                            String loginName = loginCredentials.getName();
                            String loginPassword = loginCredentials.getPassword();

                            boolean authSuccess = false;
                            for (InputDatabase entry : inputDatabases) {

                                Credentials entryUserCredentials =
                                        entry.getDbUser().getCredentials();

                                if (loginName.equals(entryUserCredentials.getName())
                                        && loginPassword.equals(
                                                entryUserCredentials.getPassword())) {
                                    authSuccess = true;

                                    currentUser = new currentUser(entryUserCredentials,
                                            entry.getDbUser());

                                    currentMovies = new ArrayList<>();
                                    for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                        currentMovies.add(new CurrentMovie(movie));
                                    }

                                    subscribedGenres = new ArrayList<>(
                                            entry.getDbSubscribedGenres());

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

                            successOutput(outputs, Collections.emptyList(), currentUser);
                        }

                        case "search" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                            int length = action.getStartsWith().length();

                            List<CurrentMovie> currentMoviesCopyForSearch = new ArrayList<>();
                            for (CurrentMovie movie : currentMovies) {
                                CurrentMovie movieCopy = new CurrentMovie(movie);
                                currentMoviesCopyForSearch.add(movieCopy);
                            }

                            Iterator<CurrentMovie> iForSearch =
                                    currentMoviesCopyForSearch.iterator();
                            while (iForSearch.hasNext()) {
                                CurrentMovie currentMovie = iForSearch.next();

                                if (currentMovie.getName().length() >= length) {
                                    String nameStartsWith =
                                            currentMovie.getName().substring(0, length);

                                    if (!nameStartsWith.equals(action.getStartsWith())) {
                                        iForSearch.remove();
                                    }
                                } else {
                                    iForSearch.remove();
                                }
                            }

                            successOutput(outputs, currentMoviesCopyForSearch, currentUser);
                        }

                        case "filter" -> {
                            if (!currentPage.equals("movies")) {
                                errorOutput(outputs);
                                break;
                            }

                        /*
                          Updating currentMovies variable in case other actions modified it without
                          rolling back to the original version afterwards (e.g. See Details page).
                         */
                            for (InputDatabase entry : inputDatabases) {

                                assert currentUser != null;
                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName =
                                        entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {

                                    currentMovies = new ArrayList<>();
                                    for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                        CurrentMovie movieCopy = new CurrentMovie(movie);
                                        currentMovies.add(movieCopy);
                                    }

                                    break;
                                }
                            }

                            List<CurrentMovie> currentMoviesCopyForFilter = new ArrayList<>();
                            for (CurrentMovie movie : currentMovies) {
                                CurrentMovie movieCopy = new CurrentMovie(movie);
                                currentMoviesCopyForFilter.add(movieCopy);
                            }

                            Contains contains = action.getFilters().getContains();
                            if (contains != null) {
                                if (contains.getActors() != null) {
                                    Iterator<CurrentMovie> iForContains =
                                            currentMoviesCopyForFilter.iterator();
                                    while (iForContains.hasNext()) {
                                        CurrentMovie currentMovie = iForContains.next();

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
                                    Iterator<CurrentMovie> iForContains =
                                            currentMoviesCopyForFilter.iterator();
                                    while (iForContains.hasNext()) {
                                        CurrentMovie currentMovie = iForContains.next();

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
                                if (sort.getRating().equals("increasing")
                                        && sort.getDuration() == null) {
                                    currentMoviesCopyForFilter.sort(new IncreasingNull());
                                } else if (sort.getRating().equals("decreasing")
                                        && sort.getDuration() == null) {
                                    currentMoviesCopyForFilter.sort(new DecreasingNull());
                                } else if (sort.getRating() == null
                                        && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopyForFilter.sort(new NullIncreasing());
                                } else if (sort.getRating() == null
                                        && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopyForFilter.sort(new NullDecreasing());
                                } else if (sort.getRating().equals("increasing")
                                        && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopyForFilter.sort(new IncreasingIncreasing());
                                } else if (sort.getRating().equals("increasing")
                                        && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopyForFilter.sort(new IncreasingDecreasing());
                                } else if (sort.getRating().equals("decreasing")
                                        && sort.getDuration().equals("increasing")) {
                                    currentMoviesCopyForFilter.sort(new DecreasingIncreasing());
                                } else if (sort.getRating().equals("decreasing")
                                        && sort.getDuration().equals("decreasing")) {
                                    currentMoviesCopyForFilter.sort(new DecreasingDecreasing());
                                }
                            }

                            currentMovies = new ArrayList<>();
                            for (CurrentMovie movie : currentMoviesCopyForFilter) {
                                currentMovies.add(new CurrentMovie(movie));
                            }

                            successOutput(outputs, currentMoviesCopyForFilter, currentUser);
                        }

                        case "buy tokens" -> {
                            if (!currentPage.equals("upgrades")) {
                                errorOutput(outputs);
                                break;
                            }

                            assert currentUser != null;
                            Credentials credentialsCopyForTokens =
                                    new Credentials(currentUser.getCredentials());
                            currentUser currentUserCopyForTokens =
                                    new currentUser(credentialsCopyForTokens, currentUser);

                            int tokensWanted = parseInt(action.getCount());
                            int currentBalanceForTokens =
                                    parseInt(currentUserCopyForTokens.getCredentials().
                                            getBalance());
                            int currentTokensForTokens = currentUserCopyForTokens.getTokensCount();

                            if (currentBalanceForTokens < tokensWanted) {
                                errorOutput(outputs);
                                break;
                            }

                            currentBalanceForTokens -= tokensWanted;
                            String aux2 = Integer.toString(currentBalanceForTokens);
                            currentUserCopyForTokens.getCredentials().setBalance(aux2);

                            currentTokensForTokens += tokensWanted;
                            currentUserCopyForTokens.setTokensCount(currentTokensForTokens);

                            currentUser = new currentUser(credentialsCopyForTokens,
                                    currentUserCopyForTokens);

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbUser(new currentUser(credentialsCopyForTokens,
                                            currentUserCopyForTokens));

                                    break;
                                }
                            }
                        }

                        case "buy premium account" -> {
                            if (!currentPage.equals("upgrades")) {
                                errorOutput(outputs);
                                break;
                            }

                            assert currentUser != null;
                            if (!currentUser.getCredentials().getAccountType().equals("standard")) {
                                errorOutput(outputs);
                                break;
                            }

                            Credentials credentialsCopyForPremium =
                                    new Credentials(currentUser.getCredentials());
                            currentUser currentUserCopyForPremium =
                                    new currentUser(credentialsCopyForPremium, currentUser);

                            int currentTokensForPremium =
                                    currentUserCopyForPremium.getTokensCount();

                            if (currentTokensForPremium < TEN) {
                                errorOutput(outputs);
                                break;
                            }

                            currentTokensForPremium -= TEN;
                            currentUserCopyForPremium.setTokensCount(currentTokensForPremium);
                            currentUserCopyForPremium.getCredentials().setAccountType("premium");

                            currentUser = new currentUser(credentialsCopyForPremium,
                                    currentUserCopyForPremium);

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbUser(new currentUser(credentialsCopyForPremium,
                                            currentUserCopyForPremium));

                                    break;
                                }
                            }
                        }

                        case "purchase" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                assert actualMovie != null;
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean found = false;
                            assert currentUser != null;
                            for (CurrentMovie movie : currentUser.getPurchasedMovies()) {
                                assert actualMovie != null;
                                if (actualMovie.getName().equals(movie.getName())) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                errorOutput(outputs);
                                break;
                            }

                            Credentials credentialsCopyForPurchase =
                                    new Credentials(currentUser.getCredentials());
                            currentUser currentUserCopyForPurchase =
                                    new currentUser(credentialsCopyForPurchase, currentUser);

                            Credentials aux3 = currentUserCopyForPurchase.getCredentials();
                            if (aux3.getAccountType().equals("standard")) {

                                int currentTokens = currentUserCopyForPurchase.getTokensCount();

                                if (currentTokens < TWO) {
                                    errorOutput(outputs);
                                    break;
                                }

                                currentTokens -= TWO;
                                currentUserCopyForPurchase.setTokensCount(currentTokens);

                            } else if (aux3.getAccountType().equals("premium")) {

                                int currentNumFreePremiumMovies =
                                        currentUserCopyForPurchase.getNumFreePremiumMovies();
                                int currentTokens = currentUserCopyForPurchase.getTokensCount();

                                if (currentNumFreePremiumMovies == 0 && currentTokens < 2) {
                                    errorOutput(outputs);
                                    break;
                                }

                                if (currentNumFreePremiumMovies > 0) {

                                    currentNumFreePremiumMovies -= 1;
                                    currentUserCopyForPurchase.setNumFreePremiumMovies(
                                            currentNumFreePremiumMovies);

                                } else if (currentTokens >= 2) {

                                    currentTokens -= 2;
                                    currentUserCopyForPurchase.setTokensCount(currentTokens);

                                }
                            }

                            currentUserCopyForPurchase.getPurchasedMovies().add(actualMovie);

                            currentUser = new currentUser(credentialsCopyForPurchase,
                                    currentUserCopyForPurchase);

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbUser(new currentUser(credentialsCopyForPurchase,
                                            currentUserCopyForPurchase));

                                    break;
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "watch" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                assert actualMovie != null;
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean purchasedMovie = false;
                            assert currentUser != null;
                            for (CurrentMovie movie : currentUser.getPurchasedMovies()) {
                                assert actualMovie != null;
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
                            for (CurrentMovie movie : currentUser.getWatchedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {
                                    watchedMovie = true;
                                    break;
                                }
                            }

                            Credentials credentialsCopyForWatch =
                                    new Credentials(currentUser.getCredentials());
                            currentUser currentUserCopyForWatch =
                                    new currentUser(credentialsCopyForWatch, currentUser);

                            if (!watchedMovie) {
                                currentUserCopyForWatch.getWatchedMovies().add(actualMovie);
                            }

                            currentUser = new currentUser(credentialsCopyForWatch,
                                    currentUserCopyForWatch);

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbUser(new currentUser(credentialsCopyForWatch,
                                            currentUserCopyForWatch));

                                    break;
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "like" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                assert actualMovie != null;
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean likedMovie = false;
                            assert currentUser != null;
                            for (CurrentMovie movie : currentUser.getLikedMovies()) {
                                assert actualMovie != null;
                                if (actualMovie.getName().equals(movie.getName())) {
                                    likedMovie = true;
                                    break;
                                }
                            }

                            if (likedMovie) {
                                errorOutput(outputs);
                                break;
                            }

                            List<CurrentMovie> currentMoviesCopyForLike = new ArrayList<>();
                            for (CurrentMovie movie : currentMovies) {
                                currentMoviesCopyForLike.add(new CurrentMovie(movie));
                            }

                            Credentials credentialsCopyForLike =
                                    new Credentials(currentUser.getCredentials());
                            currentUser currentUserCopyForLike =
                                    new currentUser(credentialsCopyForLike, currentUser);

                            int currentNumLikes = 0;
                            boolean watchedMovieForLike = false;
                            for (CurrentMovie movie : currentUserCopyForLike.getWatchedMovies()) {

                                assert actualMovie != null;
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

                            for (CurrentMovie movie : currentUserCopyForLike.getPurchasedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {

                                    movie.setNumLikes(currentNumLikes);
                                    break;
                                }
                            }

                            for (CurrentMovie movie : currentUserCopyForLike.getRatedMovies()) {
                                if (actualMovie.getName().equals(movie.getName())) {

                                    movie.setNumLikes(currentNumLikes);
                                    break;
                                }
                            }

                            for (CurrentMovie movie : currentMoviesCopyForLike) {
                                if (actualMovie.getName().equals(movie.getName())) {

                                    movie.setNumLikes(currentNumLikes);
                                    break;
                                }
                            }

                            CurrentMovie actualMovieCopyForLike = new CurrentMovie(actualMovie);
                            actualMovieCopyForLike.setNumLikes(currentNumLikes);
                            currentUserCopyForLike.getLikedMovies().add(actualMovieCopyForLike);
                            actualMovie = new CurrentMovie(actualMovieCopyForLike);

                            currentUser = new currentUser(credentialsCopyForLike,
                                    currentUserCopyForLike);

                            currentMovies = new ArrayList<>();
                            for (CurrentMovie movie : currentMoviesCopyForLike) {
                                currentMovies.add(new CurrentMovie(movie));
                            }

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbUser(new currentUser(credentialsCopyForLike,
                                            currentUserCopyForLike));
                                }

                                for (int i = 0; i < entry.getDbCurrentMovies().size(); i++) {

                                    CurrentMovie modifiedMovie = currentMovies.get(0);
                                    CurrentMovie modifiedEntry = entry.getDbCurrentMovies().get(i);

                                    if (modifiedMovie.getName().equals(modifiedEntry.getName())) {
                                        CurrentMovie movieCopy = new CurrentMovie(modifiedMovie);
                                        entry.getDbCurrentMovies().set(i, movieCopy);
                                    }
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "rate" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
                            }

                            if (action.getMovie() != null) {
                                assert actualMovie != null;
                                if (!action.getMovie().equals(actualMovie.getName())) {
                                    errorOutput(outputs);
                                    break;
                                }
                            }

                            boolean ratedMovie = false;
                            assert currentUser != null;
                            for (CurrentMovie movie : currentUser.getRatedMovies()) {
                                assert actualMovie != null;
                                if (actualMovie.getName().equals(movie.getName())) {
                                    ratedMovie = true;
                                    break;
                                }
                            }

                            if (action.getRate() < 1 || action.getRate() > FIVE) {
                                errorOutput(outputs);
                                break;
                            }

                            boolean watchedMovieForRate = false;
                            for (CurrentMovie movie : currentUser.getWatchedMovies()) {
                                assert actualMovie != null;
                                if (actualMovie.getName().equals(movie.getName())) {
                                    watchedMovieForRate = true;
                                    break;
                                }
                            }

                            if (!watchedMovieForRate) {
                                errorOutput(outputs);
                                break;
                            }

                            double currentRatingsSum = 0.00;
                            int currentRatingsNum = 0;
                            for (InputDatabase entry : inputDatabases) {
                                if (currentUser.getCredentials().getName().equals(
                                        entry.getDbUser().getCredentials().getName())) {
                                    entry.getUserRatings().put(actualMovie.getName(),
                                            (double) action.getRate());
                                }

                                Double currentRating =
                                        entry.getUserRatings().get(actualMovie.getName());
                                if (currentRating > 0) {
                                    currentRatingsSum += currentRating;
                                    currentRatingsNum += 1;
                                }
                            }

                            double newRating = currentRatingsSum / (double) currentRatingsNum;

                            CurrentMovie actualMovieCopyForRate = new CurrentMovie(actualMovie);
                            actualMovieCopyForRate.setNumRatings(currentRatingsNum);
                            actualMovieCopyForRate.setRating(newRating);

                            for (InputDatabase entry : inputDatabases) {

                                for (CurrentMovie movie
                                        : entry.getDbCurrentMovies()) {
                                    if (actualMovie.getName().equals(movie.getName())) {

                                        movie.setNumRatings(currentRatingsNum);
                                        movie.setRating(newRating);
                                        break;
                                    }
                                }

                                for (CurrentMovie movie
                                        : entry.getDbUser().getPurchasedMovies()) {
                                    if (actualMovie.getName().equals(movie.getName())) {

                                        movie.setNumRatings(currentRatingsNum);
                                        movie.setRating(newRating);
                                        break;
                                    }
                                }

                                for (CurrentMovie movie
                                        : entry.getDbUser().getWatchedMovies()) {
                                    if (actualMovie.getName().equals(movie.getName())) {

                                        movie.setNumRatings(currentRatingsNum);
                                        movie.setRating(newRating);
                                        break;
                                    }
                                }

                                for (CurrentMovie movie
                                        : entry.getDbUser().getLikedMovies()) {
                                    if (actualMovie.getName().equals(movie.getName())) {

                                        movie.setNumRatings(currentRatingsNum);
                                        movie.setRating(newRating);
                                        break;
                                    }
                                }

                                if (!currentUser.getCredentials().getName().equals(
                                        entry.getDbUser().getCredentials().getName())) {

                                    for (CurrentMovie movie
                                            : entry.getDbUser().getRatedMovies()) {
                                        if (actualMovie.getName().equals(movie.getName())) {

                                            movie.setNumRatings(currentRatingsNum);
                                            movie.setRating(newRating);
                                            break;
                                        }
                                    }
                                } else {
                                    if (!ratedMovie) {
                                        entry.getDbUser().getRatedMovies().
                                                add(actualMovieCopyForRate);
                                    }
                                }


                            }

                            actualMovie = new CurrentMovie(actualMovieCopyForRate);

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    currentUser =
                                            new currentUser(entry.getDbUser().getCredentials(),
                                            entry.getDbUser());

                                    currentMovies = new ArrayList<>();
                                    for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                        if (actualMovie.getName().equals(movie.getName())) {
                                            currentMovies.add(new CurrentMovie(movie));
                                        }
                                    }
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "subscribe" -> {
                            if (!currentPage.equals("see details")) {
                                errorOutput(outputs);
                                break;
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
                                errorOutput(outputs);
                                break;
                            }

                            found = false;
                            for (String genre : subscribedGenres) {
                                if (action.getSubscribedGenre().equals(genre)) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                errorOutput(outputs);
                                break;
                            }

                            subscribedGenres.add(action.getSubscribedGenre());

                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    entry.setDbSubscribedGenres(new ArrayList<>(subscribedGenres));

                                    break;
                                }
                            }

                        }

                        default -> { }
                    }
                }
                case "back" -> {
                    if (currentUser == null || pagesStack.size() < 2) {
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
                              modified it withoutrolling back to the original version
                              afterwards (e.g. See Details page).
                             */
                            for (InputDatabase entry : inputDatabases) {

                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    currentMovies = new ArrayList<>();
                                    for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                        CurrentMovie movieCopy = new CurrentMovie(movie);
                                        currentMovies.add(movieCopy);
                                    }

                                    break;
                                }
                            }

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        case "see details" -> {

                            boolean found = false;
                            for (CurrentMovie movie : currentMovies) {
                                if (action.getMovie().equals(movie.getName())) {
                                    found = true;
                                    actualMovie = new CurrentMovie(movie);

                                    break;
                                }
                            }

                            if (!found) {
                                errorOutput(outputs);
                                break;
                            }

                            currentPage = "see details";
                            pagesStack.push(currentPage);

                            currentMovies = new ArrayList<>();
                            currentMovies.add(actualMovie);

                            successOutput(outputs, currentMovies, currentUser);
                        }

                        default -> { }
                    }
                }
                case "database" -> {
                    String feature = action.getFeature();
                    switch (feature) {
                        case "add" -> {
                            Movie newMovie = action.getAddedMovie();
                            boolean alreadyExists = false;
                            for (Movie movie : movies) {
                                if (newMovie.getName().equals(movie.getName())) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                            if (alreadyExists) {
                                errorOutput(outputs);
                                break;
                            }

                            movies.add(new Movie(newMovie));

                            Notification newNotify = new Notification(newMovie.getName(),
                                    "ADD");

                            for (InputDatabase entry : inputDatabases) {

                                entry.getUserRatings().put(newMovie.getName(), 0.00);

                                boolean userBanned = false;
                                for (String countryBanned : newMovie.getCountriesBanned()) {
                                    if (entry.getDbUser().getCredentials().getCountry().
                                            equals(countryBanned)) {
                                        userBanned = true;
                                        break;
                                    }
                                }

                                if (!userBanned) {
                                    entry.getDbCurrentMovies().add(new CurrentMovie(newMovie));
                                }

                                boolean found = false;
                                for (String genre : entry.getDbSubscribedGenres()) {
                                    for (String genre2 : newMovie.getGenres()) {
                                        if (genre.equals(genre2)) {
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
                                        if (entry.getDbUser().getCredentials().getCountry().
                                                equals(bannedCountry)) {
                                            banned = true;
                                            break;
                                        }
                                    }

                                    if (!banned) {
                                        entry.getDbUser().getNotifications().add(newNotify);
                                    }

                                }
                            }

                            for (InputDatabase entry : inputDatabases) {

                                assert currentUser != null;
                                String currentUserName = currentUser.getCredentials().getName();
                                String entryUserName = entry.getDbUser().getCredentials().getName();

                                if (currentUserName.equals(entryUserName)) {
                                    currentUser =
                                            new currentUser(entry.getDbUser().getCredentials(),
                                            entry.getDbUser());
                                }
                            }

                        }

                        case "delete" -> {
                            String deletedMovieName = action.getDeletedMovie();
                            int index = -1;
                            for (int i = 0; i < movies.size(); i++) {
                                if (deletedMovieName.equals(movies.get(i).getName())) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index == -1) {
                                errorOutput(outputs);
                                break;
                            }

                            movies.remove(index);

                            Notification newNotify = new Notification(deletedMovieName,
                                    "DELETE");

                            for (InputDatabase entry : inputDatabases) {
                                boolean found = false;
                                for (CurrentMovie movie : entry.getDbUser().getPurchasedMovies()) {
                                    if (deletedMovieName.equals(movie.getName())) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    continue;
                                }

                                if (entry.getDbUser().getCredentials().getAccountType().
                                        equals("premium")) {
                                    int aux = entry.getDbUser().getNumFreePremiumMovies();
                                    aux += 1;
                                    entry.getDbUser().setNumFreePremiumMovies(aux);
                                } else {
                                    int aux = entry.getDbUser().getTokensCount();
                                    aux += 2;
                                    entry.getDbUser().setTokensCount(aux);
                                }

                                int purchasedIndex = -1;
                                List<CurrentMovie> purchasedReference = entry.getDbUser().
                                        getPurchasedMovies();
                                for (int i = 0; i < purchasedReference.size(); i++) {
                                    if (deletedMovieName.equals(purchasedReference.get(i).
                                            getName())) {
                                        purchasedIndex = i;
                                        break;
                                    }
                                }
                                if (purchasedIndex != -1) {
                                    purchasedReference.remove(purchasedIndex);
                                }

                                int watchedIndex = -1;
                                List<CurrentMovie> watchedReference = entry.getDbUser().
                                        getWatchedMovies();
                                for (int i = 0; i < watchedReference.size(); i++) {
                                    if (deletedMovieName.equals(watchedReference.get(i).
                                            getName())) {
                                        watchedIndex = i;
                                        break;
                                    }
                                }
                                if (watchedIndex != -1) {
                                    watchedReference.remove(watchedIndex);
                                }

                                int likedIndex = -1;
                                List<CurrentMovie> likedReference = entry.getDbUser().
                                        getLikedMovies();
                                for (int i = 0; i < likedReference.size(); i++) {
                                    if (deletedMovieName.equals(likedReference.get(i).getName())) {
                                        likedIndex = i;
                                        break;
                                    }
                                }
                                if (likedIndex != -1) {
                                    likedReference.remove(likedIndex);
                                }

                                int ratedIndex = -1;
                                List<CurrentMovie> ratedReference =
                                        entry.getDbUser().getRatedMovies();
                                for (int i = 0; i < ratedReference.size(); i++) {
                                    if (deletedMovieName.equals(ratedReference.get(i).
                                            getName())) {
                                        ratedIndex = i;
                                        break;
                                    }
                                }
                                if (ratedIndex != -1) {
                                    ratedReference.remove(ratedIndex);
                                }

                                entry.getDbUser().getNotifications().add(newNotify);
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
        objectWriter.writeValue(new File(args[1]), outputs);
//        java.io.File resultFile = Paths.get("output.json").toFile();
//        objectWriter.writeValue(resultFile, outputs);

    }

    public static void errorOutput(final List<Output> outputs) {
        Output currentOutput = new Output("Error",
                Collections.emptyList(), null);
        outputs.add(currentOutput);
    }

    public static void successOutput(final List<Output> outputs,
                                     final List<CurrentMovie> currentMovies,
                                     final currentUser currentUser) {
        Output currentOutput = new Output(null, currentMovies, currentUser);
        outputs.add(currentOutput);
    }

}


