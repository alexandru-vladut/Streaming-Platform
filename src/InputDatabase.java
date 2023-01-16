import java.util.List;

public final class InputDatabase {
    private currentUser dbUser = null;
    private List<CurrentMovie> dbCurrentMovies = null;

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
}
