package kenneth.coursework.utils;

/**
 * Describes a node in a binary tree.
 */
public class BinaryNode {
    protected BinaryNode left;
    protected BinaryNode right;
    protected BinaryNode parent;

    /**
     * Creates a standalone binary node with no parent and children.
     */
    public BinaryNode() {
        left = null;
        right = null;
        parent = null;
    }

    /**
     * Creates a new node to be stored in a binary tree.
     *
     * @param leftNode  The left child node. Can be null. This node will be the parent of that node.
     * @param rightNode The right child node. Can be null. This node will be the parent of that node.
     */
    public BinaryNode(BinaryNode leftNode, BinaryNode rightNode) {
        left = leftNode;
        right = rightNode;
        parent = null;

        if (leftNode != null) {
            left.parent = this;
        }

        if (rightNode != null) {
            right.parent = this;
        }
    }

    /**
     * @return The left child node.
     */
    public BinaryNode getLeftNode() {
        return left;
    }

    /**
     * @return The right child node.
     */
    public BinaryNode getRightNode() {
        return right;
    }

    /**
     * @return The parent of node.
     */
    public BinaryNode getParent() {
        return parent;
    }

    /**
     * Replaces the left child node with the given node.
     *
     * @param node The new node
     */
    public void setLeftNode(BinaryNode node) {
        left = node;
        left.parent = this;
    }

    /**
     * Replaces the right child node with the given node.
     *
     * @param node The new node
     */
    public void setRightNode(BinaryNode node) {
        right = node;
        right.parent = this;
    }
}
