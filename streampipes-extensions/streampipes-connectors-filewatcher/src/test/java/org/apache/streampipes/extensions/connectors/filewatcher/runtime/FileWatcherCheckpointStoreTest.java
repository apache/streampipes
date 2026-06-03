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

import org.apache.streampipes.extensions.connectors.filewatcher.model.FileFingerprint;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileGenerationState;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherCheckpoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileWatcherCheckpointStoreTest {

  @TempDir
  Path tempDir;

  @Test
  void shouldReplaceColonInCheckpointFileName() throws IOException {
    FileWatcherCheckpointStore checkpointStore = new FileWatcherCheckpointStore(tempDir);
    FileWatcherCheckpoint checkpoint = new FileWatcherCheckpoint();
    checkpoint.setCurrentFileName("Meldungsarchiv1.csv");
    checkpoint.setCurrentSequence(1);
    checkpoint.getGenerationStates().put(
        "Meldungsarchiv1.csv",
        new FileGenerationState(new FileFingerprint(123L, 456L, "hash"), 7L)
    );

    checkpointStore.save("adapter:1", checkpoint);

    Path expectedPath = tempDir.resolve("adapter_1.json");
    assertTrue(Files.exists(expectedPath));

    FileWatcherCheckpoint loadedCheckpoint = checkpointStore.load("adapter:1");
    assertEquals("Meldungsarchiv1.csv", loadedCheckpoint.getCurrentFileName());
    assertEquals(1, loadedCheckpoint.getCurrentSequence());
    assertEquals(1, loadedCheckpoint.getGenerationStates().size());
    assertEquals(
        7L,
        loadedCheckpoint.getGenerationStates().get("Meldungsarchiv1.csv").getLastProcessedRecord()
    );
  }
}
