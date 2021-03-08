# Huffman compression

This project is a Java implementation of the Huffman coding algorithm.

## Implementation

Huffman coding algorithms compresses texts by assigning fewer bits to store characters
that appear more often in the source text.
It generates a binary tree where more frequent characters are higher up in the tree.

In this implementation, instead of counting frequencies of characters, it counts the frequencies
of bytes in a given byte array. Decompression works by traversing the resulting tree and reconstructing
the byte array back.

The tree that is generated is stored at the start of the compressed file before the actual compressed content, 
by serializing the tree into a compact format that takes up as little extra space as possible.