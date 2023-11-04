package user;

import movie.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User extends InputUser {
    private int tokensCount = 0;
    private int numFreePremiumMovies = 15;
    private List<Movie> purchasedMovies;
    private List<Movie> watchedMovies;
    private List<Movie> likedMovies;
    private List<Movie> ratedMovies;
    private List<Notification> notifications;
    private List<String> subscribedGenres;
    private HashMap<String, Integer> userRatings;

    public User(Credentials credentials) {
        super(credentials);
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
        notifications = new ArrayList<>();
        subscribedGenres = new ArrayList<>();
        userRatings = new HashMap<>();
    }

    public User(Credentials credentials, User user) {
        super(credentials);
        this.tokensCount = user.tokensCount;
        this.numFreePremiumMovies = user.numFreePremiumMovies;

        this.purchasedMovies = new ArrayList<>();
        for (Movie movie : user.getPurchasedMovies()) {
            Movie movieCopy = new Movie(movie);
            this.purchasedMovies.add(movieCopy);
        }

        this.watchedMovies = new ArrayList<>();
        for (Movie movie : user.getWatchedMovies()) {
            Movie movieCopy = new Movie(movie);
            this.watchedMovies.add(movieCopy);
        }

        this.likedMovies = new ArrayList<>();
        for (Movie movie : user.getLikedMovies()) {
            Movie movieCopy = new Movie(movie);
            this.likedMovies.add(movieCopy);
        }

        this.ratedMovies = new ArrayList<>();
        for (Movie movie : user.getRatedMovies()) {
            Movie movieCopy = new Movie(movie);
            this.ratedMovies.add(movieCopy);
        }

        this.notifications = new ArrayList<>();
        for (Notification notify : user.getNotifications()) {
            Notification notifyCopy = new Notification(notify);
            this.notifications.add(notifyCopy);
        }
    }

    public void modifyTokensCount(int value) {
        tokensCount += value;
    }

    public void modifyFreePremiumMovies(int value) {
        numFreePremiumMovies += value;
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

    public List<Movie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(final List<Movie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public List<Movie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(final List<Movie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public List<Movie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(final List<Movie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public List<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(final List<Movie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<String> getSubscribedGenres() {
        return subscribedGenres;
    }

    public void setSubscribedGenres(List<String> subscribedGenres) {
        this.subscribedGenres = subscribedGenres;
    }

    public HashMap<String, Integer> getUserRatings() {
        return userRatings;
    }
}
