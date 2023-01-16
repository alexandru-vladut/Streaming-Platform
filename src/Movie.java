import java.util.ArrayList;
import java.util.List;

class CurrentMovie extends Movie {
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

    CurrentMovie(final CurrentMovie currentMovie) {
        super(currentMovie);
        this.numLikes = currentMovie.numLikes;
        this.rating = currentMovie.rating;
        this.numRatings = currentMovie.numRatings;
    }

    CurrentMovie(final Movie movie) {
        super(movie);
    }

    CurrentMovie() {
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

public class Movie {
    private String name;
    private String year;
    private int duration;
    private List<String> genres;
    private List<String> actors;
    private List<String> countriesBanned;

    Movie(final Movie movie) {
        this.name = movie.name;
        this.year = movie.year;
        this.duration = movie.duration;
        this.genres = new ArrayList<>(movie.genres);
        this.actors = new ArrayList<>(movie.actors);
        this.countriesBanned = new ArrayList<>(movie.countriesBanned);
    }

    Movie() { }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(final List<String> genres) {
        this.genres = genres;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(final List<String> actors) {
        this.actors = actors;
    }

    public List<String> getCountriesBanned() {
        return countriesBanned;
    }

    public void setCountriesBanned(final List<String> countriesBanned) {
        this.countriesBanned = countriesBanned;
    }
}
