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

import org.apache.streampipes.extensions.connectors.filewatcher.model.CsvParserSettings;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherCheckpoint;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileSetWatcherTest {

  @TempDir
  Path tempDir;

  @Test
  void shouldReadInitialFilesAndOnlyNewGenerationAfterWrapAround() throws IOException {
    write("Meldungsarchiv1", "id,value\n1,a\n2,b\n");
    write("Meldungsarchiv2", "id,value\n3,c\n");

    FileSetWatcher watcher = new FileSetWatcher(
        new FileWatcherConfig(tempDir, Pattern.compile("Meldungsarchiv(\\d+)"), new CsvParserSettings(true, ','), 1, false, 0),
        new FileWatcherCheckpointStore(tempDir.resolve("checkpoints")),
        new CsvFileReader(),
        EventMapper.identity()
    );

    List<Map<String, Object>> events = new ArrayList<>();
    watcher.poll("adapter-1", events::add);

    assertEquals(3, events.size());
    assertEquals("1", events.get(0).get("id"));
    assertEquals("3", events.get(2).get("id"));

    events.clear();
    watcher.poll("adapter-1", events::add);
    assertEquals(0, events.size());

    write("Meldungsarchiv1", "id,value\n4,d\n5,e\n");
    watcher.poll("adapter-1", events::add);

    assertEquals(2, events.size());
    assertEquals("4", events.get(0).get("id"));
    assertEquals("5", events.get(1).get("id"));
  }

  @Test
  void shouldResumeWithinSameGenerationFromCheckpoint() throws IOException {
    write("Meldungsarchiv1", "id,value\n1,a\n2,b\n3,c\n");
    Path checkpointDir = tempDir.resolve("checkpoints");
    FileWatcherCheckpointStore checkpointStore = new FileWatcherCheckpointStore(checkpointDir);

    FileSetWatcher watcher = new FileSetWatcher(
        new FileWatcherConfig(tempDir, Pattern.compile("Meldungsarchiv(\\d+)"), new CsvParserSettings(true, ','), 1, false, 0),
        checkpointStore,
        new CsvFileReader(),
        EventMapper.identity()
    );

    FileSlot slot = watcher.discoverFiles().get(0);
    FileWatcherCheckpoint checkpoint = new FileWatcherCheckpoint();
    checkpoint.setCurrentFileName(slot.fileName());
    checkpoint.setCurrentSequence(slot.sequence());
    checkpoint.setCurrentFingerprint(slot.fingerprint());
    checkpoint.setLastProcessedRecord(1L);
    checkpoint.getProcessedGenerations().put(slot.fileName(), slot.fingerprint());
    checkpointStore.save("adapter-2", checkpoint);

    List<Map<String, Object>> events = new ArrayList<>();
    watcher.poll("adapter-2", events::add);

    assertEquals(1, events.size());
    assertEquals("3", events.get(0).get("id"));
  }

  @Test
  void shouldContinueFromLastLineWhenSingleFileGrows() throws IOException {
    write("Meldungsarchiv.csv", "id,value\n1,a\n2,b\n");

    FileSetWatcher watcher = new FileSetWatcher(
        new FileWatcherConfig(tempDir, Pattern.compile("Meldungsarchiv\\.csv"), new CsvParserSettings(true, ','), 1, true, 0),
        new FileWatcherCheckpointStore(tempDir.resolve("checkpoints-single")),
        new CsvFileReader(),
        EventMapper.identity()
    );

    List<Map<String, Object>> events = new ArrayList<>();
    watcher.poll("adapter-single", events::add);

    assertEquals(2, events.size());
    events.clear();

    write("Meldungsarchiv.csv", "id,value\n1,a\n2,b\n3,c\n4,d\n");
    watcher.poll("adapter-single", events::add);

    assertEquals(2, events.size());
    assertEquals("3", events.get(0).get("id"));
    assertEquals("4", events.get(1).get("id"));
  }

  @Test
  void shouldRestartFromBeginningWhenSingleFileIsTruncatedOrReplaced() throws IOException {
    write("Meldungsarchiv.csv", "id,value\n1,a\n2,b\n3,c\n");

    FileSetWatcher watcher = new FileSetWatcher(
        new FileWatcherConfig(tempDir, Pattern.compile("Meldungsarchiv\\.csv"), new CsvParserSettings(true, ','), 1, true, 0),
        new FileWatcherCheckpointStore(tempDir.resolve("checkpoints-replace")),
        new CsvFileReader(),
        EventMapper.identity()
    );

    watcher.poll("adapter-replace", event -> {
    });

    List<Map<String, Object>> events = new ArrayList<>();
    write("Meldungsarchiv.csv", "id,value\n10,x\n11,y\n");
    watcher.poll("adapter-replace", events::add);

    assertEquals(2, events.size());
    assertEquals("10", events.get(0).get("id"));
    assertEquals("11", events.get(1).get("id"));
  }

  @Test
  void shouldDelayBetweenEventsWhenConfigured() throws IOException {
    write("Meldungsarchiv1", "id,value\n1,a\n2,b\n3,c\n");
    AtomicInteger delayCalls = new AtomicInteger();
    List<Long> appliedDelays = new ArrayList<>();

    FileSetWatcher watcher = new FileSetWatcher(
        new FileWatcherConfig(tempDir, Pattern.compile("Meldungsarchiv(\\d+)"), new CsvParserSettings(true, ','), 1, false, 5),
        new FileWatcherCheckpointStore(tempDir.resolve("checkpoints-delay")),
        new CsvFileReader(),
        EventMapper.identity(),
        delayMs -> {
          delayCalls.incrementAndGet();
          appliedDelays.add(delayMs);
        }
    );

    List<Map<String, Object>> events = new ArrayList<>();
    watcher.poll("adapter-delay", events::add);

    assertEquals(3, events.size());
    assertEquals(2, delayCalls.get());
    assertEquals(List.of(5L, 5L), appliedDelays);
  }

  private void write(String fileName, String content) throws IOException {
    Files.writeString(tempDir.resolve(fileName), content);
  }
}
