package de.kaleidox.util.serializer;

import de.kaleidox.util.toolchains.CustomCollectors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a PropertiesMapper, which can be used to store things in a {@code .properties} File.
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue", "NullableProblems"})
public class PropertiesMapper<K, V> extends ConcurrentHashMap<K, List<V>> implements Iterable<V> {
    private final static Character[] splitterList = new Character[]{'▪'};
    protected final ArrayList<Character> deadCharacters = new ArrayList<>();
    protected final IOPort<ConcurrentHashMap<String, String>, Map<String, String>> ioPort;
    protected final Character splitWith;
    protected final DoubleFunction<String, K> keyFunction;
    protected final DoubleFunction<String, V> valueFunction;

    /**
     * Creates a new instance.
     *
     * @param file          A file to read from. Must be a {@code .properties} file.
     * @param keyFunction   A DoubleFunction to convert the keys.
     * @param valueFunction A DoubleFunction to convert the values.
     */
    public PropertiesMapper(
            File file,
            DoubleFunction<String, K> keyFunction,
            DoubleFunction<String, V> valueFunction) {
        this(
                IOPort.mapPort(file),
                null,
                keyFunction,
                valueFunction
        );
    }

    /**
     * Creates a new instance.
     *
     * @param ioPort        The IOPort to read and write from.
     * @param splitWith     A Nullable custom Character to split the entries in the file with.
     * @param keyFunction   A DoubleFunction to convert the keys.
     * @param valueFunction A DoubleFunction to convert the values.
     */
    public PropertiesMapper(
            IOPort<ConcurrentHashMap<String, String>, Map<String, String>> ioPort,
            @Nullable Character splitWith,
            DoubleFunction<String, K> keyFunction,
            DoubleFunction<String, V> valueFunction) {
        super();
        this.ioPort = ioPort;
        this.splitWith = (splitWith == null ? selectSplitter() : splitWith);
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;

        reloadFromFile();
    }

    // Getter Methods

    /**
     * Gets the splitter character.
     *
     * @return the character that the entries in the file are being split with.
     */
    public Character getSplitWith() {
        return splitWith;
    }

    /**
     * Gets the used IO Port.
     *
     * @return the IO Port that is used to read and write to the file.
     */
    public IOPort<ConcurrentHashMap<String, String>, Map<String, String>> getIoPort() {
        return ioPort;
    }

    /**
     * Returns a SelectedPropertiesMapper with this instance's pieces.
     *
     * @param key The key to select.
     * @return the new SelectedPropertiesMapper.
     */
    public SelectedPropertiesMapper<K, V> select(K key) {
        return new SelectedPropertiesMapper<>(
                this.ioPort,
                key,
                this.splitWith,
                this.keyFunction,
                this.valueFunction
        );
    }

    @Override
    public List<V> get(Object key) {
        return super.get(key);
    }

    /**
     * Gets the item in the list, if available.
     *
     * @param key   The key to look at.
     * @param index The index of the item to get.
     * @return the item at the given index.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#get(int)
     */
    public V get(K key, int index) {
        provideList(key);
        return get(key).get(index);
    }

    /**
     * Gets an item in the list, if available; otherwise returns an absence value.
     *
     * @param key           The key to look at.
     * @param index         The index of the item to get.
     * @param valueIfAbsent The value to return if the element is not available.
     * @return the item at the given index, otherwise the absence value.
     */
    public V getOrDefault(K key, int index, V valueIfAbsent) {
        provideList(key);
        if (get(key).size() > index) {
            return get(key).get(index);
        }

        return valueIfAbsent;
    }

    /**
     * Adds an item to the list.
     * When converted to a String,
     *
     * @param key   The key to add the value to.
     * @param value The value to add.
     *              Gets appended at the end of the list.
     * @return whether or not the value could be added.
     * @see ArrayList#add(Object)
     */
    public boolean add(K key, V value) {
        testForIllegalCharacters(value);
        provideList(key);
        boolean add = get(key).add(value);
        if (add) writeToFile();
        return add;
    }

