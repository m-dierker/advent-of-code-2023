import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

package p5;

public class P5 {
  public static void main(String... args) throws Exception {
    Scanner in = new Scanner(new File("./data/5.txt"));

    String seeds = in.nextLine();
    in.nextLine(); // blank line

    Map<String, Tree> map = new HashMap<>();

    while (in.hasNext()) {
      String mapName = in.nextLine().trim();
      System.out.println("Building " + mapName);
      Tree tree = new Tree();
      map.put(mapName, tree);

      // Within a map, parse data.
      while (in.hasNext()) {
        String line = in.nextLine();
        if (line == "") {
          break;
        }

        List<Long> mapEntry = Arrays.stream(line.split(" ")).map(str -> Long.parseLong(str)).toList();

        long dest = mapEntry.get(0);
        long src = mapEntry.get(1);
        long cnt = mapEntry.get(2);

        tree.insert(src, dest, cnt);
      }
    }

    Set<Long> vals = Arrays
        .stream(Pattern.compile("seeds: (.*)").matcher(seeds).results().map(m -> m.group(1)).findFirst().orElseThrow()
            .split(" "))
        .map(str -> Long.parseLong(str))
        .collect(Collectors.toSet());

    vals = transform(vals, map, "seed-to-soil map:");
    // System.out.println("first vals" + vals);

    vals = transform(vals, map, "soil-to-fertilizer map:");
    vals = transform(vals, map, "fertilizer-to-water map:");
    vals = transform(vals, map, "water-to-light map:");
    vals = transform(vals, map, "light-to-temperature map:");
    vals = transform(vals, map, "temperature-to-humidity map:");
    vals = transform(vals, map, "humidity-to-location map:");

    System.out.println("min: " + vals.stream().reduce(Long.MAX_VALUE, (v1, v2) -> Math.min(v1, v2)).longValue());

  }

  private static Set<Long> transform(Set<Long> vals, Map<String, Tree> map, String mapName) {
    Tree tree = map.get(mapName);
    // System.out.println("mapName: " + mapName + " " + subMap);
    return vals.stream().map(val -> tree.translate(val)).collect(Collectors.toSet());
  }
}

class Tree {
  TreeNode root;

  Tree() {
  }

  public Long translate(long src) {
    return translate(src, root);
  }

  private Long translate(long src, TreeNode cur) {
    if (cur.contains(src)) {
      return cur.translate(src);
    } else if (cur.srcIsLeft(src) && cur.left != null) {
      return translate(src, cur.left);
    } else if (!cur.srcIsLeft(src) && cur.right != null) {
      return translate(src, cur.right);
    }
    return src;
  }

  public void insert(long src, long dest, long cnt) {
    TreeNode node = new TreeNode();
    node.minSrc = src;
    node.maxSrc = src + cnt - 1;
    node.minDest = dest;

    System.out.println("Inserting " + node);

    if (root == null) {
      root = node;
    } else {
      insert(root, node);
    }
  }

  private void insert(TreeNode cur, TreeNode newNode) {
    if (newNode.maxSrc < cur.minSrc) {
      // left
      if (cur.left != null) {
        insert(cur.left, newNode);
      } else {
        cur.left = newNode;
      }
      return;
    }

    // right
    if (cur.right != null) {
      insert(cur.right, newNode);
    } else {
      cur.right = newNode;
    }
  }
}

class TreeNode {
  long minSrc;
  // Inclusive
  long maxSrc;

  long minDest;

  TreeNode left;
  TreeNode right;

  boolean contains(Long src) {
    return minSrc <= src && src <= maxSrc;
  }

  boolean srcIsLeft(Long src) {
    return src < minSrc;
  }

  Long translate(Long src) {
    if (!contains(src)) {
      throw new Error("Cannot lookup " + src + " in " + minSrc + "," + maxSrc);
    }
    return src + (minDest - minSrc);
  }

  public String toString() {
    return "src={" + minSrc + "," + maxSrc + "}, minDest={" + minDest + "}, left=" + left + ", right=" + right;
  }
}