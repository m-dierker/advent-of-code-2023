package p10.wrong;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class P10wrong {
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

    visited[start.r][start.c] = true;
    Set<Integer> numSteps = new HashSet<>();
    // possible off by one on numSteps
    numSteps.add(traverse(start.r - 1, start.c, maze, visited, Direction.SOUTH,
        1));
    numSteps.add(traverse(start.r + 1, start.c, maze, visited, Direction.NORTH, 1));
    numSteps.add(traverse(start.r, start.c - 1, maze, visited, Direction.EAST,
        1));
    numSteps.add(traverse(start.r, start.c + 1, maze, visited, Direction.WEST,
        1));

    System.out.println(numSteps);
    System.out.println(numSteps.stream().filter(elem -> elem != -1).max(Integer::compare).orElse(0) / 2);
  }

  public static int traverse(int r, int c, char[][] maze, boolean[][] visited, Direction cameFrom, int numSteps) {
    if (r < 0 || c < 0 || r >= maze.length || c >= maze[0].length) {
      return -1;
    }
    if (maze[r][c] == 'S') {
      return numSteps;
    }
    if (visited[r][c]) {
      return -1;
    }
    visited[r][c] = true;

    PointDirection next = nextPoint(r, c, maze, cameFrom);
    int result;
    if (next == null) {
      result = -1;
    } else {
      result = traverse(next.point.r, next.point.c, maze, visited, next.newCameFrom, numSteps + 1);
    }

    // This is safe, but might mean walking the same long parts of the maze
    // unnecessarily.
    visited[r][c] = false;
    return result;
  }

  /** Returns null if not a valid traversal. */
  private static PointDirection nextPoint(int r, int c, char[][] maze, Direction cameFrom) {
    Direction next = nextPointDirection(maze[r][c], cameFrom);
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

  private static Direction nextPointDirection(char ch, Direction cameFrom) {
    switch (ch) {
      case '|':
        return cameFrom == Direction.NORTH ? Direction.SOUTH : Direction.NORTH;
      case '-':
        return cameFrom == Direction.EAST ? Direction.WEST : Direction.EAST;
      case 'L':
        return cameFrom == Direction.NORTH ? Direction.EAST : Direction.NORTH;
      case 'J':
        return cameFrom == Direction.NORTH ? Direction.WEST : Direction.NORTH;
      case '7':
        return cameFrom == Direction.SOUTH ? Direction.WEST : Direction.SOUTH;
      case 'F':
        return cameFrom == Direction.SOUTH ? Direction.EAST : Direction.SOUTH;
      case '.':
        return null;
      default:
        System.err.println("Unknown character: " + ch);
        return null;
    }
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
