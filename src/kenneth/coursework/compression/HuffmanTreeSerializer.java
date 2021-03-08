package kenneth.coursework.compression;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

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

    public static void serializeToStream(HuffmanTree tree, DataOutputStream stream) throws IOException {
        final var ref = new Object() {
            int prevLevel = -1;
            // Number of shorts required to store the given tree.
            int shortCount = 0;
            // The serialized tree in shorts.
            // short is used instead of int to save space, since bytes will never exceed 8 bits.
            final LinkedList<Short> bytes = new LinkedList<>();
        };

        tree.traverse((node, pos, level) -> {
            if (level <= ref.prevLevel) {
                for (var i = 0; i <= ref.prevLevel - level; i++) {
                    ref.bytes.addLast(Code.UP.val);
                    ref.shortCount++;
                }
            }

            switch (pos) {
                case LEFT:
                    ref.bytes.addLast(Code.LEFT.val);
                    ref.shortCount++;
                    break;
                case RIGHT:
                    ref.bytes.addLast(Code.RIGHT.val);
                    ref.shortCount++;
                    break;
                case ROOT:
                    ref.bytes.addLast(Code.ROOT.val);
                    ref.shortCount++;
                    break;
                default:
                    break;
            }

            final var b = node.getByte();
            if (b != null) {
                ref.bytes.addLast((short) (int) b);
                ref.shortCount++;
            }

            ref.prevLevel = level;
        });

        stream.writeInt(ref.shortCount);

        while (ref.shortCount > 0) {
            stream.writeShort(ref.bytes.removeFirst());
            ref.shortCount--;
        }
    }

    public static HuffmanTree deserializeFromStream(DataInputStream stream) throws IOException {
        var size = stream.readInt();

        HuffmanTree.HuffmanNode root = null;
        HuffmanTree.HuffmanNode currentNode = null;

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
            } else {
                currentNode.setByte(s);
            }
        }

        return new HuffmanTree(root);
    }
}
