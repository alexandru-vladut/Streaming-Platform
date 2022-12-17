import java.util.ArrayList;
import java.util.List;

public class Output {
    private String error = null;
    private List<currentMovie> currentMoviesList;
    private currentUser currentUser = null;

    public Output(String error, List<currentMovie> currentMoviesList, currentUser currentUser) {
        this.error = error;
        this.currentMoviesList = currentMoviesList;
        this.currentUser = currentUser;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<currentMovie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(List<currentMovie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public currentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(currentUser currentUser) {
        this.currentUser = currentUser;
    }
}
