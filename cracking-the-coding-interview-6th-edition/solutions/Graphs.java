import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Graphs {
  // helper classes and functions
  enum State {
    UNVISITED, VISITED
  }

  public static class TreeNode {
    public int value;
    public TreeNode left = null;
    public TreeNode right = null;

    public TreeNode(int value) {
      this.value = value;
    }

  }

  public static class Node {
    public int value;
    public State state = State.UNVISITED;

    public Node(int value) {
      this.value = value;
    }

  }

  public static class Graph<T> {
    private Map<T, ArrayList<T>> adjList = new LinkedHashMap<>();

    public void addEdge(T u, T v) {
      // Add edge to the adjacency list
      adjList.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
      // adjList.computeIfAbsent(v, k -> new ArrayList<>()).add(u); // For undirected
      // graph

    }

    public void dfs(T start) {
      // Depth-First Search implementation
    }

    public void bfs(T start) {
      // Breadth-First Search implementation
    }
    // get nodes

  }

  public static void inOrderTraversal(TreeNode node) {
    if (node == null) {
      return;
    }
    inOrderTraversal(node.left);
    System.out.print(node.value + " ");
    inOrderTraversal(node.right);
  }

  // 4.1 Route Between Nodes: Given a directed graph, design an algorithm to find
  // out whether there is a route between two nodes.

  public static boolean hasRoute(Graph<Integer> graph, int start, int end) {
    // bfs implementation (not use graph.bfs())
    final LinkedList<Integer> queue = new LinkedList<>();
    final HashSet<Integer> visited = new HashSet<>();
    queue.add(start);
    while (!queue.isEmpty()) {
      final Integer cur = queue.poll();
      if (cur == end) {
        return true;
      }
      visited.add(cur);
      // get neighbors of node
      if (graph.adjList.get(cur) == null) {
        continue;
      }
      for (Integer neighbor : graph.adjList.get(cur)) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
        }
      }
    }
    return false;
  }

  // 4.2 Minimal Tree: Given a sorted (increasing order) array with unique
  // integers, write an algorithm to create a binary search tree with minimal
  // height.
  public static TreeNode minimalTreeBST(int[] sortedArray) {
    if (sortedArray == null || sortedArray.length == 0) {
      return null;
    }
    return minimalTreeBSTHelper(sortedArray, 0, sortedArray.length - 1);
  }

  private static TreeNode minimalTreeBSTHelper(int[] sortedArray, int start, int end) {
    if (start > end) {
      return null;
    }
    int mid = start + (end - start) / 2;
    TreeNode node = new TreeNode(sortedArray[mid]);
    node.left = minimalTreeBSTHelper(sortedArray, start, mid - 1);
    node.right = minimalTreeBSTHelper(sortedArray, mid + 1, end);
    return node;
  }

  // 4.3 List of Depths: Given a binary tree, design an algorithm which creates a
  // linked list of all the nodes at each depth.
  public static ArrayList<LinkedList<TreeNode>> listOfDepths(TreeNode root) {
    ArrayList<LinkedList<TreeNode>> lists = new ArrayList<>();
    listOfDepthsHelper(root, lists, 0);
    return lists;
  }

  public static void listOfDepthsHelper(TreeNode node, ArrayList<LinkedList<TreeNode>> lists, int level) {
    if (node == null) {
      return;
    }
    LinkedList<TreeNode> list = null;
    if (lists.size() == level) { // create a new list (new index)
      list = new LinkedList<>();
      lists.add(list);
    } else
      list = lists.get(level);
    list.add(node);
    listOfDepthsHelper(node.left, lists, level + 1);
    listOfDepthsHelper(node.right, lists, level + 1);
  }

  public static void main(String[] args) {
    // Example usage of the Graph class
    Graph<Integer> graph = new Graph<>();
    graph.addEdge(1, 2);
    graph.addEdge(1, 3);
    graph.addEdge(2, 4);
    graph.addEdge(3, 4);
    graph.addEdge(4, 5);
    // another connected graph but disconnected from the first one on the same graph
    graph.addEdge(6, 7);
    graph.addEdge(6, 8);
    graph.addEdge(7, 9);
    graph.addEdge(8, 9);
    graph.addEdge(9, 10);

    // testing for hasRoute function (4.1)
    System.out.println("Is there a route from 1 to 5? " + hasRoute(graph, 1, 5)); // true
    System.out.println("Is there a route from 1 to 10? " + hasRoute(graph, 1, 10)); // false

    // testing for minimal tree (4.2)
    int[] sortedArray = { 1, 2, 3, 4, 5, 6, 7, 8 };
    TreeNode root = minimalTreeBST(sortedArray);
    inOrderTraversal(root);

    // testing for list of depths (4.3)
    System.out.println("\nList of Depths:");
    ArrayList<LinkedList<TreeNode>> lists = listOfDepths(root);
    for (int i = 0; i < lists.size(); i++) {
      System.out.print("Depth " + i + ": ");
      for (TreeNode node : lists.get(i)) {
        System.out.print(node.value + " ");
      }
      System.out.println();
    }
  }
}
