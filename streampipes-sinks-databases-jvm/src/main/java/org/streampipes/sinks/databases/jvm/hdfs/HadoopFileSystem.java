/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.hdfs;

import java.sql.SQLException;
import java.util.Map;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.wrapper.runtime.EventSink;

public class HadoopFileSystem extends EventSink<HadoopFileSystemParameters> {

  private HadoopFileSystemClient hadoopFileSystemClient;

  private static Logger LOG;

  public HadoopFileSystem(HadoopFileSystemParameters params) {
    super(params);
  }

  @Override
  public void bind(HadoopFileSystemParameters parameters) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(HadoopFileSystem.class);

    this.hadoopFileSystemClient = new HadoopFileSystemClient(LOG);
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    try {
      hadoopFileSystemClient.save(event);
    } catch (SpRuntimeException e) {
      //TODO: error or warn?
      LOG.error(e.getMessage());
      //e.printStackTrace();
    }
  }

  @Override
  public void discard() throws SpRuntimeException {
    hadoopFileSystemClient.stop();
  }
}
