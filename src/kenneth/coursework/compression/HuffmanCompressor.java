package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;
import kenneth.coursework.utils.BinaryTree;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.LinkedList;

public class HuffmanCompressor {
    static final String FILE_EXTENSION = ".huff";

    public void compress(String inputFilePath, String dest, boolean overwrite) throws IOException, IncorrectFormatException {
        final var inputFile = new File(inputFilePath);

        final var tree = new HuffmanTree(new BufferedInputStream(new FileInputStream(inputFile)));
        tree.build();

        final var file = new File(dest + FILE_EXTENSION);
        final var isFileCreated = file.createNewFile();

        if (!isFileCreated && !overwrite) {
            throw new FileAlreadyExistsException(dest + FILE_EXTENSION);
        }

        final var inputFileStream = new BufferedInputStream(new FileInputStream(inputFile));
        final var fileOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, false)));

        final var treeVisitor = new HuffmanTreeVisitor();

        tree.traverse(treeVisitor);

        writeSerializedTree(treeVisitor.getSerializedTreeBytes(), treeVisitor.getSerializedTreeSize(), fileOutput);

        final var bitCodeMap = treeVisitor.getBitCodeMap();

        fileOutput.writeLong(inputFile.length());

        var partition = 0;
        var pos = 8;

        for (var b = inputFileStream.read(); b >= 0; b = inputFileStream.read()) {
            final var bits = bitCodeMap.get(b);
            final var bitLength = bits[0];
            final var bitCode = bits[1];

            // write bits into temporary bytes before writing into stream
            // each bit code in bitCodeMap is stored in a tuple, where the first item
            // specifies the length of the bits, and the second item specifies the actual bit code.
            //
            // this allows us to obtain leading zero bits in the code which would otherwise not be accessible
            // if we used a combination of bit mask and while loop to obtain bit length manually, since we
            // can't distinguish between start of bit code or 0 bit with actual meaning (left node in the huffman tree).
            //
            // for example, consider 00100. without knowing the length, it is impossible to tell where the start
            // of the bit code is. it can be 0100, or 100, or 00100.
            //
            // by knowing the length, we can create a bit mask with exactly that amount of bits
            // (by creating a leading 1 shifted to the left by the same amount).
            //
            // we write the byte (partition) left to right - so pos should start at 8 (8 bits = 1 byte).
            // when we reaches 0, we know the byte is full, so we write the byte to the stream
            // and reset the byte (partition).

            var maskPos = bitLength - 1;
            var mask = 1 << maskPos;
            while (mask > 0) {
                if (pos == 0) {
                    fileOutput.write(partition);
                    pos = 8;
                    partition = 0;
                }
                partition |= ((bitCode & mask) >> maskPos--) << (pos-- - 1);
                mask >>>= 1;
            }
        }

        if (pos < 8) {
            // there are some remaining bits
            fileOutput.write(partition);
        }

        inputFileStream.close();
        fileOutput.close();
    }

    private void writeSerializedTree(LinkedList<Short> serializedTree, int size, DataOutputStream stream) throws IOException {
        stream.writeInt(size);
        while (size > 0) {
            stream.writeShort(serializedTree.removeFirst());
            size--;
        }
    }

    private static class HuffmanTreeVisitor implements BinaryTree.Visitor<HuffmanTree.HuffmanNode> {
        // maps bytes to their corresponding huffman code
        final HashMap<Integer, int[]> bitCodeMap = new HashMap<>();
        // resultant huffman code of the path of the traversal
        int code = 0;
        // level of last visited node
        int prevLevel = -1;
        // Number of shorts required to store the given tree.
        int shortCount = 0;
        // The serialized tree in shorts.
        // short is used instead of int to save space, since bytes will never exceed 8 bits.
        final LinkedList<Short> serializedTree = new LinkedList<>();

        @Override
        public void visit(HuffmanTree.HuffmanNode node, BinaryTree.Position position, int level) {
            if (level <= prevLevel) {
                final var levelDiff = prevLevel - level + 1;
                code >>>= levelDiff;
                for (var i = 0; i < levelDiff; i++) {
                    serializedTree.addLast(HuffmanTreeSerializer.Code.UP.getCode());
                    shortCount++;
                }
            }

            updateHuffmanCode(node, position, level);
            serializeNode(node, position);

            prevLevel = level;
        }

        private void updateHuffmanCode(HuffmanTree.HuffmanNode node, BinaryTree.Position position, int level) {
            code <<= 1;
            code |= position.getHuffmanBit();

            final var b = node.getByte();

            if (b != null) {
                // we have arrived to an end node
                final var arr = new int[2];
                arr[0] = level;
                arr[1] = code;
                bitCodeMap.put(b, arr);
            }
        }

        private void serializeNode(HuffmanTree.HuffmanNode node, BinaryTree.Position position) {
            switch (position) {
                case LEFT:
                    serializedTree.addLast(HuffmanTreeSerializer.Code.LEFT.getCode());
                    shortCount++;
                    break;
                case RIGHT:
                    serializedTree.addLast(HuffmanTreeSerializer.Code.RIGHT.getCode());
                    shortCount++;
                    break;
                case ROOT:
                    serializedTree.addLast(HuffmanTreeSerializer.Code.ROOT.getCode());
                    shortCount++;
                    break;
                default:
                    break;
            }

            final var b = node.getByte();
            if (b != null) {
                serializedTree.addLast((short) (int) b);
                shortCount++;
            }
        }

        /**
         * @return A map of bytes to their corresponding huffman bit code. Empty if this visitor hasn't visited
         * any huffman tree.
         */
        private HashMap<Integer, int[]> getBitCodeMap() {
            return bitCodeMap;
        }

        /**
         * @return The length of the serialized version of the huffman tree. 0 if this visitor hasn't visited any huffman tree.
         */
        private int getSerializedTreeSize() {
            return shortCount;
        }

        /**
         * @return The serialized version of the huffman tree. Empty if this visitor hasn't visited any huffman tree.
         */
        private LinkedList<Short> getSerializedTreeBytes() {
            return serializedTree;
        }
    }
}
