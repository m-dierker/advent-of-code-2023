package p6p2;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class P6P2 {
  public static void main(String[] args) throws Exception {
    Scanner in = new Scanner(new File("./data/6.txt"));

    // Idx 0 is labels. Ignore.
    long totalTime = Long.parseLong(in.nextLine().replaceAll("[^\\d]+", ""));
    long distRecord = Long.parseLong(in.nextLine().replaceAll("[^\\d]+", ""));
    System.out.println(totalTime + " " + distRecord);

    System.out.println("res: " + solve(totalTime, distRecord));
  }

  private static long solve(long time, long record) {
    long res = 0;
    for (long holdTime = 0; holdTime <= time; holdTime++) {
      long speed = holdTime;
      long timeToRace = time - holdTime;
      if (speed * timeToRace > record) {
        res++;
      }
    }
    return res;
  }
}
