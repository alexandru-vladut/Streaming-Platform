package io.users;


public class User {
    private Credentials credentials;

    public User(final Credentials credentials) {
        this.credentials = credentials;
    }

    public User(final User user) {
        this.credentials = new Credentials(user.credentials);
    }

    public User() { }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }
}
