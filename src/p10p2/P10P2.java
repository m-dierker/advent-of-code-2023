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
    List<String> lines = Files.readAllLines(Paths.get("./data/10.txt"));

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

    // Rebuild maze in 2x.
    char[][] doubleMaze = new char[maze.length * 2][];
    SpotLabel[][] labels = new SpotLabel[maze.length * 2][];
    for (int r = 0; r < doubleMaze.length; r++) {
      doubleMaze[r] = new char[maze[0].length * 2];
      labels[r] = new SpotLabel[maze[0].length * 2];
      for (int c = 0; c < doubleMaze[0].length; c++) {
        doubleMaze[r][c] = ' ';
      }
    }

    // Copy dots only once.
    for (int r = 0; r < maze.length; r++) {
      for (int c = 0; c < maze[0].length; c++) {
        doubleMaze[r * 2][c * 2] = '.';
      }
    }

    // Put the original path in, at 2x.
    for (Point p : path) {
      doubleMaze[p.r * 2][p.c * 2] = maze[p.r][p.c];
      labels[p.r * 2][p.c * 2] = SpotLabel.PATH;
    }
    // Fill the gaps.
    for (int i = 0; i < path.size() - 1; i++) {
      Point p1 = path.get(i);
      Point p2 = path.get(i + 1);
      fillGap(p1, p2, doubleMaze, labels);
    }
    // Fill the start to the first point.
    // The start is the last point.
    fillGap(path.get(0), path.get(path.size() - 1), doubleMaze, labels);

    // Traverse entire maze from the outside edge.
    for (int c = 0; c < doubleMaze[0].length; c++) {
      markOuter(new Point(0, c), doubleMaze, labels);
      markOuter(new Point(doubleMaze.length - 1, c), doubleMaze, labels);
    }
    for (int r = 0; r < doubleMaze.length; r++) {
      markOuter(new Point(r, 0), doubleMaze, labels);
      markOuter(new Point(r, doubleMaze[0].length - 1), doubleMaze, labels);
    }

    // Count unlabeled periods.
    int area = 0;
    for (int r = 0; r < doubleMaze.length; r++) {
      for (int c = 0; c < doubleMaze[0].length; c++) {
        // Inner isn't really labeled.
        if (doubleMaze[r][c] != ' ' && labels[r][c] == null) {
          area++;
        }
      }
    }
    System.out.println("Area: " + area);

  }

  private static void markOuter(Point p, char[][] doubleMaze, SpotLabel[][] labels) {
    if (!p.isValid(doubleMaze)) {
      return;
    }
    // If the point has been seen already, mark it off.
    if (labels[p.r][p.c] != null) {
      return;
    }

    labels[p.r][p.c] = SpotLabel.OUTER;
    markOuter(new Point(p.r - 1, p.c), doubleMaze, labels);
    markOuter(new Point(p.r + 1, p.c), doubleMaze, labels);
    markOuter(new Point(p.r, p.c - 1), doubleMaze, labels);
    markOuter(new Point(p.r, p.c + 1), doubleMaze, labels);
  }

  private static void fillGap(Point p1, Point p2, char[][] doubleMaze, SpotLabel[][] labels) {
    // Should only be possible to be off by column or row but not both.
    if (p1.r != p2.r && p1.c != p2.c) {
      System.err.println("Double offset " + p1 + " " + p2);
    } else if (p1.r != p2.r) {
      // Vertical difference
      doubleMaze[Math.min(p1.r, p2.r) * 2 + 1][p1.c * 2] = '|';
      labels[Math.min(p1.r, p2.r) * 2 + 1][p1.c * 2] = SpotLabel.PATH;
    } else if (p1.c != p2.c) {
      // Horz difference.
      doubleMaze[p1.r * 2][Math.min(p1.c, p2.c) * 2 + 1] = '-';
      labels[p1.r * 2][Math.min(p1.c, p2.c) * 2 + 1] = SpotLabel.PATH;
    } else {
      System.err.println("Same point twice? " + p1);
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

  private static void printMaze(char[][] maze) {
    for (int r = 0; r < maze.length; r++) {
      for (int c = 0; c < maze[0].length; c++) {
        System.out.print(maze[r][c]);
      }
      System.out.println();
    }
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

enum SpotLabel {
  OUTER,
  PATH,
  INNER
}
