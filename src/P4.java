import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class P4 {
  public static void main(String... args) throws Exception {
    List<String> cards = Files.readAllLines(Paths.get("./data/4.txt"));

    Pattern cardPattern = Pattern.compile(".*: ([\\d\\s]+) \\| ([\\d\\s]+)");
    int scoreSum = 0;
    for (String card : cards) {
      Matcher cardMatcher = cardPattern.matcher(card);
      cardMatcher.matches();

      Set<Integer> winners = Arrays.stream(cardMatcher.group(1).trim().split(" ")).filter(str -> str.length() > 0)
          .map(str -> Integer.parseInt(str))
          .collect(Collectors.toSet());
      List<Integer> numbers = Arrays.stream(cardMatcher.group(2).trim().split(" ")).filter(str -> str.length() > 0)
          .map(str -> Integer.parseInt(str))
          .toList();

      int score = 0;
      for (int num : numbers) {
        if (winners.contains(num)) {
          if (score == 0) {
            score = 1;
          } else {
            score *= 2;
          }
        }
      }
      scoreSum += score;
    }
    System.out.println("score sum: " + scoreSum);
  }
}
