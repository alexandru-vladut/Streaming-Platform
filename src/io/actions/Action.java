package io.actions;

import io.Movie;
import io.users.Credentials;

public final class Action {
    private String type;
    private String page;
    private String movie;
    private String feature;
    private Credentials credentials;
    private String startsWith;
    private String count;
    private String objectType;
    private int rate;
    private Filters filters;
    private String subscribedGenre;
    private Movie addedMovie;
    private String deletedMovie;

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

    public Movie getAddedMovie() {
        return addedMovie;
    }

    public String getDeletedMovie() {
        return deletedMovie;
    }
}
