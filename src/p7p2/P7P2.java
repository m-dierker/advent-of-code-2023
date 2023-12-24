package p7p2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class P7P2 {
  public static void main(String[] args) throws Exception {
    List<Hand> hands = Files.readAllLines(Paths.get("./data/7.txt")).stream()
        .map(handLine -> {
          String[] line = handLine.split(" ");
          Hand hand = new Hand(line[0], Integer.parseInt(line[1]));
          return hand;
        })
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.sort(hands);

    int sum = 0;
    for (int i = 0; i < hands.size(); i++) {
      int rank = i + 1;
      sum += hands.get(i).score * rank;
    }
    System.out.println("sum: " + sum);

  }
}

class Hand implements Comparable<Hand> {
  String hand;
  HandType type;
  int score;
  int jokerCount;

  public Hand(String hand, int score) {
    this.hand = hand;
    this.score = score;
    this.jokerCount = this.hand.replaceAll("[^J]", "").length();
    this.type = this.computeType();

    System.out.println(this);
  }

  public int compareTo(Hand other) {
    int cmp = type.compareTo(other.type);
    if (cmp < 0) {
      return -1;
    } else if (cmp > 0) {
      return 1;
    }

    for (int i = 0; i < hand.length(); i++) {
      int myC = charToInt(hand.charAt(i));
      int otherC = charToInt(other.hand.charAt(i));
      if (myC < otherC) {
        return -1;
      } else if (otherC < myC) {
        return 1;
      }
    }
    // Exact same hand is undefined behavior.
    System.out.println("same hand eq: " + hand);
    return 0;
  }

  public static int charToInt(char c) {
    if (Character.isDigit(c)) {
      return Character.getNumericValue(c);
    }
    switch (c) {
      case 'T':
        return 10;
      case 'J':
        return 1; // Weakest card now
      case 'Q':
        return 12;
      case 'K':
        return 13;
      case 'A':
        return 14;
      default:
        System.err.println("Unable to handle character: " + c);
        return -1;
    }
  }

  public String toString() {
    return hand + "-" + this.type + "(" + score + ")(" + jokerCount + ")";
  }

  private HandType computeType() {
    Map<Character, Integer> charCount = new HashMap<>();
    for (char c : this.hand.toCharArray()) {
      charCount.put(c, charCount.getOrDefault(c, 0) + 1);
    }

    Map<Integer, Integer> countCount = new HashMap<>();
    for (int count : charCount.values()) {
      countCount.put(count, countCount.getOrDefault(count, 0) + 1);
    }
    List<Integer> sortedCounts = new ArrayList<>();
    for (char c : charCount.keySet()) {
      if (c != 'J') {
        sortedCounts.add(charCount.get(c));
      }
    }
    Collections.sort(sortedCounts);
    int maxOneChar = (sortedCounts.size() > 0 ? sortedCounts.get(sortedCounts.size() - 1) : 0) + jokerCount;

    if (maxOneChar >= 5) {
      return HandType.FIVE_OF_A_KIND;
    }
    if (maxOneChar >= 4) {
      return HandType.FOUR_OF_A_KIND;
    }
    // If the two highest cards + jokers can make 5, that's a full house.
    if (sortedCounts.get(sortedCounts.size() - 1) + sortedCounts.get(sortedCounts.size() - 2) + jokerCount == 5) {
      return HandType.FULL_HOUSE;
    }
    if (maxOneChar == 3) {
      return HandType.THREE_OF_A_KIND;
    }
    // If the two highest cards + jokers can make 4, that's two pairs.
    if (sortedCounts.get(sortedCounts.size() - 1) + sortedCounts.get(sortedCounts.size() - 2) + jokerCount == 4) {
      return HandType.TWO_PAIR;
    }
    // If the highest card + jokers can make a pair, that's a pair.
    if (sortedCounts.get(sortedCounts.size() - 1) + jokerCount == 2) {
      return HandType.ONE_PAIR;
    }
    return HandType.HIGH_CARD;
  }
}

// Higher = better
enum HandType {
  HIGH_CARD,
  ONE_PAIR,
  TWO_PAIR,
  THREE_OF_A_KIND,
  FULL_HOUSE,
  FOUR_OF_A_KIND,
  FIVE_OF_A_KIND,
}
