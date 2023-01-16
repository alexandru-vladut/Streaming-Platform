public class Notification {
    private String movieName;
    private String message;

    Notification(String movieName, String message) {
        this.movieName = movieName;
        this.message = message;
    }

    Notification(final Notification notification) {
        this.movieName = notification.movieName;
        this.message = notification.message;
    }

    Notification() {}

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
