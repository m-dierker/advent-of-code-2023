import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class P1 {
  public static void main(String[] args) throws Exception {
    List<String> numbersInLines = readInput();
    int sum = 0;
    for (String str : numbersInLines) {
      int firstDigit = Character.getNumericValue(str.charAt(0));
      int lastDigit = Character.getNumericValue(str.charAt(str.length() - 1));
      sum += firstDigit * 10 + lastDigit;
    }
    System.out.println("Sum: " + sum);
  }

  /** Returns just numbers in line. */
  private static List<String> readInput() throws Exception {
    List<String> list = Files.readAllLines(Paths.get("data/1.txt"));
    return list.stream().map(str -> str.replaceAll("[^\\d]", "")).toList();

  }
}
