package de.kaleidox.util.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.kaleidox.util.helpers.JsonHelper;

/**
 * Marks a class as a potential Json node source.
 * Similar to {@linkplain JsonSerializable}, but with very simple syntax.
 *
 * @see JsonSerializable
 */
public interface JsonNodeable {
    /**
     * Creates a new JsonNode of this object.
     *
     * @return A JsonNode of this object.
     */
    default JsonNode toJsonNode() {
        return toJsonNode(JsonHelper.objectNode());
    }

    /**
     * Creates a new JsonNode of this object, upon the given baseNode.
     *
     * @param baseNode The node to apply this object to.
     * @return A JsonNode of this object.
     */
    JsonNode toJsonNode(ObjectNode baseNode);
}
