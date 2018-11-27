package de.kaleidox.util.objects.markers;

public class IDPair {
    private final long one;
    private final long two;

    private IDPair(long one, long two) {
        this.one = one;
        this.two = two;
    }

    public static IDPair of(long one, long two) {
        return new IDPair(one, two);
    }

    public long getOne() {
        return one;
    }

    public long getTwo() {
        return two;
    }
}
