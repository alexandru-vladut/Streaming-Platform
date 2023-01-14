import java.util.List;

public final class Action {
    private final String type = null;
    private final String page = null;
    private final String movie = null;
    private final String feature = null;
    private final Credentials credentials = null;
    private final String startsWith = null;
    private final String count = null;
    private final String objectType = null;
    private int rate;
    private final Filters filters = null;

    public String getType() {
        return type;
    }

    public String getPage() {
        return page;
    }

    public String getMovie() {
        return movie;
    }

    public String getFeature() {
        return feature;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public String getCount() {
        return count;
    }

    public int getRate() {
        return rate;
    }

    public Filters getFilters() {
        return filters;
    }

    public String getObjectType() {
        return objectType;
    }
}

class Filters {
    private final Sort sort = null;
    private final Contains contains = null;

    public Sort getSort() {
        return sort;
    }

    public Contains getContains() {
        return contains;
    }
}

class Sort {
    private final String rating = null;
    private final String duration = null;

    public String getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

}

class Contains {
    private final List<String> actors = null;
    private final List<String> genre = null;

    public List<String> getActors() {
        return actors;
    }

    public List<String> getGenre() {
        return genre;
    }
}
