/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.extensions.connectors.filewatcher.runtime;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherCheckpoint;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileWatcherCheckpointStore {

  private static final ObjectMapper MAPPER = JacksonSerializer.getObjectMapper();
  private final Path baseDirectory;

  public FileWatcherCheckpointStore() {
    this(resolveDefaultDirectory());
  }

  public FileWatcherCheckpointStore(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public FileWatcherCheckpoint load(String adapterElementId) throws IOException {
    Path checkpointPath = checkpointPath(adapterElementId);
    if (!Files.exists(checkpointPath)) {
      return new FileWatcherCheckpoint();
    }

    return MAPPER.readValue(checkpointPath.toFile(), FileWatcherCheckpoint.class);
  }

  public void save(String adapterElementId, FileWatcherCheckpoint checkpoint) throws IOException {
    Files.createDirectories(baseDirectory);
    MAPPER.writeValue(checkpointPath(adapterElementId).toFile(), checkpoint);
  }

  private Path checkpointPath(String adapterElementId) {
    return baseDirectory.resolve(adapterElementId + ".json");
  }

  private static Path resolveDefaultDirectory() {
    String baseDir = Environments
        .getEnvironment()
        .getExtAssetBaseDir()
        .getValueOrReturn(System.getProperty("user.home"));

    return Path.of(baseDir, ".streampipes", "service", "filewatcher-checkpoints");
  }
}
