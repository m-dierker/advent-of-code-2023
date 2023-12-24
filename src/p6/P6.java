package p6;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class P6 {
  public static void main(String[] args) throws Exception {
    Scanner in = new Scanner(new File("./data/6.txt"));

    // Idx 0 is labels. Ignore.
    String[] timeArr = in.nextLine().split("\\s+");
    String[] distArr = in.nextLine().split("\\s+");

    int res = 1;
    for (int i = 1; i < timeArr.length; i++) {
      int time = Integer.parseInt(timeArr[i]);
      int record = Integer.parseInt(distArr[i]);

      res *= solve(time, record);
    }
    System.out.println("res: " + res);
  }

  private static int solve(int time, int record) {
    int res = 0;
    for (int holdTime = 0; holdTime <= time; holdTime++) {
      int speed = holdTime;
      int timeToRace = time - holdTime;
      if (speed * timeToRace > record) {
        res++;
      }
    }
    return res;
  }
}