    /**
     * Adds an item to the list if the value is not in the list yet.
     * Therefore this method does not allow duplications.
     *
     * @param key   The key to add the value to.
     * @param value The value to add.
     * @return whether the value could be added. This is false if the value was already in the list.
     * @see ArrayList#add(Object)
     */
    public boolean addIfValueAbsent(K key, V value) {
        provideList(key);
        if (!get(key).contains(value)) {
            boolean add = get(key).add(value);
            if (add) writeToFile();
            return add;
        }

        return false;
    }

    /**
     * Adds a value if a predicate for this Instance tests true.
     *
     * @param key          The key to add to.
     * @param value        The value to add.
     * @param mapPredicate A predicate to check this Instance.
     * @return whether the item could be added. This is false, if the predicate tested false.
     */
    public boolean addIfPredicate(K key, V value, Predicate<PropertiesMapper<K, V>> mapPredicate) {
        provideList(key);
        if (mapPredicate.test(this)) {
            boolean add = get(key).add(value);
            if (add) writeToFile();
            return add;
        }
        return false;
    }

    /**
     * Sets an index in a list.
     *
     * @param key   The key to set at.
     * @param index The index to set at.
     * @param value The value to set.
     * @return the element that was previously in this position.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public V set(K key, int index, V value) {
        provideList(key);
        V set = get(key).set(index, value);
        writeToFile();
        return set;
    }

    /**
     * Sets an item at a position in the list if the value is not in the list yet.
     *
     * @param key   The key to set at.
     * @param index The index to set at.
     * @param value The value to set.
     * @return whether the value could be set. This is false if the value was already in the list.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setIfValueAbsent(K key, int index, V value) {
        provideList(key);
        if (!get(key).contains(value)) {
            get(key).set(index, value);
            writeToFile();
            return true;
        }
        return false;
    }

    /**
     * Sets an item at a position in  the list if a given Predicate tests this Instance true.
     *
     * @param key          The key to set at.
     * @param index        The index to set at.
     * @param value        The value to set.
     * @param mapPredicate A predicate to test this Instance.
     * @return whether the value could be set. This is false if the predicate tested false.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setIfPredicate(K key, int index, V value, Predicate<PropertiesMapper<K, V>> mapPredicate) {
        provideList(key);
        if (mapPredicate.test(this)) {
            get(key).set(index, value);
            writeToFile();
            return true;
        }
        return false;
    }

    /**
     * Checks whether the instance has a given value at a given key, and checks if the index of
     * that value is equal to the given index. If it is not, then the value is set to the given index.
     *
     * @param key   The key to set at.
     * @param index The index to set at.
     * @param value The value to set.
     * @return whether the value was set.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setToCoordinates(K key, int index, V value) {
        provideList(key);
        if (get(key).contains(value) & get(key).indexOf(value) != index) {
            get(key).set(index, value);
            writeToFile();
            return true;
        }
        return false;
    }

    /**
     * Checks whether the given value is contained in any key.
     *
     * @param value The value to check for.
     * @return whether the value is contained in any key.
     * @see ArrayList#contains(Object)
     */
    public boolean hasValue(V value) {
        return entrySet().stream()
                .anyMatch(e -> e.getValue()
                        .stream()
                        .anyMatch(item -> item.equals(value))
                );
    }

    /**
     * Checks whether the given key is contained in the map.
     *
     * @param key The key to check for.
     * @return whether the key is contained.
     */
    public boolean hasKey(K key) {
        return entrySet().stream()
                .anyMatch(e -> e.getKey().equals(key));
    }

    /**
     * Checks whether the given value is contained within the given key.
     *
     * @param key   The key to check in.
     * @param value The value to check for.
     * @return whether the value is contained within the given key.
     * @see ArrayList#contains(Object)
     */
    public boolean hasValueAtKey(K key, V value) {
        return getOrDefault(key, new ArrayList<>()).contains(value);
    }

    /**
     * Removes all occuring pointers to the given value from the whole map.
     *
     * @param value The value to remove.
     * @return whether the value could be removed.
     * @see ArrayList#remove(Object)
     */
    public boolean removeValues(V value) {
        boolean val = false;
        for (Entry<K, List<V>> entry : entrySet()) {
            entry.getValue().remove(value);
            val = true;
        }
        if (val) writeToFile();
        return val;
    }

