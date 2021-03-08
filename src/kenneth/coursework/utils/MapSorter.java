package kenneth.coursework.utils;

import java.util.Map;

public class MapSorter {
    public static <K, V extends Comparable<V>> Map.Entry<K, V>[] sortEntriesByValue(Map<K, V> map) {
        final Map.Entry<K, V>[] entries = map.entrySet().toArray(new Map.Entry[0]);
        sortEntriesByValue(entries, 0, entries.length);
        return entries;
    }

    private static <K, V extends Comparable<V>> void sortEntriesByValue(
            Map.Entry<K, V>[] entries,
            int start,
            int end
    ) {
        if (start >= end) return;

        final int pivotIndex = start;
        final V pivotValue = entries[pivotIndex].getValue();
        int lastSmall = start;

        for (int i = start + 1; i < end; i++) {
            final Map.Entry<K, V> entry = entries[i];
            if (entry.getValue().compareTo(pivotValue) < 0) {
                swap(entries, ++lastSmall, i);
            }
        }

        swap(entries, lastSmall, pivotIndex);

        sortEntriesByValue(entries, start, lastSmall);
        sortEntriesByValue(entries, lastSmall + 1, end);
    }

    private static <T> void swap(T[] arr, int index1, int index2) {
        final T temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
}
