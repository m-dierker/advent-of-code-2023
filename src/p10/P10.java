package p10;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class P10 {
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

    Deque<Traversal> traversals = new LinkedList<>();
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r - 1,
        start.c),
        Direction.SOUTH, 1), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r + 1,
        start.c), Direction.NORTH, 1), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r, start.c -
        1), Direction.EAST,
        1), maze);
    maybeAddStartTraversal(traversals, new Traversal(new Point(start.r, start.c +
        1), Direction.WEST,
        1), maze);

    visited[start.r][start.c] = true;

    while (!traversals.isEmpty()) {
      Traversal t = traversals.pop();
      if (!t.newPoint.isValid(maze)) {
        continue;
      }

      if (maze[t.newPoint.r][t.newPoint.c] == 'S') {
        System.out.println("Solution: " + t.numSteps / 2);
        break;
      }
      // This is already seen.
      if (visited[t.newPoint.r][t.newPoint.c]) {
        // Okay apparently they don't have cycles, but it can meet in the middle.
        // Meeting in the middle is halfway.
        System.out.println(t.numSteps);
        break;
      }
      visited[t.newPoint.r][t.newPoint.c] = true;

      PointDirection next = nextPoint(t.newPoint.r, t.newPoint.c, maze, t.cameFrom);
      // No way to continue the traversal.
      if (next == null) {
        continue;
      }
      // Add a new traversal for the next direction.
      Traversal newTraversal = new Traversal(next.point, next.newCameFrom, t.numSteps + 1);
      traversals.offer(newTraversal);
    }
    System.out.println("All done");
  }

  private static void maybeAddStartTraversal(Deque<Traversal> traversals, Traversal t, char[][] maze) {
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
        return null;
      default:
        System.err.println("Unknown character: " + ch);
        return null;
    }
  }
}

class Traversal {
  Point newPoint;
  Direction cameFrom;
  int numSteps;

  Traversal(Point newPoint, Direction cameFrom, int numSteps) {
    this.newPoint = newPoint;
    this.cameFrom = cameFrom;
    this.numSteps = numSteps;
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
}
