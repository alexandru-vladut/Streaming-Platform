package movieClasses;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String name;
    private String year;
    private int duration;
    private List<String> genres;
    private List<String> actors;
    private List<String> countriesBanned;

    public Movie(final Movie movie) {
        this.name = movie.name;
        this.year = movie.year;
        this.duration = movie.duration;
        this.genres = new ArrayList<>(movie.genres);
        this.actors = new ArrayList<>(movie.actors);
        this.countriesBanned = new ArrayList<>(movie.countriesBanned);
    }

    public Movie() { }

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
