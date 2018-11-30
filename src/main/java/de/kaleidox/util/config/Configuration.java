package de.kaleidox.util.config;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

import de.kaleidox.util.annotations.Grouping;
import de.kaleidox.util.helpers.ListHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static de.kaleidox.util.helpers.JsonHelper.ofNode;
import static de.kaleidox.util.helpers.JsonHelper.parseExceptional;

public class Configuration extends Hashtable<String, Configuration.ConfigNode> {
    public final static String BASE_PATH = "config/";

    public Configuration(String name) {
        super();
        File file = new File(BASE_PATH + name);

        try {
            if (!file.exists()) file.createNewFile();
            readFile(file);
        } catch (IOException ignored) {}
    }

    public Configuration register(@Grouping Object... vars) throws IllegalArgumentException {
        if (vars.length % 2 != 0) throw new IllegalArgumentException("Illegal amount of arguments!");
        List<List<Object>> groups = ListHelper.everyOfList(2, Arrays.asList(vars));
        Hashtable<String, ConfigNode> table = new Hashtable<>();
        for (List<Object> group : groups) table.put(group.get(0).toString(), new ConfigNode(group.get(1)));
        putAll(table);
        return this;
    }

    public Object get(String varName) throws NoSuchElementException {
        return get(varName, Object.class);
    }

    public <V> V get(String varName, Class<V> as) throws NoSuchElementException, ClassCastException {
        if (contains(varName)) {
            Object o = get((Object) varName).get();
            return as.cast(o);
        } else throw new NoSuchElementException("Variable not found: " + varName);
    }

    private void readFile(File file) throws IOException, IllegalArgumentException {
        FileInputStream stream = new FileInputStream(file);
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
            final String path = String.format("%s.%s", underlying, name);

            if (field.isObject()) dissolveNode(field, path);
            else get((Object) path).actual(ofNode(field));
        }
    }

    private static JsonPointer buildPointer(String from) {
        return JsonPointer.compile("/" + String.join("/", from.split(".")));
    }

    protected class ConfigNode {
        private final Object def;
        private Object act;
        private boolean lock = false;

        private ConfigNode(Object def) {
            this.def = def;
        }

        private void actual(Object act) {
            if (lock) throw new IllegalAccessError("Node has been locked!");
            this.act = act;
            lock = true;
        }

        private Object get() {
            if (act == null) return def;
            return act;
        }
    }
}
