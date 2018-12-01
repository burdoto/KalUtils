package de.kaleidox.util.config;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static de.kaleidox.util.helpers.JsonHelper.ofNode;
import static de.kaleidox.util.helpers.JsonHelper.parseExceptional;

public class Configuration extends Hashtable<String, Configuration.ConfigNode> {
    public final static String BASE_PATH = "config/";

    public Configuration(String name) {
        super();
        File file = new File(BASE_PATH + name);

        try {
            InputStream resStream = ClassLoader.getSystemResourceAsStream(BASE_PATH + name);
            if (resStream != null) readFile(resStream);
            else {
                if (!file.exists()) file.createNewFile();
                readFile(new FileInputStream(file));
            }
        } catch (IOException ignored) {
        }
    }

    public Configuration register(String varName, Object value) {
        return register(varName, value, Function.identity());
    }

    public <T> Configuration register(String varName, T value, Function<Object, T> mapper) {
        if (containsKey(varName)) {
            ConfigNode node = get(varName);
            node.def = value;
            node.mapper = mapper;
        } else put(varName, new ConfigNode(value, mapper));
        return this;
    }

    public String var(String varName) throws NoSuchElementException {
        return var(varName, Object.class).toString();
    }

    public <V> V var(String varName, Class<V> as) throws NoSuchElementException, ClassCastException {
        return as.cast(getRaw(varName));
    }

    private Object getRaw(String varName) throws NoSuchElementException {
        if (containsKey(varName)) {
            ConfigNode configNode = get(varName);
            return configNode.mapper.apply(configNode.get());
        } else throw new NoSuchElementException("Variable not found: " + varName);
    }

    private void readFile(InputStream stream) throws IOException, IllegalArgumentException {
        int r;
        StringBuilder sb = new StringBuilder();
        while ((r = stream.read()) != -1) sb.append((char) r);
        dissolveNode(parseExceptional(sb.toString()), "");
    }

    private void dissolveNode(JsonNode node, String underlying) throws IllegalArgumentException {
        if (node.isArray()) throw new IllegalArgumentException("ArrayNodes are currently unsupported!");
        Iterator<String> fields = node.fieldNames();

        while (fields.hasNext()) {
            final String name = fields.next();
            final JsonNode field = node.path(name);
            final String path = String.format("%s%s%s", underlying, underlying.equals("") ? "" : ".", name);

            if (field.isObject()) dissolveNode(field, path);
            else {
                Object ofNode = ofNode(field);
                if (!containsKey(path)) put(path, new ConfigNode(ofNode, Function.identity()));
                else throw new AssertionError();
            }
        }
    }

    private static JsonPointer buildPointer(String from) {
        return JsonPointer.compile("/" + String.join("/", from.split(".")));
    }

    protected class ConfigNode {
        protected final Object act;
        protected Object def;
        protected Function<Object, ?> mapper;
        private boolean lock = false;

        private ConfigNode(Object act, Function<Object, ?> mapper) {
            this.act = act;
            this.mapper = mapper;
        }

        private void def(Object def) {
            if (lock) throw new IllegalAccessError("Node has been locked!");
            this.def = def;
            lock = true;
        }

        private Object get() {
            return act == null ? def : act;
        }
    }
}
