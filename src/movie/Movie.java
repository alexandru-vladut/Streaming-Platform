package movie;

public class Movie extends InputMovie {
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;
    private double sumRatings = 0;

    public Movie(Movie movie) {
        super(movie);
        this.numLikes = movie.numLikes;
        this.rating = movie.rating;
        this.numRatings = movie.numRatings;
        this.sumRatings = movie.sumRatings;
    }

    public Movie(InputMovie movie) {
        super(movie);
    }

    public Movie() {
    }

    public void addRating(double value) {
        sumRatings += value;
        numRatings += 1;
        rating = sumRatings / (double) numRatings;
    }

    public void updateRating(double oldValue, double newValue, boolean alreadyRated) {
        if (alreadyRated) {
            sumRatings -= oldValue;
            sumRatings += newValue;
        } else {
            sumRatings += newValue;
            numRatings += 1;
        }
        rating = sumRatings / (double) numRatings;
    }

    public void incrementNumLikes() {
        numLikes += 1;
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

    public double getSumRatings() {
        return sumRatings;
    }

    public void setSumRatings(final int sumRatings) {
        this.sumRatings = sumRatings;
    }
}
