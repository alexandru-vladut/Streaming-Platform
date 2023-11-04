package user;

import movie.Movie;
import movie.OutputMovie;

import java.util.ArrayList;
import java.util.List;

public class OutputUser extends InputUser {
    private final int tokensCount;
    private final int numFreePremiumMovies;
    private final List<OutputMovie> purchasedMovies;
    private final List<OutputMovie> watchedMovies;
    private final List<OutputMovie> likedMovies;
    private final List<OutputMovie> ratedMovies;
    private final List<Notification> notifications;

    public OutputUser(User user) {
        super(user.getCredentials());
        this.tokensCount = user.getTokensCount();
        this.numFreePremiumMovies = user.getNumFreePremiumMovies();

        this.purchasedMovies = new ArrayList<>();
        for (Movie movie : user.getPurchasedMovies()) {
            OutputMovie movieCopy = new OutputMovie(movie);
            this.purchasedMovies.add(movieCopy);
        }

        this.watchedMovies = new ArrayList<>();
        for (Movie movie : user.getWatchedMovies()) {
            OutputMovie movieCopy = new OutputMovie(movie);
            this.watchedMovies.add(movieCopy);
        }

        this.likedMovies = new ArrayList<>();
        for (Movie movie : user.getLikedMovies()) {
            OutputMovie movieCopy = new OutputMovie(movie);
            this.likedMovies.add(movieCopy);
        }

        this.ratedMovies = new ArrayList<>();
        for (Movie movie : user.getRatedMovies()) {
            OutputMovie movieCopy = new OutputMovie(movie);
            this.ratedMovies.add(movieCopy);
        }

        this.notifications = new ArrayList<>();
        for (Notification notify : user.getNotifications()) {
            Notification notifyCopy = new Notification(notify);
            this.notifications.add(notifyCopy);
        }
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public List<OutputMovie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public List<OutputMovie> getWatchedMovies() {
        return watchedMovies;
    }

    public List<OutputMovie> getLikedMovies() {
        return likedMovies;
    }

    public List<OutputMovie> getRatedMovies() {
        return ratedMovies;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
