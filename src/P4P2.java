import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class P4P2 {
  public static void main(String... args) throws Exception {
    List<String> cards = Files.readAllLines(Paths.get("./data/4.txt"));

    Pattern cardPattern = Pattern.compile(".*: ([\\d\\s]+) \\| ([\\d\\s]+)");
    int scoreSum = 0;
    Map<Integer, Integer> baseCardsWon = new HashMap<>();
    for (int cardIdx = 1; cardIdx <= cards.size(); cardIdx++) {
      String card = cards.get(cardIdx - 1);

      Matcher cardMatcher = cardPattern.matcher(card);
      cardMatcher.matches();

      Set<Integer> winners = Arrays.stream(cardMatcher.group(1).trim().split(" ")).filter(str -> str.length() > 0)
          .map(str -> Integer.parseInt(str))
          .collect(Collectors.toSet());
      List<Integer> numbers = Arrays.stream(cardMatcher.group(2).trim().split(" ")).filter(str -> str.length() > 0)
          .map(str -> Integer.parseInt(str))
          .toList();

      int matchingNums = 0;
      for (int num : numbers) {
        if (winners.contains(num)) {
          matchingNums++;
        }
      }
      baseCardsWon.put(cardIdx, matchingNums);
    }

    System.out.println(baseCardsWon);

    Map<Integer, Integer> totalCardsWon = new HashMap<>();
    for (int cardIdx = baseCardsWon.size(); cardIdx >= 1; cardIdx--) {
      int cardsWon = baseCardsWon.get(cardIdx);

      // count yourself?
      int totalCardsWonThisCard = 1;
      for (int i = cardIdx + 1; i <= cardIdx + cardsWon; i++) {
        totalCardsWonThisCard += totalCardsWon.get(i);
      }
      totalCardsWon.put(cardIdx, totalCardsWonThisCard);
    }

    System.out.println("numCards: " + totalCardsWon.values().stream().reduce(0, (arr, val) -> arr + val));
  }
}
