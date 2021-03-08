package kenneth.coursework.utils;

import kenneth.coursework.exceptions.IncorrectFormatException;

/**
 * Describes a binary tree.
 */
abstract public class BinaryTree<TNode extends BinaryNode> {
    public interface Visitor<TNode extends BinaryNode> {
        void visit(TNode node, Position position, int level);
    }

    public enum Position {
        ROOT(-1),
        LEFT(0),
        RIGHT(1);

        public int val;

        Position(int val) {
            this.val = val;
        }
    }

    protected TNode root;

    public BinaryTree() {
        root = null;
    }

    public BinaryTree(TNode root) {
        this.root = root;
    }

    public TNode getRoot() {
        return root;
    }

    /**
     * Do a pre-order traversal of the tree. Whenever a new node is visited, the given callback is called.
     */
    public void traverse(Visitor<TNode> visitor) {
        traverse(root, visitor, Position.ROOT, 0);
    }

    private void traverse(TNode currentNode, Visitor<TNode> visitor, Position position, int level) {
        if (currentNode == null) return;

        visitor.visit(currentNode, position, level);

        traverse((TNode) currentNode.getLeftNode(), visitor, Position.LEFT, level + 1);
        traverse((TNode) currentNode.getRightNode(), visitor, Position.RIGHT, level + 1);
    }
}
