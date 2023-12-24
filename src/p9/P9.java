package p9;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P9 {
  public static void main(String[] args) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/9.txt"));

    long sum = 0;
    for (String origSeq : lines) {
      sum += solve(Arrays.stream(origSeq.split(" ")).map(str -> Integer.parseInt(str)).toList());
    }
    System.out.println("sum: " + sum);
  }

  private static int solve(List<Integer> seq) {
    if (allZeroes(seq)) {
      return 0;
    }
    List<Integer> diffs = new ArrayList<>(seq.size() - 1);
    for (int i = 0; i < seq.size() - 1; i++) {
      diffs.add(seq.get(i + 1) - seq.get(i));
    }
    return seq.get(seq.size() - 1) + solve(diffs);
  }

  private static boolean allZeroes(List<Integer> seq) {
    for (int i : seq) {
      if (i != 0) {
        return false;
      }
    }
    return true;
  }
}
