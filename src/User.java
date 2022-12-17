import java.util.ArrayList;
import java.util.List;

class currentUser extends User{
    private int tokensCount = 0;
    private int numFreePremiumMovies = 15;
    private List<currentMovie> purchasedMovies;
    private List<currentMovie> watchedMovies;
    private List<currentMovie> likedMovies;
    private List<currentMovie> ratedMovies;

    public currentUser(Credentials credentials) {
        super(credentials);
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
    }

    public currentUser(Credentials credentials, int tokensCount, int numFreePremiumMovies, List<currentMovie> purchasedMovies, List<currentMovie> watchedMovies, List<currentMovie> likedMovies, List<currentMovie> ratedMovies) {
        super(credentials);
        this.tokensCount = tokensCount;
        this.numFreePremiumMovies = numFreePremiumMovies;
        this.purchasedMovies = purchasedMovies;
        this.watchedMovies = watchedMovies;
        this.likedMovies = likedMovies;
        this.ratedMovies = ratedMovies;
    }

    public currentUser(Credentials credentials, currentUser currentUser) {
        super(credentials);
        this.tokensCount = currentUser.tokensCount;
        this.numFreePremiumMovies = currentUser.numFreePremiumMovies;

        this.purchasedMovies = new ArrayList<>();
        for (currentMovie movie : currentUser.getPurchasedMovies()) {
            currentMovie movieCopy = new currentMovie(movie);
            this.purchasedMovies.add(movieCopy);
        }

        this.watchedMovies = new ArrayList<>();
        for (currentMovie movie : currentUser.getWatchedMovies()) {
            currentMovie movieCopy = new currentMovie(movie);
            this.watchedMovies.add(movieCopy);
        }

        this.likedMovies = new ArrayList<>();
        for (currentMovie movie : currentUser.getLikedMovies()) {
            currentMovie movieCopy = new currentMovie(movie);
            this.likedMovies.add(movieCopy);
        }

        this.ratedMovies = new ArrayList<>();
        for (currentMovie movie : currentUser.getRatedMovies()) {
            currentMovie movieCopy = new currentMovie(movie);
            this.ratedMovies.add(movieCopy);
        }
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public List<currentMovie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(List<currentMovie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public List<currentMovie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(List<currentMovie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public List<currentMovie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(List<currentMovie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public List<currentMovie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(List<currentMovie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }
}

public class User {
    private Credentials credentials;

    public User(Credentials credentials) {
        this.credentials = credentials;
    }

    public User(User user) {
        this.credentials = new Credentials(user.credentials);
    }

    public User() {}

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}

//class loginCredentials{
//    private String name;
//    private String password;
//
//    public loginCredentials(String name, String password) {
//        this.name = name;
//        this.password = password;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}

class Credentials{
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    public Credentials(String name, String password, String accountType, String country, String balance) {
        this.name = name;
        this.password = password;
        this.accountType = accountType;
        this.country = country;
        this.balance = balance;
    }

    public Credentials(Credentials credentials) {
        this.name = credentials.name;
        this.password = credentials.password;
        this.accountType = credentials.accountType;
        this.country = credentials.country;
        this.balance = credentials.balance;
    }

    public Credentials() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
