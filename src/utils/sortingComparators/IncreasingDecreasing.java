package utils.sortingComparators;

import movie.Movie;

import java.util.Comparator;

/**
 * rating: increasing
 * duration: decreasing
 */
public final class IncreasingDecreasing implements Comparator<Movie> {
    /**
     *
     * @param cM1 the first object to be compared.
     * @param cM2 the second object to be compared.
     * @return
     */
    public int compare(final Movie cM1, final Movie cM2) {
        if (cM1.getDuration() == cM2.getDuration()) {
            if (cM1.getRating() == cM2.getRating()) {
                return 0;
            } else if (cM1.getRating() < cM2.getRating()) {
                return -1;
            } else {
                return 1;
            }
        } else if (cM1.getDuration() < cM2.getDuration()) {
            return 1;
        } else {
            return -1;
        }
    }
}
