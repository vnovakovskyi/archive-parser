package course;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

final class Main {

  private Main() {

  }

  /**
   * Path of .zip file to parse.
   */
  private static final String FILE_PATH = "inputs__1_.zip";

  /**
   * Path of final .zip file.
   */
  private static final String RES_FILE_PATH = "inputsv2.zip";

  /**
   * Phone numbers after parsing.
   */
  private static Set<String> phoneNumbers = new TreeSet<>();

  /**
   * email addresses after parsing.
   */
  private static Set<String> emails = new TreeSet<>();

  /**
   * ZipOutputStream of final .zip file.
   */
  private static ZipOutputStream resZipOut;

  public static void main(final String[] args) {
    readZipFile(Paths.get(FILE_PATH));
  }

  private static void readZipFile(final Path filePath) {
    try (InputStream inputStream = Files.newInputStream(filePath)) {
      File res = new File(RES_FILE_PATH);
      resZipOut = new ZipOutputStream(new FileOutputStream(res));

      readZipFileStreamRecursive(inputStream);

      writeFile("phoneNumbers.txt", phoneNumbers);
      writeFile("emails.txt", emails);

      resZipOut.close();
    } catch (IOException e) {
      System.err.println("error reading zip file " + FILE_PATH);
    }
  }

  private static void readZipFileStreamRecursive(final InputStream is) {
    ZipInputStream zipInputStream = new ZipInputStream(is);
    ZipEntry zipEntry;
    try {
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        if (zipEntry.isDirectory()) {
          continue;
        }
        if (zipEntry.getName().endsWith(".zip")) {
          readZipFileStreamRecursive(zipInputStream);
        } else if (zipEntry.getName().endsWith(".gz")) {
          readGZipFile(zipInputStream);
        } else if (zipEntry.getName().endsWith(".txt")) {
          readTextFile(zipInputStream);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void readGZipFile(final InputStream inputStream)
          throws IOException {
    BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(
                    new GZIPInputStream(inputStream)));
    String line;
    while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
      parseText(line);
    }
  }

  private static void readTextFile(final InputStream inputStream)
          throws IOException {
    String str;
    BufferedReader reader = new BufferedReader(
            new InputStreamReader(inputStream));
    while ((str = reader.readLine()) != null && !str.isEmpty()) {
      parseText(str);
    }
  }

  private static void parseText(final String text) {
    String[] lines = text.split("[\\\\&,;\\t ?_$-]");

    String phone = "";
    for (String line : lines) {
      if (line.contains("@")) {
        if (line.endsWith(".org")) {
          emails.add(line);
        }
      } else {
        phone = phone.concat(line);
      }
    }

    phoneNumbers.add(replacePhoneCode(phone));
  }

  private static String replacePhoneCode(final String phone) {
    String result = phone;
    String sub = result.substring(result.indexOf("("), result.indexOf(")") + 1);
    if (sub.equals("(101)")) {
      result = result.replace("(101)", "(401)");
    }

    if (sub.equals("(202)")) {
      result = result.replace("(202)", "(802)");
    }

    if (sub.equals("(301)")) {
      result = result.replace("(301)", "(321)");
    }

    result = result.replace("(", " (");
    result = result.replace(")", ") ");

    return result;
  }

  private static void writeFile(final String name, final Set<String> set)
          throws IOException {
    StringBuilder sb = new StringBuilder();

    for (String s : set) {
      sb.append(s).append("\n");
    }

    ZipEntry e = new ZipEntry(name);
    resZipOut.putNextEntry(e);

    byte[] data = sb.toString().getBytes();
    resZipOut.write(data, 0, data.length);
    resZipOut.closeEntry();
  }
}
