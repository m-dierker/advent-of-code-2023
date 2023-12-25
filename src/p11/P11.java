package p11;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class P11 {
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
        List<Character> newCopy = new ArrayList<>(map.get(r));
        map.add(r, newCopy);
        rowSize = map.size();
        r++;
      }
    }

    // Adjust all columns.
    int colSize = map.get(0).size();
    for (int c = 0; c < colSize; c++) {
      if (allEmpty(map, c)) {
        for (int r = 0; r < map.size(); r++) {
          map.get(r).add(c, '.');
        }
        c++;
        colSize = map.get(0).size();
      }
    }

    List<Point> galaxies = new ArrayList<>();
    for (int r = 0; r < map.size(); r++) {
      for (int c = 0; c < map.get(0).size(); c++) {
        if (map.get(r).get(c) == '#') {
          galaxies.add(new Point(r, c));
        }
      }
    }

    int sum = 0;
    for (int i = 0; i < galaxies.size() - 1; i++) {
      for (int j = i + 1; j < galaxies.size(); j++) {
        Point p1 = galaxies.get(i);
        Point p2 = galaxies.get(j);
        sum += p1.dist(p2);
      }
    }
    System.out.println("sum: " + sum);

  }

  private static boolean allEmpty(List<Character> row) {
    for (char c : row) {
      if (c != '.') {
        return false;
      }
    }
    return true;
  }

  private static boolean allEmpty(List<List<Character>> map, int c) {
    for (int r = 0; r < map.size(); r++) {
      if (map.get(r).get(c) != '.') {
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

  int dist(Point other) {
    return Math.abs(r - other.r) + Math.abs(c - other.c);
  }
}