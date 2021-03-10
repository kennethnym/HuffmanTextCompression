package kenneth.coursework.compression;

import kenneth.coursework.utils.MapSorter;

import java.util.*;
import java.util.stream.Collectors;

class ByteFrequencyMap extends HashMap<Integer, Integer> {
    void countByte(int b) {
        merge(b, 1, Integer::sum);
    }

    ArrayList<HuffmanTree.HuffmanNode> toHuffmanNodes() {
        return Arrays.stream(MapSorter.sortEntriesByValue(this))
                .map((entry) -> new HuffmanTree.HuffmanNode(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
