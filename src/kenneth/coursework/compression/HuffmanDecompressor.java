package kenneth.coursework.compression;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class HuffmanDecompressor {
    public void decompress(File inputFile, File dest, boolean overwrite) throws IOException {
        final var fileInput = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));

        final var huffmanTree = HuffmanTreeSerializer.deserializeFromStream(fileInput);
        final var isFileCreated = dest.createNewFile();

        if (!isFileCreated && !overwrite) {
            throw new FileAlreadyExistsException(dest.getAbsolutePath());
        }

        final var fileOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
        var fileSize = fileInput.readLong();

        var bytesWritten = 0L;
        var root = huffmanTree.getRoot();
        var node = root;

        for (var b = fileInput.read(); b >= 0; b = fileInput.read()) {
            // read from the start of a byte
            var mask = 0b10000000;
            var pos = 7;
            while (mask > 0) {
                var bit = (mask & b) >>> pos--;

                final var nextNode = (HuffmanTree.HuffmanNode) (bit == 1 ? node.getRightNode() : node.getLeftNode());
                final var nextByte = nextNode.getByte();

                if (nextByte != null) {
                    fileOutput.write(nextByte);
                    if (++bytesWritten == fileSize) break;
                    node = root;
                } else {
                    node = nextNode;
                }

                mask >>>= 1;
            }
        }

        fileInput.close();
        fileOutput.close();
    }
}
