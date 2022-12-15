import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class FileUtils {

  public static String readString(String filename) throws IOException {
    return Files.readString(Path.of(filename));
  }

  public static String readFile(String path) {
    List<String> filecontents = new ArrayList<String>();
    try {
        File file = new File(path);
        Scanner myReader = new Scanner(new FileInputStream(file));
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            filecontents.add(data);
        }
        myReader.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    String listString = filecontents.toString();
    listString = listString.substring(1, listString.length() - 1);
    return listString;
  }


  public static List<String> readAllLines(String filename) throws IOException {
    return Files.readAllLines(Path.of(filename));
  }

}