    /**
     * Removes a key from the map.
     *
     * @param key The key to remove.
     * @return The previous value of the key.
     * @see Map#remove(Object)
     */
    public List<V> removeKey(K key) {
        List<V> remove = remove(key);
        writeToFile();
        return remove;
    }

    /**
     * Removes a value from a specific key.
     *
     * @param key   The key to remove from.
     * @param value The value to remove.
     * @return whether the value could be removed.
     * @see ArrayList#remove(Object)
     */
    public boolean removeValueFromKey(K key, V value) {
        boolean remove = get(key).remove(value);
        writeToFile();
        return remove;
    }

    /**
     * Halts this instance and asynchronally reloads the contents of the file, then continues the instance.
     *
     * @see Object#wait()
     */
    public void reloadFromFile() {
        synchronized (this) {
            ioPort.read()
                    .forEach((key, value) -> {
                        K keyItem = keyFunction.toOutput(key);
                        this.putIfAbsent(keyItem, new ArrayList<>());
                        Arrays.asList(value.split(splitWith.toString()))
                                .forEach(item -> this.add(keyItem, valueFunction.toOutput(item)));
                    });
            this.notify();
        }
    }

    /**
     * Halts this instance and asynchronally writes the contents to the file, then continues the instance.
     *
     * @see Object#wait()
     */
    public void writeToFile() {
        synchronized (this) {
            HashMap<String, String> ioWriteMap = new HashMap<>();
            forEach((key, value) -> {
                String ioPortValues = value.stream()
                        .map(valueFunction::toInput)
                        .collect(CustomCollectors.toConcatenatedString(splitWith));
                ioWriteMap.put(keyFunction.toInput(key), ioPortValues);
            });
            ioPort.write(ioWriteMap);
            this.notify();
        }
    }

    /**
     * Selects a splitter Character.
     *
     * @return The selected splitter character.
     * @throws IllegalArgumentException If no "non-dead" Character could be found in {@code splitterList}.
     */
    private Character selectSplitter() {
        return Stream.of(splitterList)
                .filter(c -> !deadCharacters.contains(c))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Your input values contain too many illegal characters!" +
                        "\nYou must not use all of the following characters: " + Arrays.toString(splitterList) +
                        "\nYou may use some of them, but not all of them."));
    }

    /**
     * Tests a value or all values for illegal characters.
     *
     * @param value A nullable value to test for.
     */
    private void testForIllegalCharacters(@Nullable V value) {
        if (value == null) {
            forEach((key, lists) -> lists.forEach(this::testForIllegalCharacters));
        } else {
            if (valueFunction.toInput(value).contains(splitWith.toString())) {
                deadCharacters.add(splitWith);
                selectSplitter();
            }
        }
    }

    /**
     * Puts a list to the given key, if no list is there yet.
     *
     * @param atKey The key to put the list to.
     * @see Map#putIfAbsent(Object, Object)
     */
    void provideList(K atKey) {
        putIfAbsent(atKey, new ArrayList<>());
    }

    /**
     * Returns an iterator to iterate this instance.
     *
     * @return An iterator that iterates through all sub-items from all the lists in the map.
     */
    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            List<V> allValues = new ArrayList<V>() {{
                for (Entry<K, List<V>> entry : PropertiesMapper.super.entrySet()) {
                    this.addAll(entry.getValue());
                }
            }};
            int bigIndex = 0;

            @Override
            public boolean hasNext() {
                return allValues.size() < bigIndex;
            }

            @Override
            public V next() {
                V value = allValues.get(bigIndex);
                bigIndex++;
                return value;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        while (iterator().hasNext()) {
            action.accept(iterator().next());
        }
    }

    /**
     * Returns a spliterator for this instance.
     *
     * @return a spliterator for this instance.
     * @throws UnsupportedOperationException Always, because there is no spliterator implemented.
     */
    @Override
    public Spliterator<V> spliterator() {
        throw new UnsupportedOperationException("Spliterators not implemented yet.");
    }
}
