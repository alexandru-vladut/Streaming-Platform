package utils;

import movie.InputMovie;
import user.Credentials;

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
    private final String subscribedGenre = null;
    private final InputMovie addedMovie = null;
    private final String deletedMovie = null;

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

    public String getSubscribedGenre() {
        return subscribedGenre;
    }

    public InputMovie getAddedMovie() {
        return addedMovie;
    }

    public String getDeletedMovie() {
        return deletedMovie;
    }
}
