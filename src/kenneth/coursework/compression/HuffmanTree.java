package kenneth.coursework.compression;

import kenneth.coursework.utils.BinaryNode;
import kenneth.coursework.utils.BinaryTree;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a huffman encoding tree.
 */
public class HuffmanTree extends BinaryTree<HuffmanTree.HuffmanNode> {
    private InputStream inputStream;
    private final ByteFrequencyMap frequencyMap = new ByteFrequencyMap();

    public HuffmanTree(HuffmanNode root) {
        super(root);
    }

    public HuffmanTree(InputStream input) {
        super();
        inputStream = input;
    }

    public void build() throws IOException {
        constructFrequencyMap();

        final var sortedFrequencyNodes = frequencyMap.toHuffmanNodes();
        var nodeCount = sortedFrequencyNodes.getSize();

        if (nodeCount == 1) {
            final var node = sortedFrequencyNodes.removeFirst();
            root = new HuffmanNode(node, null, node.frequency);
            return;
        }

        while (nodeCount > 0) {
            final var smallest = sortedFrequencyNodes.removeFirst();
            final var secondSmallest = sortedFrequencyNodes.removeFirst();
            final var totalFrequency = smallest.frequency + secondSmallest.frequency;

            nodeCount = sortedFrequencyNodes.getSize();

            if (nodeCount > 0) {
                final var parentNode = new HuffmanNode(smallest, secondSmallest, totalFrequency);

                var i = 0;
                for (var item : sortedFrequencyNodes) {
                    if (item.getValue().frequency >= totalFrequency) {
                        sortedFrequencyNodes.insertBefore(item, parentNode);
                        break;
                    }
                    i++;
                }

                if (i == nodeCount) {
                    sortedFrequencyNodes.append(parentNode);
                }
            } else {
                root = new HuffmanNode(smallest, secondSmallest, totalFrequency);
            }
        }
    }

    private void constructFrequencyMap() throws IOException {
        for (var b = inputStream.read(); b >= 0; b = inputStream.read()) {
            frequencyMap.countByte(b);
        }
    }

    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder();

        traverse((node, position, level) -> string
                .append("    ".repeat(level))
                .append(node)
                .append("\n"));

        return string.toString();
    }

    /**
     * Defines a value stored in a Huffman coding binary tree.
     */
    static class HuffmanNode extends BinaryNode {
        /**
         * A character in the original text with the associated frequency.
         * null of this node has children.
         */
        private Integer b;

        /**
         * If this node stores a character, frequency will be the frequency of the character.
         * Otherwise, it is the total frequency of the children of this node.
         */
        private final int frequency;

        public HuffmanNode() {
            this(null, null, 0);
        }

        public HuffmanNode(int b, int frequency) {
            super();
            this.b = b;
            this.frequency = frequency;
        }

        public HuffmanNode(HuffmanNode leftNode, HuffmanNode rightNode, int frequency) {
            super(leftNode, rightNode);
            this.b = null;
            this.frequency = frequency;
        }

        public Integer getByte() {
            return b;
        }

        public void setByte(int b) {
            this.b = b;
        }

        public boolean hasChildren() {
            return left != null || right != null;
        }

        @Override
        public String toString() {
            return "HuffmanNode{" +
                    "b=" + b +
                    ", frequency=" + frequency +
                    '}';
        }
    }
}
