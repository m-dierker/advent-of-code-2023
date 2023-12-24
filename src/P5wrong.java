import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class P5 {
  public static void main(String... args) throws Exception {
    Scanner in = new Scanner(new File("./data/5.txt"));

    String seeds = in.nextLine();
    in.nextLine(); // blank line

    Map<String, Map<Long, Long>> map = new HashMap<>();

    while (in.hasNext()) {
      String mapName = in.nextLine().trim();
      System.out.println("Building " + mapName);
      Map<Long, Long> subMap = new HashMap<>();
      map.put(mapName, subMap);

      // Within a map, parse data.
      while (in.hasNext()) {
        String line = in.nextLine();
        if (line == "") {
          // System.out.println("subMap" + mapName + " " + subMap + "\n\n");
          break;
        }

        List<Long> mapEntry = Arrays.stream(line.split(" ")).map(str -> Long.parseLong(str)).toList();

        long dest = mapEntry.get(0);
        long src = mapEntry.get(1);
        long cnt = mapEntry.get(2);

        for (long i = 0; i < cnt; i++) {
          subMap.put(src + i, dest + i);
        }
      }
    }

    Set<Long> vals = Arrays
        .stream(Pattern.compile("seeds: (.*)").matcher(seeds).results().map(m -> m.group(1)).findFirst().orElseThrow()
            .split(" "))
        .map(str -> Long.parseLong(str))
        .collect(Collectors.toSet());

    vals = transform(vals, map, "seed-to-soil map:");
    // System.out.println("first vals" + vals);

    vals = transform(vals, map, "soil-to-fertilizer map:");
    vals = transform(vals, map, "fertilizer-to-water map:");
    vals = transform(vals, map, "water-to-light map:");
    vals = transform(vals, map, "light-to-temperature map:");
    vals = transform(vals, map, "temperature-to-humidity map:");
    vals = transform(vals, map, "humidity-to-location map:");

    System.out.println("min: " + vals.stream().reduce(Long.MAX_VALUE, (v1, v2) -> Math.min(v1, v2)).longValue());

  }

  private static Set<Long> transform(Set<Long> vals, Map<String, Map<Long, Long>> map, String mapName) {
    Map<Long, Long> subMap = map.get(mapName);
    // System.out.println("mapName: " + mapName + " " + subMap);
    return vals.stream().map(val -> subMap.getOrDefault(val, val)).collect(Collectors.toSet());
  }
}

class TreeNode {
  long minSrc;
  long maxSrc;

  long minDest;

  // left.max = min - 1
  TreeNode left;

  // right.min = max + 1
  TreeNode right;
}