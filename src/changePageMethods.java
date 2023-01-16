import java.util.ArrayList;
import java.util.List;

public class changePageMethods {
    public static String loginMethod(String currentPage, List<Output> outputs) {
        if (!currentPage.equals("homepage neautentificat")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

        return "login";
    }

    public static String registerMethod(String currentPage, List<Output> outputs) {
        if (!currentPage.equals("homepage neautentificat")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

        return "register";
    }

    public static String homepageMethod(String currentPage, List<Output> outputs) {
        if (!currentPage.equals("movies")
                && !currentPage.equals("see details")
                && !currentPage.equals("upgrades")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

        return "homepage autentificat";
    }

    public static String upgradesMethod(String currentPage, List<Output> outputs) {
        if (!currentPage.equals("see details")
                && !currentPage.equals("homepage autentificat")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

        return "upgrades";
    }

    public static String logoutMethod(String currentPage, List<Output> outputs) {
        if (!currentPage.equals("see details") && !currentPage.equals("movies")
                && !currentPage.equals("upgrades")
                && !currentPage.equals("homepage autentificat")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

        return "homepage neautentificat";
    }

    /*
    public static String moviesMethod(String currentPage, currentUser currentUser, List<CurrentMovie> currentMovies, List<InputDatabase> inputDatabases, List<Output> outputs) {
        if (!currentPage.equals("homepage autentificat")
                && !currentPage.equals("see details")
                && !currentPage.equals("upgrades")
                && !currentPage.equals("movies")) {
            Main.errorOutput(outputs);
            return currentPage;
        }

//          Updating currentMovies variable in case other actions modified it without
//          rolling back to the original version afterwards (e.g. See Details page).
        for (InputDatabase entry : inputDatabases) {

            String currentUserName = currentUser.getCredentials().getName();
            String entryUserName = entry.getDbUser().getCredentials().getName();

            if (currentUserName.equals(entryUserName)) {
                currentMovies = new ArrayList<>();
                for (CurrentMovie movie : entry.getDbCurrentMovies()) {
                    currentMovies.add(new CurrentMovie(movie));
                }

                break;
            }
        }

        Main.successOutput(outputs, currentMovies, currentUser);

        return "movies";
    }
    */

    /*
    public static String seeDetailsMethod(Action action, CurrentMovie actualMovie, String currentPage, currentUser currentUser, List<CurrentMovie> currentMovies, List<Output> outputs) {
        if (!currentPage.equals("movies")) {
            Main.errorOutput(outputs);
            return currentPage;
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
            Main.errorOutput(outputs);
            return currentPage;
        }

        currentMovies = new ArrayList<>();
        currentMovies.add(actualMovie);

        Main.successOutput(outputs, currentMovies, currentUser);

        return "see details";
    }
     */

}
