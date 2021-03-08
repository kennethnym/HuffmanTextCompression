package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;

public class HuffmanCompressor {
    private static final String FILE_EXTENSION = ".huff";

    public void compress(String inputFile, String dest, boolean overwrite) throws IOException, IncorrectFormatException {
        HuffmanTree tree = new HuffmanTree(new FileInputStream(inputFile));

        tree.build();

        final var file = new File(dest);
        final var isFileCreated = file.createNewFile();

        if (!isFileCreated && !overwrite) {
            throw new FileAlreadyExistsException(dest + FILE_EXTENSION);
        }

        final var fileInput = new FileInputStream(inputFile);
        final var fileOutput = new DataOutputStream(new FileOutputStream(file, false));

        HuffmanTreeSerializer.serializeToStream(tree, fileOutput);

//        final var serializedTree = tree.serialize().getBytes(StandardCharsets.UTF_8);
//
//        fileOutput.writeInt(serializedTree.length);
//        fileOutput.write(serializedTree);
        fileOutput.writeLong(fileInput.getChannel().size());

        final var bitCodeMap = new BitCodeMap();
        bitCodeMap.generateBitCode(tree);

        var partition = 0;
        var pos = 8;

        for (var b = fileInput.read(); b >= 0; b = fileInput.read()) {
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
            // (by creating a leading 1 shifted to the right by the same amount).
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
                partition |= ((bitCode & mask) >> maskPos--) << (pos-- -1);
                mask >>>= 1;
            }
        }

        if (pos < 8) {
            // there are some remaining bits
            fileOutput.write(partition);
        }

        fileInput.close();
        fileOutput.close();
    }
}
