package org.streampipes.pe.sources.samples;

import org.streampipes.pe.sources.samples.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * Created by riemer on 25.02.2017.
 */
public class HellaReplayLoader {

  public static final String DIRECTORY =
          "c:\\users\\riemer\\downloads\\Montrac_2017-01-19";
  public static final String OUTPUT_DIRECTORY = DIRECTORY +File.separator;
  public static final String OUTPUT_FILENAME = "hella-montrac2.csv";

  public static void main(String[] args) throws IOException {
    new HellaReplayLoader().prepareReplay();
  }

  private void prepareReplay() throws IOException {

    String line;
    File dataDir = new File(DIRECTORY);
    File[] dataFiles = dataDir.listFiles();
    File outputFile = new File(OUTPUT_DIRECTORY +OUTPUT_FILENAME);

    PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile), true);


    for (File file : dataFiles) {

      Optional<BufferedReader> readerOpt = Utils.getReader(file);
      if (readerOpt.isPresent()) {
        BufferedReader reader = readerOpt.get();
        while ((line = reader.readLine()) != null) {
          writer.append(line +"\n");
        }
      }
    }
    writer.close();
  }
}
