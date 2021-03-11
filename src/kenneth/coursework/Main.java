package kenneth.coursework;

import kenneth.coursework.ui.MainApplication;

/**
 * Daily log:
 * <p>
 * - mar 3:
 * - started researching what text compression algorithm to used and settled on burrow-wheeler transform + huffman
 * - implementing HuffmanTree
 * - mar 4:
 * - continue implementing HuffmanTree
 * - mar 5:
 * - change HuffmanTree so it considers frequency of bytes instead of frequency of character to avoid encoding issues.
 * - finished implementing compression
 * - started implementing tree serialization and deserialization
 * - mar 6
 * - finished implementing tree serialization and deserialization
 * - problem with decompressing file - debugging
 * - mar 7
 * - managed to fix compression/decompression issue
 * - started working on burrows-wheeler transform
 * - mar 8
 * - made some optimizations
 * - redesign huffman tree serialization
 * - mar 9
 * - SIGNIFICANTLY IMPROVED PERFORMANCE (1000 FOLD DIFFERENCE!) by using buffered streams
 * - parallelize compression (https://nickolasfisher.com/blog/Improving-Java-IO-Performance-Appropriately-Using-Random-Access-Over-Streams)
 * - mar 10
 * - started implementing ui
 */

public class Main {
    public static void main(String[] args) {
//        final var inputFile = "/Users/kenneth/Desktop/portuguese.txt";
//        final var outputFile = "/Users/kenneth/Desktop/compressed_portuguese";
//        final var decompressedFile = "/Users/kenneth/Desktop/decompressed_portuguese";
//        final var compressor = new HuffmanCompressor();
//        final var decompressor = new HuffmanDecompressor();
//
//        try {
//            final var start = System.currentTimeMillis();
//            compressor.compress(inputFile, outputFile, true);
//            decompressor.decompress(outputFile, decompressedFile);
//            final var end = System.currentTimeMillis();
//            System.out.println("took " + (end - start) + "ms");
//        } catch (IOException | IncorrectFormatException ex) {
//            System.out.println(ex.getLocalizedMessage());
//            ex.printStackTrace();
//        }
        MainApplication.main(args);
    }
}
