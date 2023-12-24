import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class P2 {
  public static void main(String... args) throws Exception {
    List<String> games = Files.readAllLines(Paths.get("./data/2.txt"));

    int summedGameIds = 0;
    for (String gameStr : games) {
      System.out.println(gameStr);
      Pattern pattern = Pattern.compile("Game (\\d+): (.*)");
      Matcher gameMatcher = pattern.matcher(gameStr);
      gameMatcher.matches();

      int gameId = Integer.parseInt(gameMatcher.group(1));

      Pattern cubePattern = Pattern.compile("(\\d+) (red|blue|green)");
      List<int[]> gameRounds = Arrays.stream(gameMatcher.group(2).split(";")).map(
          round -> {
            // r, g, b
            int[] cubes = new int[3];
            Matcher matcher = cubePattern.matcher(round);
            while (matcher.find()) {
              int cubeCount = Integer.parseInt(matcher.group(1));
              int colorIdx = getColorIdx(matcher.group(2));
              cubes[colorIdx] += cubeCount;
            }
            System.out.println("round cubes: " + round + " = " + Arrays.toString(cubes));

            return cubes;
          }).toList();
      int[] gameTotals = gameRounds.stream().reduce(new int[] { 0, 0, 0 },
          (acc, arr) -> new int[] { Math.max(acc[0], arr[0]), Math.max(acc[1], arr[1]), Math.max(acc[2], arr[2]) });

      if (gameTotals[0] <= 12 && gameTotals[1] <= 13 && gameTotals[2] <= 14) {
        summedGameIds += gameId;
      }
    }
    System.out.println("Sum: " + summedGameIds);
  }

  private static int getColorIdx(String color) {
    switch (color) {
      case "red":
        return 0;
      case "green":
        return 1;
      case "blue":
        return 2;
      default:
        System.err.println("Missing color: " + color);
        return -1;
    }
  }
}