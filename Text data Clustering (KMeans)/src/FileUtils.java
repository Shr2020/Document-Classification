import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileUtils {

  public static String readString(String filename) throws IOException {
    return Files.readString(Path.of(filename));
  }

  public static List<String> readAllLines(String filename) throws IOException {
    return Files.readAllLines(Path.of(filename));
  }

}
