import movieClasses.CurrentMovie;

import java.util.List;

public final class Output {
    private String error = null;
    private List<CurrentMovie> currentMoviesList;
    private currentUser currentUser = null;

    public Output(final String error, final List<CurrentMovie> currentMoviesList,
                  final currentUser currentUser) {
        this.error = error;
        this.currentMoviesList = currentMoviesList;
        this.currentUser = currentUser;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public List<CurrentMovie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(final List<CurrentMovie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public currentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final currentUser currentUser) {
        this.currentUser = currentUser;
    }
}
