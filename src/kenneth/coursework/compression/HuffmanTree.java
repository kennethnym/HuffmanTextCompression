package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;
import kenneth.coursework.utils.BinaryNode;
import kenneth.coursework.utils.BinaryTree;
import kenneth.coursework.utils.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Represents a huffman encoding tree.
 */
public class HuffmanTree extends BinaryTree<HuffmanTree.HuffmanNode> implements Serializable {
    private InputStream inputStream;
    private ByteFrequencyMap frequencyMap = new ByteFrequencyMap();

    public HuffmanTree(HuffmanNode root) {
        super(root);
    }

    public HuffmanTree(InputStream input) {
        super();
        inputStream = input;
    }

    public void build() throws IOException {
        constructFrequencyMap();

        final List<HuffmanNode> sortedFrequencyNodes = frequencyMap.toHuffmanNodes();

        int nodeCount = sortedFrequencyNodes.size();

        if (nodeCount == 1) {
            final var node = sortedFrequencyNodes.get(0);
            root = new HuffmanNode(node, null, node.frequency);
            return;
        }

        while (nodeCount > 0) {
            final HuffmanNode smallest = sortedFrequencyNodes.remove(0);
            final HuffmanNode secondSmallest = sortedFrequencyNodes.remove(0);
            final int totalFrequency = smallest.frequency + secondSmallest.frequency;

            nodeCount = sortedFrequencyNodes.size();

            if (nodeCount > 0) {
                final HuffmanNode parentNode = new HuffmanNode(smallest, secondSmallest, totalFrequency);

                for (int i = 0; i <= nodeCount; i++) {
                    if (i == nodeCount) {
                        sortedFrequencyNodes.add(parentNode);
                    } else if (sortedFrequencyNodes.get(i).frequency >= totalFrequency) {
                        sortedFrequencyNodes.add(Math.max(i - 1, 0), parentNode);
                        break;
                    }
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
    public String serialize() {
        final var string = new StringBuilder();

        final var ref = new Object() {
            int prevLevel = -1;
        };

        traverse((node, pos, level) -> {
            if (level == ref.prevLevel) {
                string.append("-");
            } else if (level < ref.prevLevel) {
                string.append("^".repeat(ref.prevLevel - level + 1));
            }

            switch (pos) {
                case LEFT:
                    string.append("l");
                    break;
                case RIGHT:
                    string.append("r");
                    break;
                case ROOT:
                    string.append("R");
                    break;
                default:
                    break;
            }
            final var b = node.b;
            if (b != null) string.append(b);

            ref.prevLevel = level;
        });

        return string.toString();
    }

    public static BinaryTree<HuffmanNode> deserialize(String string) throws IncorrectFormatException {
        final var len = string.length();

        if (!string.startsWith("R"))
            throw new IncorrectFormatException();

        if (len == 1)
            return new HuffmanTree(new HuffmanNode());

        HuffmanNode root = null;
        HuffmanNode currentNode = null;
        var start = 0;

        while (start < len) {
            var end = start;
            if (len == end + 1) break;

            char c;

            do {
                if (++end == len) break;
                c = string.charAt(end);
            } while (c != 'l' && c != 'r' && c != '-' && c != '^');

            final var segment = string.substring(start, end);

            if (segment.equals("^") || segment.equals("-")) {
                start = end;
                currentNode = (HuffmanNode) currentNode.getParent();
                continue;
            }

            final var isEndNode = segment.length() > 1;
            Integer b = null;

            if (isEndNode) {
                b = Integer.parseInt(segment.substring(1));
            }

            final var newNode =
                    isEndNode ? new HuffmanNode(b, 0)
                            : new HuffmanNode(null, null, 0);

            switch (segment.charAt(0)) {
                case 'l':
                    currentNode.setLeftNode(newNode);
                    break;

                case 'r':
                    currentNode.setRightNode(newNode);
                    break;

                case 'R':
                    root = newNode;
                    break;
            }

            start = end;
            currentNode = newNode;
        }

        return new HuffmanTree(root);
    }

    /**
     * Defines a value stored in a Huffman coding binary tree.
     */
    static class HuffmanNode extends BinaryNode {
        /**
         * A character in the original text with the associated frequency.
         * null of this node has children.
         */
        private final Integer b;

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

    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder();

        traverse((node, position, level) -> {
            string
                    .append("    ".repeat(level))
                    .append(node)
                    .append("\n");
        });

        return string.toString();
    }
}
