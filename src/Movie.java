import java.util.ArrayList;
import java.util.List;

class CurrentMovie extends Movie {
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

    CurrentMovie(final String name, final int year, final int duration, final List<String> genres,
                 final List<String> actors, final List<String> countriesBanned) {
        super(name, year, duration, genres, actors, countriesBanned);
    }

    CurrentMovie(final String name, final int year, final int duration, final List<String> genres,
                 final List<String> actors, final List<String> countriesBanned, final int numLikes,
                 final double rating, final int numRatings) {
        super(name, year, duration, genres, actors, countriesBanned);
        this.numLikes = numLikes;
        this.rating = rating;
        this.numRatings = numRatings;
    }

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
    private int year;
    private int duration;
    private List<String> genres;
    private List<String> actors;
    private List<String> countriesBanned;

    Movie(final String name, final int year, final int duration, final List<String> genres,
          final List<String> actors, final List<String> countriesBanned) {
        this.name = name;
        this.year = year;
        this.duration = duration;
        this.genres = genres;
        this.actors = actors;
        this.countriesBanned = countriesBanned;
    }

    Movie(final Movie movie) {
        this.name = movie.name;
        this.year = movie.year;
        this.duration = movie.duration;
        this.genres = new ArrayList<>(movie.genres);
        this.actors = new ArrayList<>(movie.actors);
        this.countriesBanned = new ArrayList<>(movie.countriesBanned);
    }

    Movie() { }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     *
     * @param year
     */
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(final int duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     *
     * @param genres
     */
    public void setGenres(final List<String> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     *
     * @param actors
     */
    public void setActors(final List<String> actors) {
        this.actors = actors;
    }

    /**
     *
     * @return
     */
    public List<String> getCountriesBanned() {
        return countriesBanned;
    }

    /**
     *
     * @param countriesBanned
     */
    public void setCountriesBanned(final List<String> countriesBanned) {
        this.countriesBanned = countriesBanned;
    }
}
