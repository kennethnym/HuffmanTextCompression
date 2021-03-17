package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class HuffmanDecompressor {
    /**
     * Decompresses the given file, and writes the decompressed content into the given destination.
     *
     * @param inputFile The compressed file to be decompressed
     * @param dest      The destination file to which the decompressed content will be written to
     * @param overwrite Whether to overwrite the content of the destination file if it exists already.
     * @throws IOException              Thrown when there is an error decompressing, or when the destination file already exists
     *                                  and the overwrite option is not enabled.
     * @throws IncorrectFormatException When the compressed file is invalid.
     */
    public void decompress(File inputFile, File dest, boolean overwrite) throws IOException, IncorrectFormatException {
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
        var isUnknownByte = false;

        for (var b = fileInput.read(); b >= 0; b = fileInput.read()) {
            if (b == HuffmanCompressor.SpecialByte.UNKNOWN_BYTE_ENDS.b) {
                isUnknownByte = false;
                continue;
            }

            if (isUnknownByte) {
                fileOutput.write(b);
                continue;
            }

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
                    if (b == HuffmanCompressor.SpecialByte.UNKNOWN_BYTE_STARTS.b) {
                        isUnknownByte = true;
                        continue;
                    }
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
