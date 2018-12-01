package de.kaleidox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public enum LogicalOperator {
    UNKNOWN("unknown", list -> false),
    AND("and", coll -> coll.stream().allMatch(b -> b)),
    OR("or", coll -> coll.stream().anyMatch(b -> b)),
    NAND("not", coll -> coll.stream().noneMatch(b -> b)),
    XOR("xor", coll -> coll.stream().filter(b -> b).count() < 2);

    private final String name;
    private final Function<Collection<Boolean>, Boolean> function;

    LogicalOperator(String name, Function<Collection<Boolean>, Boolean> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String toString() {
        return "LogicalOperator (" + name + ")";
    }

    public boolean test(Collection<Boolean> collection) {
        return test(b -> b, collection);
    }

    public <T> boolean test(Predicate<T> predicate, Collection<T> collection) {
        return collection.stream()
                .map(predicate::test)
                .collect(collector());
    }

    public Collector<Boolean, List<Boolean>, Boolean> collector() {
        return new CustomCollectors.CustomCollectorImpl<>(
                ArrayList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                function::apply,
                CustomCollectors.CH_NOID
        );
    }

    public static Optional<LogicalOperator> find(String tag) {
        return Stream.of(values())
                .filter(lo -> lo.name.equalsIgnoreCase(tag))
                .findAny();
    }
}
