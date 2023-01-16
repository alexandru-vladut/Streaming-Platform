import java.util.HashMap;
import java.util.List;

public final class InputDatabase {
    private HashMap<String, Double> userRatings = null;
    private currentUser dbUser = null;
    private List<CurrentMovie> dbCurrentMovies = null;
    private List<String> dbSubscribedGenres = null;

    public currentUser getDbUser() {
        return dbUser;
    }

    public void setDbUser(currentUser dbUser) {
        this.dbUser = dbUser;
    }

    public List<CurrentMovie> getDbCurrentMovies() {
        return dbCurrentMovies;
    }

    public void setDbCurrentMovies(List<CurrentMovie> dbCurrentMovies) {
        this.dbCurrentMovies = dbCurrentMovies;
    }

    public List<String> getDbSubscribedGenres() {
        return dbSubscribedGenres;
    }

    public void setDbSubscribedGenres(List<String> dbSubscribedGenres) {
        this.dbSubscribedGenres = dbSubscribedGenres;
    }

    public HashMap<String, Double> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(HashMap<String, Double> userRatings) {
        this.userRatings = userRatings;
    }
}
