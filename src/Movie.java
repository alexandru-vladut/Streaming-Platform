import java.util.ArrayList;
import java.util.List;

class currentMovie extends Movie{
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

    public currentMovie(String name, int year, int duration, List<String> genres, List<String> actors, List<String> countriesBanned) {
        super(name, year, duration, genres, actors, countriesBanned);
    }

    public currentMovie(String name, int year, int duration, List<String> genres, List<String> actors, List<String> countriesBanned, int numLikes, double rating, int numRatings) {
        super(name, year, duration, genres, actors, countriesBanned);
        this.numLikes = numLikes;
        this.rating = rating;
        this.numRatings = numRatings;
    }

    public currentMovie(currentMovie currentMovie) {
        super(currentMovie);
        this.numLikes = currentMovie.numLikes;
        this.rating = currentMovie.rating;
        this.numRatings = currentMovie.numRatings;
    }

    public currentMovie(Movie movie) {
        super(movie);
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

public class Movie{
    private String name;
    private int year;
    private int duration;
    private List<String> genres;
    private List<String> actors;
    private List<String> countriesBanned;

    public Movie(String name, int year, int duration, List<String> genres, List<String> actors, List<String> countriesBanned) {
        this.name = name;
        this.year = year;
        this.duration = duration;
        this.genres = genres;
        this.actors = actors;
        this.countriesBanned = countriesBanned;
    }

    public Movie(Movie movie) {
        this.name = movie.name;
        this.year = movie.year;
        this.duration = movie.duration;
        this.genres = new ArrayList<>(movie.genres);
        this.actors = new ArrayList<>(movie.actors);
        this.countriesBanned = new ArrayList<>(movie.countriesBanned);
    }

    public Movie() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public List<String> getCountriesBanned() {
        return countriesBanned;
    }

    public void setCountriesBanned(List<String> countriesBanned) {
        this.countriesBanned = countriesBanned;
    }
}
