package de.kaleidox.util.serializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SelectedPropertiesMapper<K, V> extends PropertiesMapper<K, V> implements Iterable<V> {
    final K key;

    public SelectedPropertiesMapper(
            File file,
            K key,
            DoubleFunction<String, K> keyFunction,
            DoubleFunction<String, V> valueFunction) {
        this(IOPort.mapPort(file), key, null, keyFunction, valueFunction);
    }

    public SelectedPropertiesMapper(
            IOPort<ConcurrentHashMap<String, String>, Map<String, String>> ioPort,
            K key,
            @Nullable Character splitWith,
            DoubleFunction<String, K> keyFunction,
            DoubleFunction<String, V> valueFunction) {
        super(ioPort, splitWith, keyFunction, valueFunction);

        this.key = key;
    }

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
     * Gets the selected Key.
     *
     * @return The key of the instance.
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets all values of this key.
     *
     * @return A list of all values.
     */
    public List<V> get() {
        return get(key);
    }

    /**
     * Gets the item in the list, if available.
     *
     * @param index The index of the item to get.
     * @return the item at the given index.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#get(int)
     */
    public V get(int index) {
        return get(key, index);
    }

    /**
     * Gets an item in the list, if available; otherwise returns an absence value.
     *
     * @param index         The index of the item to get.
     * @param valueIfAbsent The value to return if the element is not available.
     * @return the item at the given index, otherwise the absence value.
     */
    public V getOrDefault(int index, V valueIfAbsent) {
        return getOrDefault(key, index, valueIfAbsent);
    }

    /**
     * Adds an item to the list.
     * When converted to a String,
     *
     * @param value The value to add.
     *              Gets appended at the end of the list.
     * @return whether or not the value could be added.
     * @see ArrayList#add(Object)
     */
    public boolean add(V value) {
        return add(key, value);
    }

    /**
     * Adds an item to the list if the value is not in the list yet.
     * Therefore this method does not allow duplications.
     *
     * @param value The value to add.
     * @return whether the value could be added. This is false if the value was already in the list.
     * @see ArrayList#add(Object)
     */
    public boolean addIfValueAbsent(V value) {
        return addIfValueAbsent(key, value);
    }

    /**
     * Adds a value if a predicate for this Instance tests true.
     *
     * @param value        The value to add.
     * @param mapPredicate A predicate to check this Instance.
     * @return whether the item could be added. This is false, if the predicate tested false.
     */
    public boolean addIfPredicate(V value, Predicate<PropertiesMapper<K, V>> mapPredicate) {
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
     * @param index The index to set at.
     * @param value The value to set.
     * @return the element that was previously in this position.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public V set(int index, V value) {
        return set(key, index, value);
    }

    /**
     * Sets an item at a position in the list if the value is not in the list yet.
     *
     * @param index The index to set at.
     * @param value The value to set.
     * @return whether the value could be set. This is false if the value was already in the list.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setIfValueAbsent(int index, V value) {
        return setIfValueAbsent(key, index, value);
    }

    /**
     * Sets an item at a position in  the list if a given Predicate tests this Instance true.
     *
     * @param index        The index to set at.
     * @param value        The value to set.
     * @param mapPredicate A predicate to test this Instance.
     * @return whether the value could be set. This is false if the predicate tested false.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setIfPredicate(int index, V value, Predicate<PropertiesMapper<K, V>> mapPredicate) {
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
     * @param index The index to set at.
     * @param value The value to set.
     * @return whether the value was set.
     * @throws IndexOutOfBoundsException If the index out of the Lists range.
     * @see ArrayList#set(int, Object)
     */
    public boolean setToCoordinates(int index, V value) {
        return setToCoordinates(key, index, value);
    }

    /**
     * Checks whether the given value is contained within the given key.
     *
     * @param value The value to check for.
     * @return whether the value is contained within the given key.
     * @see ArrayList#contains(Object)
     */
    @Override
    public boolean hasValue(V value) {
        return get(key).contains(value);
    }
}
