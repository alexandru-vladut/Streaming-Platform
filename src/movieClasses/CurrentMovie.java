package movieClasses;

public class CurrentMovie extends Movie {
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

    public CurrentMovie(final CurrentMovie currentMovie) {
        super(currentMovie);
        this.numLikes = currentMovie.numLikes;
        this.rating = currentMovie.rating;
        this.numRatings = currentMovie.numRatings;
    }

    public CurrentMovie(final Movie movie) {
        super(movie);
    }

    public CurrentMovie() {
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(final int numLikes) {
        this.numLikes = numLikes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(final double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(final int numRatings) {
        this.numRatings = numRatings;
    }
}
