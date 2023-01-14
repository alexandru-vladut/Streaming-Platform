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
        java.io.File myFile = Paths.get(args[0]).toFile();
        JsonNode jsonNode = objectMapper.readTree(myFile);

        String arrayString = jsonNode.get("users").toString();
        List<User> users = objectMapper.readValue(arrayString, new TypeReference<List<User>>() { });

        arrayString = jsonNode.get("movies").toString();
        List<Movie> movies = objectMapper.readValue(arrayString,
                new TypeReference<List<Movie>>() { });

        arrayString = jsonNode.get("actions").toString();
        List<Action> actions = objectMapper.readValue(arrayString,
                new TypeReference<List<Action>>() { });

        /*
          Initialising database with default user information and its CurrentMovies List.
         */
        List<InputDatabase> inputDatabases = new ArrayList<InputDatabase>();
        for (User user: users) {
            InputDatabase dbEntry = new InputDatabase();

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

            inputDatabases.add(dbEntry);
        }


        /*
          Initialising auxiliary variables for current (active) information.
         */
        String currentPage = new String("homepage neautentificat");
        currentUser currentUser = null;
        List<CurrentMovie> currentMovies = new ArrayList<>();

        /*
          Initialising auxiliary hashmaps for rating calculus.
         */
        HashMap<String, Double> ratingsSum = new HashMap<String, Double>();
        for (Movie movie : movies) {
            ratingsSum.put(movie.getName(), 0.00);
        }

        HashMap<String, Integer> ratingsNum = new HashMap<String, Integer>();
        for (Movie movie : movies) {
            ratingsNum.put(movie.getName(), 0);
        }

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

            if (!action.getType().equals("change page") && !action.getType().equals("on page")) {
                errorOutput(outputs);
                break;
            }

            /*
              CHANGE PAGE
             */
            if (action.getType().equals("change page")) {

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
                    }

                    case "upgrades" -> {
                        if (!currentPage.equals("see details")
                                && !currentPage.equals("homepage autentificat")) {
                            errorOutput(outputs);
                            break;
                        }
                        currentPage = "upgrades";
                    }

                    case "logout" -> {
                        if (!currentPage.equals("see details") && !currentPage.equals("movies")
                                && !currentPage.equals("upgrades")
                                && !currentPage.equals("homepage autentificat")) {
                            errorOutput(outputs);
                            break;
                        }

                        currentPage = "homepage neautentificat";
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

                        currentMovies = new ArrayList<>();
                        currentMovies.add(actualMovie);

                        successOutput(outputs, currentMovies, currentUser);
                    }
                }
            /*
              ON PAGE
             */
            } else if (action.getType().equals("on page")) {

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

                        /*
                          Creating new database entry for registered user.
                         */
                        InputDatabase dbEntry = new InputDatabase();

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
                          Adding new entry to database.
                         */
                        inputDatabases.add(dbEntry);

                        currentUser = new currentUser(dbEntry.getDbUser().getCredentials());
                        currentMovies = new ArrayList<>();
                        for (CurrentMovie movie : dbEntry.getDbCurrentMovies()) {
                            currentMovies.add(new CurrentMovie(movie));
                        }

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

                            Credentials entryUserCredentials = entry.getDbUser().getCredentials();

                            if (loginName.equals(entryUserCredentials.getName())
                                    && loginPassword.equals(entryUserCredentials.getPassword())) {
                                authSuccess = true;

                                currentUser = new currentUser(entryUserCredentials,
                                        entry.getDbUser());

                                currentMovies = new ArrayList<>();
                                for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                                    currentMovies.add(new CurrentMovie(movie));
                                }

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

                        Iterator<CurrentMovie> iForSearch = currentMoviesCopyForSearch.iterator();
                        while (iForSearch.hasNext()) {
                            CurrentMovie currentMovie = iForSearch.next();

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
                                parseInt(currentUserCopyForTokens.getCredentials().getBalance());
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

                        Credentials credentialsCopyForWatch =
                                new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForWatch =
                                new currentUser(credentialsCopyForWatch, currentUser);

                        currentUserCopyForWatch.getWatchedMovies().add(actualMovie);

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
                            if (actualMovie.getName().equals(movie.getName())) {
                                ratedMovie = true;
                                break;
                            }
                        }

                        if (ratedMovie) {
                            errorOutput(outputs);
                            break;
                        }

                        if (action.getRate() < 1 || action.getRate() > FIVE) {
                            errorOutput(outputs);
                            break;
                        }

                        List<CurrentMovie> currentMoviesCopyForRate = new ArrayList<>();
                        for (CurrentMovie movie : currentMovies) {
                            currentMoviesCopyForRate.add(new CurrentMovie(movie));
                        }

                        Credentials credentialsCopyForRate =
                                new Credentials(currentUser.getCredentials());
                        currentUser currentUserCopyForRate =
                                new currentUser(credentialsCopyForRate, currentUser);

                        Double currentRatingsSum = ratingsSum.get(actualMovie.getName());
                        currentRatingsSum += action.getRate();
                        ratingsSum.put(actualMovie.getName(), currentRatingsSum);

                        Integer currentRatingsNum = ratingsNum.get(actualMovie.getName());
                        currentRatingsNum += 1;
                        ratingsNum.put(actualMovie.getName(), currentRatingsNum);

                        double newRating = currentRatingsSum / (double) currentRatingsNum;
                        boolean watchedMovieForRate = false;
                        for (CurrentMovie movie : currentUserCopyForRate.getWatchedMovies()) {
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

                        for (CurrentMovie movie
                                : currentUserCopyForRate.getPurchasedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumRatings(currentRatingsNum);
                                movie.setRating(newRating);
                                break;
                            }
                        }

                        for (CurrentMovie movie : currentUserCopyForRate.getLikedMovies()) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumRatings(currentRatingsNum);
                                movie.setRating(newRating);
                                break;
                            }
                        }

                        for (CurrentMovie movie : currentMoviesCopyForRate) {
                            if (actualMovie.getName().equals(movie.getName())) {

                                movie.setNumRatings(currentRatingsNum);
                                movie.setRating(newRating);
                                break;
                            }
                        }

                        CurrentMovie actualMovieCopyForRate = new CurrentMovie(actualMovie);
                        actualMovieCopyForRate.setNumRatings(currentRatingsNum);
                        actualMovieCopyForRate.setRating(newRating);
                        currentUserCopyForRate.getRatedMovies().add(actualMovieCopyForRate);
                        actualMovie = new CurrentMovie(actualMovieCopyForRate);

                        currentUser = new currentUser(credentialsCopyForRate,
                                currentUserCopyForRate);

                        currentMovies = new ArrayList<>();
                        for (CurrentMovie movie : currentMoviesCopyForRate) {
                            currentMovies.add(new CurrentMovie(movie));
                        }

                        for (InputDatabase entry : inputDatabases) {

                            String currentUserName = currentUser.getCredentials().getName();
                            String entryUserName = entry.getDbUser().getCredentials().getName();

                            if (currentUserName.equals(entryUserName)) {
                                entry.setDbUser(new currentUser(credentialsCopyForRate,
                                        currentUserCopyForRate));
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
                }

            }
        }

        /*
          Write to JSON.
          java.io.File resultFile = Paths.get("redundantFiles/outputExample.json").toFile();
          objectWriter.writeValue(resultFile, outputs);
         */
        objectWriter.writeValue(new File(args[1]), outputs);

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


