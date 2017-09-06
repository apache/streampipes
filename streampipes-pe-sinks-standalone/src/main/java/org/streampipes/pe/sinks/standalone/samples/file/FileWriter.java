package org.streampipes.pe.sinks.standalone.samples.file;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FileWriter implements EventSink<FileParameters> {

  private static final Logger LOG = LoggerFactory.getLogger(FileWriter.class);

  private FileOutputStream stream;
  private long counter = 0;
  private boolean firstEvent;

  private void prepare(String path) {
    File file = new File(path);
    try {
      stream = FileUtils.openOutputStream(file);
    } catch (IOException e) {
      LOG.error(e.getMessage());
    }

    firstEvent = true;
  }

  @Override
  public void bind(FileParameters params) throws SpRuntimeException {
    prepare(params.getPath());
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    counter++;
    try {
      if (firstEvent) {
        stream.write(makeRow(event.keySet()).getBytes());
        firstEvent = false;
      }
      stream.write(makeRow(event.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet())).getBytes());
      if (counter % 10000 == 0) {
        LOG.info(counter + " Event processed.");
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }

  private <T> String makeRow(Set<T> objects) {
    String result = "";
    for (Object o : objects) {
      result = String.valueOf(o) + ",";
    }
    return result.substring(0, result.length() - 1) + "\n";
  }

  @Override
  public void discard() throws SpRuntimeException {
    try {
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
