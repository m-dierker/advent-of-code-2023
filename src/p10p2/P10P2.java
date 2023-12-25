package p10p2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class P10P2 {
  public static void main(String[] args) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/10-test3.txt"));

    char[][] maze = new char[lines.size()][];
    boolean[][] visited = new boolean[lines.size()][];
    Point start = null;
    for (int r = 0; r < lines.size(); r++) {
      maze[r] = lines.get(r).toCharArray();
      visited[r] = new boolean[maze[r].length];

      if (start == null) {
        for (int c = 0; c < maze[r].length; c++) {
          if (maze[r][c] == 'S') {
            start = new Point(r, c);
          }
        }
      }
    }

    List<Point> path = getTraversalPath(maze, start, visited);

    boolean[][] partOfPath = new boolean[maze.length][];
    // null = untriaged, true = yes, false = no
    Boolean[][] insidePath = new Boolean[maze.length][];
    for (int r = 0; r < maze.length; r++) {
      partOfPath[r] = new boolean[maze[0].length];
      insidePath[r] = new Boolean[maze[0].length];
    }

    for (Point p : path) {
      partOfPath[p.r][p.c] = true;
    }

    // Traverse entire maze from the outside edge.
    // FIXME

    int inner = 0;
    for (int r = 0; r < maze.length; r++) {
      for (int c = 0; c < maze[0].length; c++) {
        // sorry.
        if (insidePath[r][c] != null && insidePath[r][c] != false) {
          inner++;
        }
      }
    }

    System.out.println("All done: " + inner);
  }

  private static void markOuter(Point point, Direction cameFrom, char[][] maze, boolean[][] partOfPath,
      Boolean[][] insidePath) {
    if (!point.isValid(maze)) {
      return;
    }
    // TODO: Does this need three states?
    if (insidePath[point.r][point.c] != null) {
      return;
    }
    // If this traversal can reach it, it should always be false?
    insidePath[point.r][point.c] = false;

    // Compare pairs to see if it's permeable.
    if (partOfPath[point.r][point.c]) {
      Point p1 = point;
      // Find the pairs to investigate.
      Point p2, p3;
      if (cameFrom.isVertical()) {
        p2 = new Point(point.r, point.c + 1);
        p3 = new Point(point.r, point.c - 1);
      } else {
        // horz
        p2 = new Point(point.r + 1, point.c);
        p3 = new Point(point.r - 1, point.c);
      }
      markOuterPair(p1, p2, cameFrom, maze, partOfPath, insidePath);
      markOuterPair(p1, p3, cameFrom, maze, partOfPath, insidePath);
      return;
    }

    // Any other point is eligible to be traversed.
    char ch = maze[point.r][point.c];
    markOuter(new Point(point.r - 1, point.c), Direction.SOUTH, maze, partOfPath, insidePath);
    markOuter(new Point(point.r + 1, point.c), Direction.NORTH, maze, partOfPath, insidePath);
    markOuter(new Point(point.r, point.c - 1), Direction.EAST, maze, partOfPath, insidePath);
    markOuter(new Point(point.r, point.c + 1), Direction.WEST, maze, partOfPath, insidePath);

  }

  private static void markOuterPair(Point p1, Point p2, Direction cameFrom, char[][] maze, boolean[][] partOfPath,
      Boolean[][] insidePath) {
    if (!p1.isValid(maze) || !p2.isValid(maze)) {
      return;
    }

    // If both sides are off the path, this isn't the right traversal.
    // I think there might be a wrong corner case here.
    if (!partOfPath[p1.r][p1.c] && !partOfPath[p2.r][p2.c]) {
      return;
    }

    char ch1 = maze[p1.r][p1.c];
    char ch2 = maze[p2.r][p2.c];

    if (!permeablePair(ch1, ch2, cameFrom)) {
      return;
    }

    // Continue in the same direction of travel.

    // If one is a dot, call out to the dot and also check for permeable?

  }

  private static boolean permeablePair(char c1, char c2, Direction cameFrom) {
    String c = "" + c1 + c2;

    // F7
    // LJ
    // This should compare against cameFrom.invert(), but it doesn't matter for vert
    // vs. horz.
    if (cameFrom.isVertical()) {
      return !c.equals("F7") && !c.equals("F-") && !c.equals("FJ") && !c.equals("LJ") && !c.equals("L-")
          && !c.equals("L7") && !c.equals("--") && !c.equals("-J") && !c.equals("-7");
    } else if (cameFrom.isHorizontal()) {
      return !c.equals("FL") && !c.equals("F|") && !c.equals("FJ") && !c.equals("||") && !c.equals("|L")
          && !c.equals("|J") && !c.equals("7L") && !c.equals("7|") && !c.equals("7J");
    } else {
      System.err.println("can't happen permeable pair");
      return false;
    }
  }

  private static List<Point> getTraversalPath(char[][] maze, Point start, boolean[][] visited) {
    Deque<Traversal> traversals = new LinkedList<>();
    // Note: This only chooses one traversal out of Start.
    // I'm not sure that's always legal but it is here.
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r - 1,
        start.c),
        Direction.SOUTH, 1, new ArrayList<>()), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r + 1,
        start.c), Direction.NORTH, 1, new ArrayList<>()), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r, start.c -
        1), Direction.EAST,
        1, new ArrayList<>()), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r, start.c +
        1), Direction.WEST,
        1, new ArrayList<>()), maze);

    visited[start.r][start.c] = true;

    while (!traversals.isEmpty()) {
      Traversal t = traversals.pop();
      if (!t.newPoint.isValid(maze)) {
        continue;
      }

      if (maze[t.newPoint.r][t.newPoint.c] == 'S') {
        System.out.println("Solution: " + t.numSteps / 2);
        return t.history;
      }
      // This is already seen.
      if (visited[t.newPoint.r][t.newPoint.c]) {
        // Okay apparently they don't have cycles, but it can meet in the middle.
        // Meeting in the middle is halfway.
        System.out.println("this should never trigger");
        break;
      }
      visited[t.newPoint.r][t.newPoint.c] = true;

      PointDirection next = nextPoint(t.newPoint.r, t.newPoint.c, maze, t.cameFrom);
      // No way to continue the traversal.
      if (next == null) {
        continue;
      }
      // Add a new traversal for the next direction.
      Traversal newTraversal = new Traversal(next.point, next.newCameFrom, t.numSteps + 1, t.history);
      traversals.offer(newTraversal);
    }
    return new ArrayList<>();
  }

  private static void maybeAddStartTraversal(Deque<Traversal> traversals, Traversal t, char[][] maze) {
    // Only traverse one way from the start.
    if (traversals.size() == 1) {
      return;
    }
    if (!t.newPoint.isValid(maze)) {
      return;
    }
    // Only add if you can get back to the start piont.
    Set<Direction> validDirections = nextPointDirection(maze[t.newPoint.r][t.newPoint.c]);
    if (validDirections.contains(t.cameFrom)) {
      traversals.add(t);
    }
  }

  /** Returns null if not a valid traversal. */
  private static PointDirection nextPoint(int r, int c, char[][] maze, Direction cameFrom) {
    Set<Direction> nextDirSet = nextPointDirection(maze[r][c]);
    // Remove the direction we came from.
    nextDirSet.remove(cameFrom);
    Direction next = nextDirSet.iterator().next();
    if (next == null) {
      return null;
    }
    Point point = new Point(-1, -1);
    switch (next) {
      case NORTH:
        point = new Point(r - 1, c);
        break;
      case SOUTH:
        point = new Point(r + 1, c);
        break;
      case WEST:
        point = new Point(r, c - 1);
        break;
      case EAST:
        point = new Point(r, c + 1);
        break;
    }
    PointDirection res = new PointDirection();
    res.point = point;
    res.newCameFrom = next.invert();
    return res;
  }

  private static Set<Direction> nextPointDirection(char ch) {
    switch (ch) {
      // These should all be static sets
      case '|':
        return new HashSet<>(Arrays.asList(Direction.NORTH, Direction.SOUTH));
      case '-':
        return new HashSet<>(Arrays.asList(Direction.EAST, Direction.WEST));
      case 'L':
        return new HashSet<>(Arrays.asList(Direction.NORTH, Direction.EAST));
      case 'J':
        return new HashSet<>(Arrays.asList(Direction.NORTH, Direction.WEST));
      case '7':
        return new HashSet<>(Arrays.asList(Direction.SOUTH, Direction.WEST));
      case 'F':
        return new HashSet<>(Arrays.asList(Direction.SOUTH, Direction.EAST));
      case '.':
        return new HashSet<>();
      default:
        System.err.println("Unknown character: " + ch);
        return new HashSet<>();
    }
  }

  private static Optional<Point> getInnerTurnPoint(Point point, char[][] maze) {
    if (!point.isValid(maze)) {
      return Optional.empty();
    }
    switch (maze[point.r][point.c]) {
      case 'L':
        return Optional.of(new Point(point.r - 1, point.c + 1));
      case 'J':
        return Optional.of(new Point(point.r - 1, point.c - 1));
      case '7':
        return Optional.of(new Point(point.r + 1, point.c - 1));
      case 'F':
        return Optional.of(new Point(point.r + 1, point.c + 1));
    }
    return Optional.empty();
  }
}

class Traversal {
  Point newPoint;
  Direction cameFrom;
  int numSteps;
  List<Point> history;

  Traversal(Point newPoint, Direction cameFrom, int numSteps, List<Point> history) {
    this.newPoint = newPoint;
    this.cameFrom = cameFrom;
    this.numSteps = numSteps;

    this.history = history;
    this.history.add(newPoint);
  }
}

class PointDirection {
  Point point;
  Direction newCameFrom;
}

class Point {
  int r;
  int c;

  Point(int r, int c) {
    this.r = r;
    this.c = c;
  }

  public boolean isValid(char[][] maze) {
    return r >= 0 && c >= 0 && r < maze.length && c < maze[0].length;
  }

  public String toString() {
    return String.format("(%d, %d)", r, c);
  }
}

enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST;

  Direction invert() {
    switch (this) {
      case WEST:
        return EAST;
      case EAST:
        return WEST;
      case NORTH:
        return SOUTH;
      case SOUTH:
        return NORTH;
      default:
        return null;
    }
  }

  boolean isHorizontal() {
    return this == WEST || this == EAST;
  }

  boolean isVertical() {
    return this == NORTH || this == SOUTH;
  }
}
