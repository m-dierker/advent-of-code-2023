package p5p2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class P5P2 {
  public static void main(String... args) throws Exception {
    Scanner in = new Scanner(new File("./data/5.txt"));

    String seedsLine = in.nextLine();
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

    System.out.println("Filling trees");
    // map.get("seed-to-soil map:").fill();
    // System.exit(1);
    for (Tree tree : map.values()) {
      tree.fill();
    }

    List<Range> vals = new ArrayList<>();
    String[] seedsArr = seedsLine.split(" ");
    for (int i = 1; i < seedsArr.length; i += 2) {
      long seedStart = Long.parseLong(seedsArr[i]);
      long seedCount = Long.parseLong(seedsArr[i + 1]);
      vals.add(new Range(seedStart, seedStart + seedCount - 1));
    }

    vals = transform(vals, map, "seed-to-soil map:");
    // System.out.println("first vals" + vals);

    vals = transform(vals, map, "soil-to-fertilizer map:");
    vals = transform(vals, map, "fertilizer-to-water map:");
    vals = transform(vals, map, "water-to-light map:");
    vals = transform(vals, map, "light-to-temperature map:");
    vals = transform(vals, map, "temperature-to-humidity map:");
    vals = transform(vals, map, "humidity-to-location map:");

    long min = Long.MAX_VALUE;
    for (Range val : vals) {
      min = Math.min(val.min, min);
    }
    System.out.println("min: " + min);

  }

  private static List<Range> transform(List<Range> vals, Map<String, Tree> map, String mapName) {
    Tree tree = map.get(mapName);

    List<Range> results = new ArrayList<>();
    for (Range range : vals) {
      results.addAll(tree.translate(range));
    }
    return results;
  }
}

class Tree {
  TreeNode root;
  long maxVal = -1;

  Tree() {
  }

  public List<Range> translate(Range src) {
    long cur = src.min;

    List<Range> ranges = new ArrayList<>();
    while (cur < src.max) {
      LookupResult result = translate(cur, root, src.max);
      cur = result.srcRange.max + 1;

      Range dstRange = new Range();
      dstRange.min = result.dstMin;
      dstRange.max = result.dstMin + (result.srcRange.max - result.srcRange.min);
      ranges.add(dstRange);
    }
    return ranges;
  }

  private LookupResult translate(long srcStart, TreeNode cur, long srcMax) {
    if (cur.contains(srcStart)) {
      return cur.translate(srcStart, srcMax);
    } else if (cur.srcIsLeft(srcStart) && cur.left != null) {
      return translate(srcStart, cur.left, srcMax);
    } else if (!cur.srcIsLeft(srcStart) && cur.right != null) {
      return translate(srcStart, cur.right, srcMax);
    }
    // This shouldn't happen after fill().
    return null;
  }

  public void insert(long src, long dest, long cnt) {
    TreeNode node = new TreeNode();
    node.minSrc = src;
    node.maxSrc = src + cnt - 1;
    node.minDest = dest;

    // Update Tree's cache of max value.
    if (maxVal < node.maxSrc) {
      maxVal = node.maxSrc;
    }

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

  /**
   * Fill any missing ranges. This wouldn't be necessary if I'd used a LinkedList
   * instead. -_-
   */
  public void fill() {
    long cur = 0;
    long lastValidIdx = -1;
    while (cur < maxVal) {
      // System.out.println("Checking " + cur);
      LookupResult result = translate(cur, root, Long.MAX_VALUE);
      if (result != null) {
        // System.out.println("Found valid result: " + result.srcRange);
        cur = result.srcRange.max + 1;
        lastValidIdx = result.srcRange.max;
        continue;
      }

      // Find the next non-null result.
      // Is this too inefficient? Hmm.
      // The "right" way is to find the next rightmost node... cc: timeline.
      // Let's see if this works.
      // Hey, it's a little slow, but it does eventually! :D

      while (cur < maxVal && result == null) {
        cur++;
        result = translate(cur, root, Long.MAX_VALUE);
      }
      long fillStart = lastValidIdx + 1;
      long fillCnt = result.srcRange.min - fillStart; // -1 is for the end, +1 is for counting with subtraction.
      // Fill with the same value.
      // System.out.println("Filling shim node " + fillStart + "," + fillCnt);
      insert(fillStart, fillStart, fillCnt);
    }
    // This is dumb and I hate it so much.
    insert(cur, cur, Long.MAX_VALUE - cur);
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

  LookupResult translate(Long srcStart, Long maxLookupSrc) {
    if (!contains(srcStart)) {
      throw new Error("Cannot lookup " + srcStart + " in " + minSrc + "," + maxSrc);
    }
    LookupResult result = new LookupResult();
    result.srcRange.min = srcStart;
    result.srcRange.max = Math.min(maxLookupSrc, maxSrc);
    result.dstMin = srcStart + (minDest - minSrc);
    return result;
  }

  public String toString() {
    return "src={" + minSrc + "," + maxSrc + "}, minDest={" + minDest + "}, left=" + left + ", right=" + right;
  }
}

class Range {
  long min;
  // inclusive
  long max;

  Range() {

  }

  Range(long min, long max) {
    this.min = min;
    this.max = max;
  }

  public String toString() {
    return String.format("[%d, %d]", min, max);
  }
}

class LookupResult {
  Range srcRange;
  Long dstMin;

  LookupResult() {
    srcRange = new Range();
  }
}