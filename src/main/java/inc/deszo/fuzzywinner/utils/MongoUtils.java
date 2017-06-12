package inc.deszo.fuzzywinner.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MongoUtils {

  public static String getJSFile(String path) throws IOException {

    StringBuilder text = new StringBuilder();
    BufferedReader br = new BufferedReader(new FileReader(
        new File(path)));
    try {
      while (true) {
        String line = br.readLine();
        if (line == null) {
          break;
        }
        text.append(line).append("\n");
      }
    } finally {
      try {
        br.close();
      } catch (Exception ignore) {
      }
    }

    return text.toString();
  }
}

