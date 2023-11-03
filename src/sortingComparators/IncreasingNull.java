package sortingComparators;

import movieClasses.CurrentMovie;
import java.util.Comparator;

/**
 * rating: increasing
 * duration: null
 */
public final class IncreasingNull implements Comparator<CurrentMovie> {
    /**
     *
     * @param cM1 the first object to be compared.
     * @param cM2 the second object to be compared.
     * @return
     */
    public int compare(final CurrentMovie cM1, final CurrentMovie cM2) {
        if (cM1.getRating() == cM2.getRating()) {
            return 0;
        } else if (cM1.getRating() < cM2.getRating()) {
            return -1;
        } else {
            return 1;
        }
    }
}
