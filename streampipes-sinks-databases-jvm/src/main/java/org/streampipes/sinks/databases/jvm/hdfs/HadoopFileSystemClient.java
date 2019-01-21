package org.streampipes.sinks.databases.jvm.hdfs;

import java.util.Map;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;

public class HadoopFileSystemClient {

  private Logger logger;

  HadoopFileSystemClient(Logger logger) {
    this.logger = logger;
  }

  void save(Map<String, Object> event) throws SpRuntimeException {
    if (event == null) {
      throw new SpRuntimeException("event is null");
    }
  }

  void stop() {

  }
}
