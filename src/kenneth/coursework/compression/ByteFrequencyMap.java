package kenneth.coursework.compression;

import kenneth.coursework.utils.MapSorter;

import java.util.*;

class ByteFrequencyMap extends HashMap<Integer, Integer> {
    void countByte(int b) {
        merge(b, 1, Integer::sum);
    }

    HuffmanNodeList toHuffmanNodes() {
        final var sortedEntries = MapSorter.sortEntriesByValue(this);
        final var list = new HuffmanNodeList();

        for (var entry : sortedEntries) {
            list.append(new HuffmanTree.HuffmanNode(entry.getKey(), entry.getValue()));
        }

        return list;
    }

    /**
     * A simple implementation of LinkedList with O(1) add operation at arbitrary index given a node in the list.
     * The add operation of {@link LinkedList} does full list traversal to the given index. If we know the node
     * beforehand there is no need to do full traversal again, when we can directly link the node to the new node.
     */
    static class HuffmanNodeList implements Iterable<HuffmanNodeList.Item> {
        private Item first = null;
        private Item last = null;
        private int size = 0;

        /**
         * Appends the given value to the list at the end of the list
         * @param value The value to be inserted.
         */
        void append(HuffmanTree.HuffmanNode value) {
            if (first == null) {
                first = new Item(value);
                last = first;
            } else {
                last = new Item(value, last);
            }
            size++;
        }

        /**
         * @return The size of this list.
         */
        int getSize() {
            return size;
        }

        /**
         * Inserts the given value before the given {@link Item}
         * @param beforeItem The {@link Item} affected.
         * @param value      The value to be inserted
         */
        void insertBefore(Item beforeItem, HuffmanTree.HuffmanNode value) {
            beforeItem.prev = new Item(value, beforeItem.prev, beforeItem);
            size++;
        }

        /**
         * Removes the first item in the list.
         * @return The value of the removed item.
         */
        HuffmanTree.HuffmanNode removeFirst() {
            if (size == 0)
                throw new ArrayIndexOutOfBoundsException();

            final var value = first.value;
            first = first.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
            size--;

            return value;
        }

        @Override
        public Iterator<Item> iterator() {
            return new Iterator<>() {
                private Item currentItem = first;

                @Override
                public boolean hasNext() {
                    return currentItem != null;
                }

                @Override
                public Item next() {
                    final var cur = currentItem;
                    currentItem = currentItem.next;
                    return cur;
                }
            };
        }

        static class Item {
            private final HuffmanTree.HuffmanNode value;
            private Item next;
            private Item prev;

            private Item(HuffmanTree.HuffmanNode value) {
                this.value = value;
                next = null;
            }

            private Item(HuffmanTree.HuffmanNode value, Item prev) {
                this(value);
                this.prev = prev;
                prev.next = this;
            }

            private Item(HuffmanTree.HuffmanNode value, Item prev, Item next) {
                this(value, prev);
                this.next = next;
                next.prev = this;
            }

            HuffmanTree.HuffmanNode getValue() {
                return value;
            }
        }
    }
}
