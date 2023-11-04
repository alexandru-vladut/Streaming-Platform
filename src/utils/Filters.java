package utils;

import java.util.List;

public class Filters {
    private final Sort sort = null;
    private final Contains contains = null;

    public static class Sort {
        private final String rating = null;
        private final String duration = null;

        public String getRating() {
            return rating;
        }

        public String getDuration() {
            return duration;
        }

    }

    public static class Contains {
        private final List<String> actors = null;
        private final List<String> genre = null;

        public List<String> getActors() {
            return actors;
        }

        public List<String> getGenre() {
            return genre;
        }
    }

    public Sort getSort() {
        return sort;
    }

    public Contains getContains() {
        return contains;
    }
}
