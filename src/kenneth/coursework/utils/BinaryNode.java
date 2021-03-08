package kenneth.coursework.utils;

/**
 * Describes a node in a binary tree.
 */
public class BinaryNode {
    protected BinaryNode left;
    protected BinaryNode right;
    protected BinaryNode parent;

    public BinaryNode() {
        left = null;
        right = null;
        parent = null;
    }

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

    public BinaryNode getLeftNode() {
        return left;
    }

    public BinaryNode getRightNode() {
        return right;
    }

    public BinaryNode getParent() {
        return parent;
    }

    public void setLeftNode(BinaryNode node) {
        left = node;
        left.parent = this;
    }

    public void setRightNode(BinaryNode node) {
        right = node;
        right.parent = this;
    }

    public boolean hasRightNode() {
        return right != null;
    }

    public boolean hasChildren() {
        return left != null || right != null;
    }
}
