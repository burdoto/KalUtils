package de.kaleidox.crystalshard.util.helpers;

import java.util.Iterator;
import java.util.Queue;

public class QueueHelper extends CollectionHelper {
    // Static members
    // Static membe
    public static <T> T getTail(Queue<T> queue) {
        Iterator<T> iterator = queue.iterator();
        T val = null;
        while (iterator.hasNext()) {
            val = iterator.next();
        }
        return val;
    }
}
