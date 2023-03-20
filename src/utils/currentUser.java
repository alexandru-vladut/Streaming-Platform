package utils;

import io.users.Credentials;
import io.users.User;

import java.util.ArrayList;
import java.util.List;

public class currentUser extends User {
    private int tokensCount = 0;
    private int numFreePremiumMovies = 15;
    private List<CurrentMovie> purchasedMovies;
    private List<CurrentMovie> watchedMovies;
    private List<CurrentMovie> likedMovies;
    private List<CurrentMovie> ratedMovies;
    private List<Notification> notifications;

    public currentUser(final Credentials credentials) {
        super(credentials);
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public currentUser(final Credentials credentials, final currentUser currentUser) {
        super(credentials);
        this.tokensCount = currentUser.tokensCount;
        this.numFreePremiumMovies = currentUser.numFreePremiumMovies;

        this.purchasedMovies = new ArrayList<>();
        for (CurrentMovie movie : currentUser.getPurchasedMovies()) {
            CurrentMovie movieCopy = new CurrentMovie(movie);
            this.purchasedMovies.add(movieCopy);
        }

        this.watchedMovies = new ArrayList<>();
        for (CurrentMovie movie : currentUser.getWatchedMovies()) {
            CurrentMovie movieCopy = new CurrentMovie(movie);
            this.watchedMovies.add(movieCopy);
        }

        this.likedMovies = new ArrayList<>();
        for (CurrentMovie movie : currentUser.getLikedMovies()) {
            CurrentMovie movieCopy = new CurrentMovie(movie);
            this.likedMovies.add(movieCopy);
        }

        this.ratedMovies = new ArrayList<>();
        for (CurrentMovie movie : currentUser.getRatedMovies()) {
            CurrentMovie movieCopy = new CurrentMovie(movie);
            this.ratedMovies.add(movieCopy);
        }

        this.notifications = new ArrayList<>();
        for (Notification notify : currentUser.getNotifications()) {
            Notification notifyCopy = new Notification(notify);
            this.notifications.add(notifyCopy);
        }
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(final int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(final int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public List<CurrentMovie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(final List<CurrentMovie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public List<CurrentMovie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(final List<CurrentMovie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public List<CurrentMovie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(final List<CurrentMovie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public List<CurrentMovie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(final List<CurrentMovie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}