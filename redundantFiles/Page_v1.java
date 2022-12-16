public class Page_v1 {
    private String type;
    private String page;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}

class changePageSeeDetails extends Page_v1{
    private String movie;

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }
}

// Buy premium account, Purchase, Watch, Like.
class onPage extends Page_v1{
    private String feature;
}

class Register extends onPage{
    private Credentials credentials;

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}

class Login extends onPage{
    private loginCredentials loginCredentials;

    public loginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    public void setLoginCredentials(loginCredentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }
}

class Search extends onPage{
    private String startsWith;

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }
}

class buyTokens extends onPage{
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

class rateMovie extends onPage{
    private int rate;

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}

class Filter extends onPage{
    private Filters filters;

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }
}

class Filters{
    Sort sort;
    Contains contains;

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Contains getContains() {
        return contains;
    }

    public void setContains(Contains contains) {
        this.contains = contains;
    }
}

class Sort{
    private String rating;
    private String duration;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

class Contains{
    private String[] actors;
    private String[] genre;

    public String[] getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        this.actors = actors;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }
}
