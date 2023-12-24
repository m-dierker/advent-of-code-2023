package p8p2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class P8P2 {
  public static void main(String[] args) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/8.txt"));

    String path = lines.get(0);
    Map<String, Node> nodes = new HashMap<>();

    Pattern nodePattern = Pattern.compile("([A-Z\\d]+) = \\(([A-Z\\d]+), ([A-Z\\d]+)\\)");
    for (int i = 2; i < lines.size(); i++) {
      System.out.println(lines.get(i));
      Matcher matcher = nodePattern.matcher(lines.get(i).trim());
      matcher.matches();

      Node node = getNode(matcher.group(1), nodes);
      nodes.put(matcher.group(1), node);

      node.left = getNode(matcher.group(2), nodes);
      node.right = getNode(matcher.group(3), nodes);
    }

    Node[] cur = nodes.values().stream().filter(node -> node.self.endsWith("A")).toArray(Node[]::new);
    int[] iterations = new int[cur.length];
    int iteration = 0;
    while (!allNull(cur)) {
      iteration++;
      for (int i = 0; i < path.length(); i++) {
        for (int curIdx = 0; curIdx < cur.length; curIdx++) {
          if (cur[curIdx] == null) {
            continue;
          }
          cur[curIdx] = path.charAt(i) == 'R' ? cur[curIdx].right : cur[curIdx].left;
        }
      }
      for (int i = 0; i < cur.length; i++) {
        if (cur[i] == null) {
          continue;
        }
        if (cur[i].self.endsWith("Z")) {
          iterations[i] = iteration;
          cur[i] = null;
        }
      }
    }
    System.out
        .println(Arrays.stream(iterations).mapToLong(i -> i * 1L).reduce(1L, (mul, val) -> mul * val) * path.length());

  }

  private static boolean allNull(Node[] cur) {
    for (Node node : cur) {
      if (node != null) {
        return false;
      }
    }
    return true;
  }

  private static Node getNode(String node, Map<String, Node> nodes) {
    if (nodes.containsKey(node)) {
      return nodes.get(node);
    }
    Node newNode = new Node(node);
    nodes.put(node, newNode);
    return newNode;
  }
}

class Node {
  String self;

  Node(String self) {
    this.self = self;
  }

  Node left;
  Node right;
}