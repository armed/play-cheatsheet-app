package util;

import java.util.Iterator;

public class IteratorHelper {

    public static <T> T allOrNothing(Iterator<T> iter) {
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
