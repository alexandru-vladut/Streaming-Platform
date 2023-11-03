package sortingComparators;

import movieClasses.CurrentMovie;
import java.util.Comparator;

/**
 * rating: null
 * duration: increasing
 */
public final class NullIncreasing implements Comparator<CurrentMovie> {
    /**
     *
     * @param cM1 the first object to be compared.
     * @param cM2 the second object to be compared.
     * @return
     */
    public int compare(final CurrentMovie cM1, final CurrentMovie cM2) {
        if (cM1.getDuration() == cM2.getDuration()) {
            return 0;
        } else if (cM1.getDuration() < cM2.getDuration()) {
            return -1;
        } else {
            return 1;
        }
    }
}
