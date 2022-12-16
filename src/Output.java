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

class currentUser extends User{
    private int tokensCount = 0;
    private int numFreePremiumMovies = 15;
    private List<currentMovie> purchasedMovies;
    private List<currentMovie> watchedMovies;
    private List<currentMovie> likedMovies;
    private List<currentMovie> ratedMovies;

    public currentUser(Credentials credentials) {
        super(credentials);
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
    }


    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public List<currentMovie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(List<currentMovie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public List<currentMovie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(List<currentMovie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public List<currentMovie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(List<currentMovie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public List<currentMovie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(List<currentMovie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }
}

class currentMovie extends Movie{
    private int numLikes;
    private double rating;
    private int numRatings;

    public currentMovie(String name, int year, int duration, List<String> genres, List<String> actors, List<String> countriesBanned) {
        super(name, year, duration, genres, actors, countriesBanned);
        numLikes = 0;
        rating = 0;
        numRatings = 0;
    }

    public currentMovie() {}

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }
}
