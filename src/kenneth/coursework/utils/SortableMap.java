package kenneth.coursework.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A map that can be sorted by either value or key.
 * <p>
 * Internally, the keys and values are stored in an {@link ArrayList}. Therefore the performance of various operations
 * of {@link SortableMap} should be similar to {@link ArrayList}.
 */
public class SortableMap<K, V> implements Map<K, V> {
    private int size = 0;
    private Set<Integer> keys = new HashSet<>();
    private List<V> table = new ArrayList<>(100);

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return table.contains(value);
    }

    @Override
    public V get(Object key) {
        return table.get(key.hashCode());
    }

    @Override
    public V put(K key, V value) {
        final int hash = key.hashCode();

        size++;
        keys.add(hash);

        if (hash >= table.size() || table.get(hash) == null) {
            table.add(hash, value);
            return value;
        }

        table.remove(hash);
        table.add(hash, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        final int hash = key.hashCode();
        final V removedValue = table.get(hash);
        table.remove(key);
        return removedValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((key, val) -> table.add(key.hashCode(), val));
    }

    @Override
    public void clear() {
        table.clear();
        keys.clear();
    }

    @Override
    public Set keySet() {
        return keys;
    }

    @Override
    public Collection<V> values() {
        return keys.stream()
                .map((key) -> table.get(key)).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        keys.forEach((key) -> builder.append(table.get(key)));
        return builder.toString();
    }
}
