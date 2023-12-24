package p8;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class P8 {
  public static void main(String[] args) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/8.txt"));

    String path = lines.get(0);
    Map<String, Node> nodes = new HashMap<>();

    Pattern nodePattern = Pattern.compile("([A-Z]+) = \\(([A-Z]+), ([A-Z]+)\\)");
    for (int i = 2; i < lines.size(); i++) {
      System.out.println(lines.get(i));
      Matcher matcher = nodePattern.matcher(lines.get(i).trim());
      matcher.matches();

      Node node = getNode(matcher.group(1), nodes);
      nodes.put(matcher.group(1), node);

      node.left = getNode(matcher.group(2), nodes);
      node.right = getNode(matcher.group(3), nodes);
    }

    Node cur = nodes.get("AAA");
    int steps = 0;
    while (!cur.self.equals("ZZZ")) {
      for (int i = 0; i < path.length(); i++) {
        cur = path.charAt(i) == 'R' ? cur.right : cur.left;
      }
      steps += path.length();
    }
    System.out.println(steps);

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