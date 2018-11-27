package de.kaleidox.util.serializer;

import java.util.function.Function;

/**
 * This class represents a double function, used by {@link PropertiesMapper}.
 * Used to convert an Object back and forth.
 *
 * @param <In>  The input type of the Function.
 * @param <Out> The output type of the Function.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DoubleFunction<In, Out> {
    public final static DoubleFunction<String, Object> STRING_OBJECT_DOUBLE_FUNCTION = new DoubleFunction<>(a -> a, Object::toString);
    public final static DoubleFunction<String, String> STRING_STRING_DOUBLE_FUNCTION = new DoubleFunction<>(a -> a, b -> b);
    public final static DoubleFunction<String, Integer> STRING_INTEGER_DOUBLE_FUNCTION = new DoubleFunction<>(Integer::parseInt, String::valueOf);
    public final static DoubleFunction<String, Long> STRING_LONG_DOUBLE_FUNCTION = new DoubleFunction<>(Long::parseLong, String::valueOf);
    public final static DoubleFunction<String, Double> STRING_DOUBLE_DOUBLE_FUNCTION = new DoubleFunction<>(Double::parseDouble, String::valueOf);
    public final static DoubleFunction<String, Float> STRING_FLOAT_DOUBLE_FUNCTION = new DoubleFunction<>(Float::parseFloat, String::valueOf);

    private final Function<In, Out> inputFunction;
    private final Function<Out, In> outputFunction;

    /**
     * Creates a new instance.
     *
     * @param inputFunction  A function to convert from {@code In} to {@code Out}.
     * @param outputFunction A function to convert from {@code Out} to {@code In}.
     */
    public DoubleFunction(
            Function<In, Out> inputFunction,
            Function<Out, In> outputFunction) {
        this.inputFunction = inputFunction;
        this.outputFunction = outputFunction;
    }

    /**
     * Converts an item from {@code In} to {@code Out}.
     *
     * @param item The item to convert.
     * @return the converted item.
     */
    public Out toOutput(In item) {
        return inputFunction.apply(item);
    }

    /**
     * Converts an item from {@code Out} to {@code In}.
     *
     * @param item The item to convert.
     * @return the converted item.
     */
    public In toInput(Out item) {
        return outputFunction.apply(item);
    }
}
