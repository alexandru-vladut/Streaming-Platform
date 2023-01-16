import java.util.ArrayList;
import java.util.List;

class currentUser extends User {
    private int tokensCount = 0;
    private int numFreePremiumMovies = 15;
    private List<CurrentMovie> purchasedMovies;
    private List<CurrentMovie> watchedMovies;
    private List<CurrentMovie> likedMovies;
    private List<CurrentMovie> ratedMovies;
    private List<Notification> notifications;

    currentUser(final Credentials credentials) {
        super(credentials);
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    currentUser(final Credentials credentials, final currentUser currentUser) {
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

public class User {
    private Credentials credentials;

    User(final Credentials credentials) {
        this.credentials = credentials;
    }

    User(final User user) {
        this.credentials = new Credentials(user.credentials);
    }

    User() { }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }
}

class Credentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    Credentials(final String name, final String password, final String accountType,
                final String country, final String balance) {
        this.name = name;
        this.password = password;
        this.accountType = accountType;
        this.country = country;
        this.balance = balance;
    }

    Credentials(final Credentials credentials) {
        this.name = credentials.name;
        this.password = credentials.password;
        this.accountType = credentials.accountType;
        this.country = credentials.country;
        this.balance = credentials.balance;
    }

    Credentials() { }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(final String balance) {
        this.balance = balance;
    }
}
