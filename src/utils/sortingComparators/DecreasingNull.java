package utils.sortingComparators;

import movie.Movie;

import java.util.Comparator;

/**
 * rating: decreasing
 * duration: null
 */
public final class DecreasingNull implements Comparator<Movie> {
    /**
     *
     * @param cM1 the first object to be compared.
     * @param cM2 the second object to be compared.
     * @return
     */
    public int compare(final Movie cM1, final Movie cM2) {
        if (cM1.getRating() == cM2.getRating()) {
            return 0;
        } else if (cM1.getRating() < cM2.getRating()) {
            return 1;
        } else {
            return -1;
        }
    }
}
