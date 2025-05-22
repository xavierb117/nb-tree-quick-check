import java.util.List;
import java.util.Map;

public class NbQuickCheck {

  /**
   * Performs a pre-order traversal of the tree, printing each node on a separate line.
   * Does nothing if the root is not present in the tree.
   *
   * @param tree the tree represented as a map of parent nodes to child lists
   * @param root the root node to start traversal from
   */
  public static void preOrder(Map<Integer, List<Integer>> tree, int root) {
    if(!tree.containsKey(root) || tree == null) {
      return;
    }

    System.out.println(root);
    List<Integer> lists = tree.get(root);
    if (tree.get(root) != null) {
      for (int children : lists) {
        preOrder(tree, children);
      }
    }
  }

  /**
   * Returns the minimum value in the tree.
   * Returns Integer.MAX_VALUE if the root is null.
   *
   * @param root the root node of the tree
   * @return the minimum value in the tree or Integer.MAX_VALUE if root is null
   */
  public static int minVal(Node<Integer> root) {
    int min = Integer.MAX_VALUE;
    if (root == null) return min;
    int currentVal = root.value;

    if (root.children != null) {
      for (Node<Integer> child : root.children) {
        currentVal = Math.min(minVal(child), currentVal);
      }
    }

    return Math.min(min, currentVal);
  }
  
}
