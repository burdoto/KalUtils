package de.kaleidox.util.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This clas represents an Read/Write bridge to read and write from/to a file.
 *
 * @param <R> The Item type to be supplied by the reader.
 * @param <W> The Item type to be consumed by the writer.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class IOPort<R, W> {
    private Supplier<R> reader;
    private Consumer<W> writer;

    /**
     * Creates a new instance.
     *
     * @param reader A supplier to read from a file.
     * @param writer A consumer to write to a file.
     */
    public IOPort(Supplier<R> reader, Consumer<W> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Poses a fitting IO Port for a {@link PropertiesMapper}.
     *
     * @param file A file to fit the IO Port to.
     * @return An IO Port to be used by a {@link PropertiesMapper}.
     */
    public static IOPort<ConcurrentHashMap<String, String>, Map<String, String>> mapPort(File file) {
        return new IOPort<>(
                () -> {
                    Properties props = new Properties();
                    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

                    try {
                        props.load(new FileInputStream(file.getPath()));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    props.forEach((key, value) -> map.put(key.toString(), value.toString()));

                    return map;
                },
                item -> {
                    Properties props = new Properties();

                    for (Map.Entry<String, String> entry : item.entrySet()) {
                        props.put(entry.getKey(), entry.getValue());
                    }

                    try {
                        props.store(new FileOutputStream(file.getPath()), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    /**
     * Returns the content obtained by the reader; accords to {@code <R>}.
     *
     * @return The content of the reader.
     */
    public R read() {
        return reader.get();
    }

    /**
     * Reads the content of the reader as a collection; splitting by the regex defined in {@code splitWith}.
     *
     * @param supplier  A supplier to provide a list to fill.
     * @param splitWith A regex to split the content apart.
     * @return A collection that contains the split items.
     */
    public <T extends Collection<String>> Collection<String> readAsCollection(Supplier<T> supplier, String splitWith) {
        return new ArrayList<>(Arrays.asList(
                reader
                        .get()
                        .toString()
                        .split(splitWith)));
    }

    /**
     * Writes the given item to the file.
     *
     * @param item The item to write.
     */
    public void write(W item) {
        writer.accept(item);
    }
}
