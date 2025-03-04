import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NbQuickCheckTest {

  // We will capture System.out in these tests to verify output of print methods.
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  // Before each test set up a fake system out to capture output
  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(new TeeOutputStream(outContent, originalOut)));
  }

  // After each test go back to the real system out
  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    outContent.reset();
  }

  // Used for testing purposes so you can still see your print statements when debugging.
  static class TeeOutputStream extends OutputStream {
    private final OutputStream first;
    private final OutputStream second;

    public TeeOutputStream(OutputStream first, OutputStream second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public void write(int b) {
      try {
        first.write(b);
        second.write(b);
      } catch (Exception e) {
        throw new RuntimeException("Error writing to TeeOutputStream", e);
      }
    }

    @Override
    public void flush() {
      try {
        first.flush();
        second.flush();
      } catch (Exception e) {
        throw new RuntimeException("Error flushing TeeOutputStream", e);
      }
    }

    @Override
    public void close() {
      try {
        first.close();
        second.close();
      } catch (Exception e) {
        throw new RuntimeException("Error closing TeeOutputStream", e);
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Tests for the preOrder method (tree represented as a Map<Integer, List<Integer>>)
  // ---------------------------------------------------------------------------

  @Test
  public void testPreOrderSingleNode() {
    Map<Integer, List<Integer>> tree = new HashMap<>();
    // A single node tree: key 5 with an empty list of children.
    tree.put(5, new ArrayList<>());
    NbQuickCheck.preOrder(tree, 5);
    String expected = "5" + System.lineSeparator();
    assertEquals(expected, outContent.toString());
  }

  @Test
  public void testPreOrderMultipleDepth() {
    Map<Integer, List<Integer>> tree = new HashMap<>();
    // Tree structure:
    //         1
    //      /  |  \
    //     2   3   4
    //          |
    //          5
    //         / \
    //        6   7
    // All nodes must be present as keys.
    tree.put(1, Arrays.asList(2, 3, 4));
    tree.put(2, new ArrayList<>());
    tree.put(3, Arrays.asList(5));
    tree.put(4, new ArrayList<>());
    tree.put(5, Arrays.asList(6, 7));
    tree.put(6, new ArrayList<>());
    tree.put(7, new ArrayList<>());
    NbQuickCheck.preOrder(tree, 1);
    // Expected pre-order: 1, 2, 3, 5, 6, 7, 4 (each on its own line)
    String expected = String.join(System.lineSeparator(),
        Arrays.asList("1", "2", "3", "5", "6", "7", "4")) + System.lineSeparator();
    assertEquals(expected, outContent.toString());
  }

  @Test
  public void testPreOrderUnbalancedTree() {
    Map<Integer, List<Integer>> tree = new HashMap<>();
    // Unbalanced tree:
    //        10
    //        |
    //        5
    //       / \
    //      3   7
    //           \
    //            2
    tree.put(10, Arrays.asList(5));
    tree.put(5, Arrays.asList(3, 7));
    tree.put(3, new ArrayList<>());
    tree.put(7, Arrays.asList(2));
    tree.put(2, new ArrayList<>());
    NbQuickCheck.preOrder(tree, 10);
    String expected = String.join(System.lineSeparator(),
        Arrays.asList("10", "5", "3", "7", "2")) + System.lineSeparator();
    assertEquals(expected, outContent.toString());
  }

  @Test
  public void testPreOrderNotFoundRoot() {
    Map<Integer, List<Integer>> tree = new HashMap<>();
    // Tree with keys 1,2,3 but we will call with root 100 which is not present.
    tree.put(1, Arrays.asList(2, 3));
    tree.put(2, new ArrayList<>());
    tree.put(3, new ArrayList<>());
    NbQuickCheck.preOrder(tree, 100);
    String expected = "";
    assertEquals(expected, outContent.toString());
  }

  @Test
  public void testPreOrderNegativeValues() {
    Map<Integer, List<Integer>> tree = new HashMap<>();
    // Tree with negative values:
    //         -1
    //       /    \
    //     -5     -3
    //             |
    //            -10
    tree.put(-1, Arrays.asList(-5, -3));
    tree.put(-5, new ArrayList<>());
    tree.put(-3, Arrays.asList(-10));
    tree.put(-10, new ArrayList<>());
    NbQuickCheck.preOrder(tree, -1);
    String expected = String.join(System.lineSeparator(),
        Arrays.asList("-1", "-5", "-3", "-10")) + System.lineSeparator();
    assertEquals(expected, outContent.toString());
  }

  // ---------------------------------------------------------------------------
  // Tests for the minVal method (using the provided Node class)
  // ---------------------------------------------------------------------------

  @Test
  public void testMinValNull() {
    Node<Integer> root = null;
    // When root is null, should return Integer.MAX_VALUE.
    assertEquals(Integer.MAX_VALUE, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValSingleNode() {
    Node<Integer> root = new Node<>(42);
    assertEquals(42, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValMinAtRoot() {
    // Minimum is at the root.
    Node<Integer> root = new Node<>(-10);
    Node<Integer> child1 = new Node<>(0);
    Node<Integer> child2 = new Node<>(5);
    root.children.add(child1);
    root.children.add(child2);
    assertEquals(-10, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValMinAtBranch() {
    // Minimum is in a branch (not the root, not a leaf directly of the root).
    Node<Integer> root = new Node<>(10);
    Node<Integer> child1 = new Node<>(3);
    Node<Integer> child2 = new Node<>(8);
    Node<Integer> grandChild = new Node<>(-5);
    child1.children.add(grandChild);
    root.children.add(child1);
    root.children.add(child2);
    assertEquals(-5, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValMinAtLeaf() {
    // Minimum is at a leaf (deep in the tree).
    Node<Integer> root = new Node<>(5);
    Node<Integer> child1 = new Node<>(10);
    Node<Integer> child2 = new Node<>(3);
    Node<Integer> grandChild = new Node<>(-1);
    child2.children.add(grandChild);
    root.children.add(child1);
    root.children.add(child2);
    assertEquals(-1, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValTie() {
    // Two nodes with the same minimum value.
    Node<Integer> root = new Node<>(5);
    Node<Integer> child1 = new Node<>(2);
    Node<Integer> child2 = new Node<>(2);
    Node<Integer> child3 = new Node<>(3);
    root.children.add(child1);
    root.children.add(child2);
    root.children.add(child3);
    assertEquals(2, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValUnbalanced() {
    // An unbalanced tree where the minimum is deep in one branch.
    Node<Integer> root = new Node<>(10);
    Node<Integer> child = new Node<>(15);
    Node<Integer> grandChild = new Node<>(7);
    Node<Integer> greatGrandChild = new Node<>(20);
    Node<Integer> leaf = new Node<>(-5);
    root.children.add(child);
    child.children.add(grandChild);
    grandChild.children.add(greatGrandChild);
    greatGrandChild.children.add(leaf);
    assertEquals(-5, NbQuickCheck.minVal(root));
  }

  @Test
  public void testMinValMultipleChildren() {
    // A tree with a node having more than 3 children.
    Node<Integer> root = new Node<>(0);
    Node<Integer> child1 = new Node<>(10);
    Node<Integer> child2 = new Node<>(-2);
    Node<Integer> child3 = new Node<>(5);
    Node<Integer> child4 = new Node<>(7);
    Node<Integer> grandChild = new Node<>(-1);
    // Adding a branch under child3.
    child3.children.add(grandChild);
    root.children.add(child1);
    root.children.add(child2);
    root.children.add(child3);
    root.children.add(child4);
    // The minimum value among 0, 10, -2, 5, 7, and -1 is -2.
    assertEquals(-2, NbQuickCheck.minVal(root));
  }
}
