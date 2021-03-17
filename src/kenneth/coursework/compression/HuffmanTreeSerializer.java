package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Serialize/deserialize huffman trees.
 */
public class HuffmanTreeSerializer {
    public enum Code {
        UP((short) -2),
        LEFT((short) -3),
        RIGHT((short) -4),
        ROOT((short) -5);

        private final short val;

        Code(short val) {
            this.val = val;
        }

        public short getCode() {
            return val;
        }
    }

    /**
     * Obtains the serialized {@link HuffmanTree} available in the given stream.
     *
     * @param stream The stream that contains the {@link HuffmanTree}
     * @return The deserialized {@link HuffmanTree}
     * @throws IOException              Thrown when there is an error reading the stream
     * @throws IncorrectFormatException Thrown when the tree is not serialized in a correct format,
     *                                  or when the tree is not available.
     */
    public static HuffmanTree deserializeFromStream(DataInputStream stream) throws IOException, IncorrectFormatException {
        var size = stream.readInt();

        HuffmanTree.HuffmanNode root = null;
        HuffmanTree.HuffmanNode currentNode = null;

        try {
            while (size > 0) {
                short s = stream.readShort();
                size--;

                if (s == Code.UP.val) {
                    currentNode = (HuffmanTree.HuffmanNode) currentNode.getParent();
                } else if (s == Code.LEFT.val) {
                    final var newNode = new HuffmanTree.HuffmanNode();
                    currentNode.setLeftNode(newNode);
                    currentNode = newNode;
                } else if (s == Code.RIGHT.val) {
                    final var newNode = new HuffmanTree.HuffmanNode();
                    currentNode.setRightNode(newNode);
                    currentNode = newNode;
                } else if (s == Code.ROOT.val) {
                    root = new HuffmanTree.HuffmanNode();
                    currentNode = root;
                } else if (s >= 0) {
                    currentNode.setByte(s);
                } else {
                    throw new IncorrectFormatException();
                }
            }
        } catch (NullPointerException ex) {
            throw new IncorrectFormatException();
        }

        return new HuffmanTree(root);
    }
}
