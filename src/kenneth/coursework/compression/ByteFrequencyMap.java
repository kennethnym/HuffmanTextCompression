package kenneth.coursework.compression;

import kenneth.coursework.utils.MapSorter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Keeps track of frequencies of bytes in a byte stream/array.
 */
class ByteFrequencyMap extends HashMap<Integer, Integer> {
    void countByte(int b) {
        merge(b, 1, Integer::sum);
    }

    /**
     * Converts the frequencies into an {@link ArrayList} of {@link HuffmanTree.HuffmanNode}.
     *
     * @return The list of {@link HuffmanTree.HuffmanNode} sorted by the frequencies in an ascending order.
     */
    ArrayList<HuffmanTree.HuffmanNode> toHuffmanNodes() {
        return Arrays.stream(MapSorter.sortEntriesByValue(this))
                .map((entry) -> new HuffmanTree.HuffmanNode(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
