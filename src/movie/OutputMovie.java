package movie;

public class OutputMovie extends InputMovie {
    private final int numLikes;
    private final double rating;
    private final int numRatings;

    public OutputMovie(Movie movie) {
        super(movie);
        this.numLikes = movie.getNumLikes();
        this.rating = movie.getRating();
        this.numRatings = movie.getNumRatings();
    }

    public int getNumLikes() {
        return numLikes;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRatings() {
        return numRatings;
    }
}
