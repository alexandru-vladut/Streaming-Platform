package user;

public class InputUser {
    private Credentials credentials;

    public InputUser(final Credentials credentials) {
        this.credentials = new Credentials(credentials);
    }

    public InputUser(final InputUser user) {
        this.credentials = new Credentials(user.credentials);
    }

    public InputUser() { }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }
}

