package de.kaleidox.util;

import de.kaleidox.util.functional.Evaluation;
import de.kaleidox.util.helpers.ListHelper;

import java.util.ArrayList;
import java.util.List;

public class Difference<T> {
    private final List<T> added;
    private final List<T> removed;

    private Difference(List<T> added, List<T> removed) {
        this.added = added;
        this.removed = removed;
    }

    public List<T> getAdded() {
        return added;
    }

    public Evaluation<Boolean> hasAdded() {
        return Evaluation.of(!added.isEmpty());
    }

    public Evaluation<Boolean> hasAdded(T obj) {
        return Evaluation.of(added.contains(obj));
    }

    public List<T> getRemoved() {
        return removed;
    }

    public Evaluation<Boolean> hasRemoved() {
        return Evaluation.of(!removed.isEmpty());
    }

    public Evaluation<Boolean> hasRemoved(T obj) {
        return Evaluation.of(removed.contains(obj));
    }

    public static <T> Difference<T> of(List<T> original, List<T> changed) {
        class Builder<A> {
            private final List<A> added;
            private final List<A> removed;

            Builder() {
                added = new ArrayList<>();
                removed = new ArrayList<>();
            }

            void addAdded(A item) {
                added.add(item);
            }

            void addRemoved(A item) {
                removed.add(item);
            }

            Difference<A> build() {
                return new Difference<>(added, removed);
            }
        }

        Builder<T> builder = new Builder<>();

        for (T t : original) if (!ListHelper.containsEquals(changed, t)) builder.addRemoved(t);
        for (T t : changed) if (!ListHelper.containsEquals(original, t)) builder.addAdded(t);

        return builder.build();
    }
}
