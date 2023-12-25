package p11p2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class P11P2 {
  public static int EXPANSION_VALUE = 1000000;

  public static void main(String[] args) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/11.txt"));

    List<List<Character>> map = new ArrayList<>(lines.size());
    for (int r = 0; r < lines.size(); r++) {
      List<Character> line = lines.get(r).chars().mapToObj(c -> (char) c)
          .collect(Collectors.toCollection(ArrayList::new));
      map.add(line);
    }

    // Adjust all rows.
    int rowSize = map.size();
    for (int r = 0; r < rowSize; r++) {
      if (allEmpty(map.get(r))) {
        for (int c = 0; c < map.get(0).size(); c++) {
          map.get(r).set(c, '*');
        }
      }
    }

    // Adjust all columns.
    int colSize = map.get(0).size();
    for (int c = 0; c < colSize; c++) {
      if (allEmpty(map, c)) {
        for (int r = 0; r < map.size(); r++) {
          map.get(r).set(c, '*');
        }
      }
    }

    print(map);

    List<Point> galaxies = new ArrayList<>();
    for (int r = 0; r < map.size(); r++) {
      for (int c = 0; c < map.get(0).size(); c++) {
        if (map.get(r).get(c) == '#') {
          galaxies.add(new Point(r, c));
        }
      }
    }

    long sum = 0;
    for (int i = 0; i < galaxies.size() - 1; i++) {
      for (int j = i + 1; j < galaxies.size(); j++) {
        Point p1 = galaxies.get(i);
        Point p2 = galaxies.get(j);
        sum += p1.dist(p2, map);
        System.out.println("Completed traversal " + i + "," + j + ", total size " + galaxies.size());
      }
    }
    System.out.println("sum: " + sum);

  }

  private static boolean allEmpty(List<Character> row) {
    for (char c : row) {
      if (c != '.' && c != '*') {
        return false;
      }
    }
    return true;
  }

  private static boolean allEmpty(List<List<Character>> map, int c) {
    for (int r = 0; r < map.size(); r++) {
      if (map.get(r).get(c) != '.' && map.get(r).get(c) != '*') {
        return false;
      }
    }
    return true;
  }

  private static void print(List<List<Character>> map) {
    for (int r = 0; r < map.size(); r++) {
      for (int c = 0; c < map.get(0).size(); c++) {
        System.out.print(map.get(r).get(c));
      }
      System.out.println();
    }
  }
}

class Point {
  int r;
  int c;

  Point(int r, int c) {
    this.r = r;
    this.c = c;
  }

  long dist(Point other, List<List<Character>> map) {
    long[][] bestCostsToDest = new long[map.size()][];
    for (int r = 0; r < bestCostsToDest.length; r++) {
      bestCostsToDest[r] = new long[map.get(0).size()];
      for (int c = 0; c < bestCostsToDest[0].length; c++) {
        bestCostsToDest[r][c] = Long.MAX_VALUE;
      }
    }
    // Start at other, come to this.
    bestCostsToDest[other.r][other.c] = 0;

    Deque<Traversal> traversals = new LinkedList<>();
    traversals.add(new Traversal(new Point(other.r - 1, other.c), 0));
    traversals.add(new Traversal(new Point(other.r + 1, other.c), 0));
    traversals.add(new Traversal(new Point(other.r, other.c - 1), 0));
    traversals.add(new Traversal(new Point(other.r, other.c + 1), 0));

    while (!traversals.isEmpty()) {
      Traversal t = traversals.pop();

      // Points off the map should bail.
      if (!t.point.isValid(map)) {
        continue;
      }

      long selfCost = map.get(t.point.r).get(t.point.c) == '.' || map.get(t.point.r).get(t.point.c) == '#' ? 1
          : P11P2.EXPANSION_VALUE;
      if (t.point.equals(this)) {
        // Initial point apparently has one cost. I don't quite get this.
        selfCost = 1;
      }
      long totalCost = t.prevCost + selfCost;

      // If this traversal's cost is already higher than the best total cost or the
      // best cost to this piont, bail.
      if (totalCost >= bestCostsToDest[this.r][this.c] || totalCost >= bestCostsToDest[t.point.r][t.point.c]) {
        continue;
      }
      bestCostsToDest[t.point.r][t.point.c] = totalCost;

      // If this is the final point, stop traversing.
      if (t.point.equals(this)) {
        continue;
      }

      // Traverse in all directions.
      traversals.offer(new Traversal(new Point(t.point.r - 1, t.point.c), totalCost));
      traversals.offer(new Traversal(new Point(t.point.r + 1, t.point.c), totalCost));
      traversals.offer(new Traversal(new Point(t.point.r, t.point.c - 1), totalCost));
      traversals.offer(new Traversal(new Point(t.point.r, t.point.c + 1), totalCost));
    }

    return bestCostsToDest[this.r][this.c];
  }

  public boolean equals(Point other) {
    return this.r == other.r && this.c == other.c;
  }

  public <T> boolean isValid(List<List<T>> list) {
    return r >= 0 && c >= 0 && r < list.size() && c < list.get(0).size();
  }

  public String toString() {
    return String.format("(%d, %d)", r, c);
  }
}

class Traversal {
  Point point;
  long prevCost;

  Traversal(Point point, long prevCost) {
    this.point = point;
    this.prevCost = prevCost;
  }
}