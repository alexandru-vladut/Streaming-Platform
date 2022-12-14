public class Output {
    private String error;
    private currentMovie[] currentMoviesList;
    private currentUser currentUser;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public currentMovie[] getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(currentMovie[] currentMoviesList) {
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
    private currentMovie[] purchasedMovies;
    private currentMovie[] watchedMovies;
    private currentMovie[] likedMovies;
    private currentMovie[] ratedMovies;

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

    public currentMovie[] getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(currentMovie[] purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public currentMovie[] getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(currentMovie[] watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public currentMovie[] getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(currentMovie[] likedMovies) {
        this.likedMovies = likedMovies;
    }

    public currentMovie[] getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(currentMovie[] ratedMovies) {
        this.ratedMovies = ratedMovies;
    }
}

class currentMovie extends Movie{
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

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
