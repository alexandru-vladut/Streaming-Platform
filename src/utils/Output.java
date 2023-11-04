package utils;

import movie.OutputMovie;
import user.OutputUser;

import java.util.List;

public final class Output {
    private String error;
    private List<OutputMovie> currentMoviesList;
    private OutputUser currentUser;

    public Output(final String error, final List<OutputMovie> currentMoviesList,
                  final OutputUser currentUser) {
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

    public List<OutputMovie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(final List<OutputMovie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public OutputUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final OutputUser currentUser) {
        this.currentUser = currentUser;
    }
}
