import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class P3P2 {
  public static void main(String... args) throws Exception {
    char[][] input = readInput();
    boolean[][] isPartNumber = new boolean[input.length][input[0].length];

    for (int c = 0; c < input[0].length; c++) {
      for (int r = 0; r < input.length; r++) {
        // Skip anything already traversed.
        if (isPartNumber[r][c]) {
          continue;
        }
        char ch = input[r][c];
        // Find symbols that indicate a traversal should start.
        if (!Character.isDigit(ch) && ch != '.') {
          traverse(r - 1, c - 1, input, isPartNumber);
          traverse(r - 1, c, input, isPartNumber);
          traverse(r - 1, c + 1, input, isPartNumber);
          traverse(r, c - 1, input, isPartNumber);
          traverse(r, c + 1, input, isPartNumber);
          traverse(r + 1, c - 1, input, isPartNumber);
          traverse(r + 1, c, input, isPartNumber);
          traverse(r + 1, c + 1, input, isPartNumber);
        }
      }
    }

    int sum = 0;
    for (int r = 0; r < input.length; r++) {
      for (int c = 0; c < input[0].length; c++) {
        if (input[r][c] == '*') {
          int[] partNums = new int[] { getPartNumCount(r - 1, c - 1, input, isPartNumber),
              getPartNumCount(r - 1, c, input, isPartNumber),
              getPartNumCount(r - 1, c + 1, input, isPartNumber),
              getPartNumCount(r, c - 1, input, isPartNumber),
              getPartNumCount(r, c + 1, input, isPartNumber),
              getPartNumCount(r + 1, c - 1, input, isPartNumber),
              getPartNumCount(r + 1, c, input, isPartNumber),
              getPartNumCount(r + 1, c + 1, input, isPartNumber) };
          int[] nonZeroNums = Arrays.stream(partNums).filter(num -> num != 0).toArray();
          System.out.println("nonZeroNums: " + Arrays.toString(nonZeroNums));
          if (nonZeroNums.length == 2) {
            sum += nonZeroNums[0] * nonZeroNums[1];
          }
        }
      }
    }
    System.out.println("Sum: " + sum);
  }

  /** Returns number if part num, 0 if not. */
  private static int getPartNumCount(int r, int c, char[][] input, boolean[][] isPartNumber) {
    if (r < 0 || c < 0 || r >= input.length || c >= input[0].length || !isPartNumber[r][c]) {
      return 0;
    }
    System.out.println("Looking at part number " + r + " " + c);

    // Parse the partNum.
    int partStartIdx = c;
    while (partStartIdx >= 0 && Character.isDigit(input[r][partStartIdx])) {
      partStartIdx--;
    }
    int partEndIdx = c;
    while (partEndIdx < input[0].length && Character.isDigit(input[r][partEndIdx])) {
      partEndIdx++;
    }
    System.out.println(partStartIdx + " " + partEndIdx);
    String partNumStr = "";
    for (int i = partStartIdx + 1; i <= partEndIdx - 1; i++) {
      partNumStr += input[r][i];
    }
    System.out.println(partNumStr);

    // HIGHLY ILLEGAL. THIS IS BAD AND I SHOULD FEEL BAD.
    zeroOutPartNum(r, c, isPartNumber);

    return Integer.parseInt(partNumStr);
  }

  private static void zeroOutPartNum(int r, int c, boolean[][] isPartNumber) {
    if (r < 0 || c < 0 || r >= isPartNumber.length || c >= isPartNumber[0].length || !isPartNumber[r][c]) {
      return;
    }
    isPartNumber[r][c] = false;
    zeroOutPartNum(r, c - 1, isPartNumber);
    zeroOutPartNum(r, c + 1, isPartNumber);
  }

  private static void traverse(int r, int c, char[][] input, boolean[][] isPartNumber) {
    if (r < 0 || c < 0 || r >= input.length || c >= input[0].length || isPartNumber[r][c]
        || !Character.isDigit(input[r][c])) {
      return;
    }
    isPartNumber[r][c] = true;
    // Numbers can only be left and right.
    traverse(r, c - 1, input, isPartNumber);
    traverse(r, c + 1, input, isPartNumber);
  }

  private static char[][] readInput() throws Exception {
    List<String> lines = Files.readAllLines(Paths.get("./data/3.txt"));
    char[][] input = new char[lines.size()][];
    for (int i = 0; i < lines.size(); i++) {
      input[i] = lines.get(i).toCharArray();
    }
    return input;
  }
}
