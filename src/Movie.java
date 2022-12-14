public class Movie{
    private String name;
    private String year;
    private int duration;
    private String[] genres;
    private String[] actors;
    private String[] countriesBanned;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        this.actors = actors;
    }

    public String[] getCountriesBanned() {
        return countriesBanned;
    }

    public void setCountriesBanned(String[] countriesBanned) {
        this.countriesBanned = countriesBanned;
    }
}
