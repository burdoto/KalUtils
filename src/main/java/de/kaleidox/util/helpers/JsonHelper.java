package de.kaleidox.util.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.kaleidox.util.exception.IllegalTypeException;
import de.kaleidox.util.interfaces.JsonNodeable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public final class JsonHelper extends NullHelper {
    public static JsonNode nodeOf(Object of) {
        if (of == null) {
            return JsonNodeFactory.instance.nullNode();
        } else if (of instanceof JsonNode) {
            return (JsonNode) of;
        } else if (of instanceof JsonNodeable) {
            return ((JsonNodeable) of).toJsonNode();
        } else if (of instanceof Collection) {
            //noinspection unchecked
            return arrayNode((Collection) of);
        } else if (of instanceof Stream) {
            return arrayNode(((Stream) of).toArray());
        } else if (of instanceof Integer) {
            return JsonNodeFactory.instance.numberNode((Integer) of);
        } else if (of instanceof Long) {
            return JsonNodeFactory.instance.numberNode((Long) of);
        } else if (of instanceof Double) {
            return JsonNodeFactory.instance.numberNode((Double) of);
        } else if (of instanceof String) {
            return JsonNodeFactory.instance.textNode((String) of);
        } else if (of instanceof Boolean) {
            return JsonNodeFactory.instance.booleanNode((Boolean) of);
        } else {
            return JsonNodeFactory.instance.textNode(of.toString());
        }
    }

    public static Object ofNode(JsonNode field) {
        switch (field.getNodeType()) {
            case NULL:
                return null;
            case NUMBER:
                field.numberValue();
            case STRING:
                return field.textValue();
            case BOOLEAN:
                return field.booleanValue();
            case MISSING:
                return null;
            default:
                throw new IllegalTypeException(field.getNodeType().toString());
        }
    }

    public static <T, N> ArrayNode arrayNode(List<T> items, Function<T, N> mapper) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.size());
        for (T item : items) node.add(nodeOf(mapper.apply(item)));
        return node;
    }

    public static <T> ArrayNode arrayNode(Collection<T> items) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.size());
        for (T item : items) node.add(nodeOf(item));
        return node;
    }

    public static ArrayNode arrayNode(Object... items) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.length);
        for (Object item : items)
            node.add(nodeOf(item));
        return node;
    }

    public static ObjectNode objectNode(Object... data) {
        if (data.length == 0) return JsonNodeFactory.instance.objectNode();
        if (data.length % 2 != 0)
            throw new IllegalArgumentException("You must provide an even amount of objects to be placed in the node.");
        ObjectNode objectNode = objectNode();
        for (List<Object> pair : ListHelper.everyOfList(2, ListHelper.of(data))) {
            if (Objects.nonNull(pair.get(0)) && Objects.nonNull(pair.get(1))) objectNode.set(pair.get(0)
                    .toString(), nodeOf(pair.get(1)));
            // ignore all pairs of which both sides are NULL
        }
        return objectNode;
    }

    public static JsonNode parse(String body) {
        try {
            return new ObjectMapper().readTree(body);
        } catch (IOException e) {
            return objectNode();
        }
    }

    public static JsonNode parseExceptional(String body) throws IOException {
        return new ObjectMapper().readTree(body);
    }
}
