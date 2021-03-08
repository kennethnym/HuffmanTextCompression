package kenneth.coursework.compression;

import kenneth.coursework.utils.BinaryTree;

import java.io.*;
import java.util.HashMap;

/**
 * Creates a bit code map for a {@link HuffmanTree} that maps characters to their corresponding huffman code.
 */
class BitCodeMap extends HashMap<Integer, int[]> {
    void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(size());
        for (var entry : entrySet()) {
            stream.writeInt(entry.getValue()[1]);
            stream.write(entry.getKey());
        }
    }

    void generateBitCode(HuffmanTree tree) {
        final var ref = new Object() {
            int code = 0;
            int prevLevel = 0;
        };

        tree.traverse((node, position, level) -> {
            if (position == BinaryTree.Position.ROOT) return;
            if (level <= ref.prevLevel) {
                ref.code >>>= ref.prevLevel - level + 1;
            }

            ref.code <<= 1;
            ref.code |= position.val;

            final var b = node.getByte();

            if (b != null) {
                // we have arrived to an end node
                final var arr = new int[2];
                arr[0] = level;
                arr[1] = ref.code;
                put(b, arr);
            }

            ref.prevLevel = level;
        });
    }
}
