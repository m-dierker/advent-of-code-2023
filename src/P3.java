import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class P3 {
  public static void main(String... args) throws Exception {
    char[][] input = readInput();
    boolean[][] inputsToAdd = new boolean[input.length][input[0].length];

    for (int c = 0; c < input[0].length; c++) {
      for (int r = 0; r < input.length; r++) {
        // Skip anything already traversed.
        if (inputsToAdd[r][c]) {
          continue;
        }
        char ch = input[r][c];
        // Find symbols that indicate a traversal should start.
        if (!Character.isDigit(ch) && ch != '.') {
          traverse(r - 1, c - 1, input, inputsToAdd);
          traverse(r - 1, c, input, inputsToAdd);
          traverse(r - 1, c + 1, input, inputsToAdd);
          traverse(r, c - 1, input, inputsToAdd);
          traverse(r, c + 1, input, inputsToAdd);
          traverse(r + 1, c - 1, input, inputsToAdd);
          traverse(r + 1, c, input, inputsToAdd);
          traverse(r + 1, c + 1, input, inputsToAdd);
        }
      }
    }

    int sum = 0;
    String curNum = null;
    for (int r = 0; r < input.length; r++) {
      for (int c = 0; c < input[0].length; c++) {

        if (!inputsToAdd[r][c]) {
          if (curNum != null) {
            sum += Integer.parseInt(curNum);
            curNum = null;
          }
          // Always skip this column.
          continue;
        }
        // This is a number to traverse.
        if (curNum == null) {
          curNum = "" + input[r][c];
        } else {
          curNum += input[r][c];
        }
      }
      // Check at the end of the traversal so numbers don't spill between lines.
      if (curNum != null) {
        sum += Integer.parseInt(curNum);
        curNum = null;
      }
    }
    System.out.println("Sum: " + sum);
  }

  private static void traverse(int r, int c, char[][] input, boolean[][] inputsToAdd) {
    if (r < 0 || c < 0 || r >= input.length || c >= input[0].length || inputsToAdd[r][c]
        || !Character.isDigit(input[r][c])) {
      return;
    }
    inputsToAdd[r][c] = true;
    // Numbers can only be left and right.
    traverse(r, c - 1, input, inputsToAdd);
    traverse(r, c + 1, input, inputsToAdd);
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
