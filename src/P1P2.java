import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class P1P2 {
  public static void main(String[] args) throws Exception {
    List<String> numbersInLines = readInput();
    int sum = 0;
    for (String str : numbersInLines) {
      int firstDigit = getFirstDigit(str);
      int lastDigit = getLastDigit(str);

      int amount = firstDigit * 10 + lastDigit;
      System.out.println(str + " = " + amount);
      sum += amount;
    }
    System.out.println("Sum: " + sum);
  }

  private static int getFirstDigit(String str) {
    for (int end = 1; end <= str.length(); end++) {
      String sub = str.substring(0, end);
      char last = sub.charAt(sub.length() - 1);
      if (Character.isDigit(last)) {
        return Character.getNumericValue(last);
      } else {
        int stringDigit = findDigit(sub);
        if (stringDigit != -1) {
          return stringDigit;
        }
      }
    }
    System.out.println("Warning: Never able to find a start digit in " + str);
    return -2;
  }

  private static int getLastDigit(String str) {
    for (int start = str.length() - 1; start >= 0; start--) {
      String sub = str.substring(start);
      char first = sub.charAt(0);
      if (Character.isDigit(first)) {
        return Character.getNumericValue(first);
      } else {
        int stringDigit = findDigit(sub);
        if (stringDigit != -1) {
          return stringDigit;
        }
      }
    }
    System.out.println("Warning: Never able to find a last digit in " + str);
    return -2;
  }

  private static int findDigit(String str) {
    if (str.contains("one")) {
      return 1;
    }
    if (str.contains("two")) {
      return 2;
    }
    if (str.contains("three")) {
      return 3;
    }
    if (str.contains("four")) {
      return 4;
    }
    if (str.contains("five")) {
      return 5;
    }
    if (str.contains("six")) {
      return 6;
    }
    if (str.contains("seven")) {
      return 7;
    }
    if (str.contains("eight")) {
      return 8;
    }
    if (str.contains("nine")) {
      return 9;
    }
    return -1;
  }

  /** Returns just numbers in line. */
  private static List<String> readInput() throws Exception {
    List<String> list = Files.readAllLines(Paths.get("data/1.txt"));
    return list;
  }
}
