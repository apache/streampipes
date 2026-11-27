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

package org.apache.streampipes.extensions.connectors.filewatcher.adapter;

import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileSetWatcher;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileWatcherCheckpointStore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatedTextLogReaderTest {

  @TempDir
  Path directory;

  private final DatedTextLogReader reader = new DatedTextLogReader(DatedTextLogReader.DEFAULT_FILE_PATTERN,
      "uuuu_MM_dd", DatedTextLogReader.DEFAULT_LINE_PATTERN, DatedTextLogReader.DEFAULT_FIELDS,
      "HH:mm:ss", "UTF-16LE", "Europe/Berlin");

  private FileSetWatcher watcher() {
    return new FileSetWatcher(directory, reader.filePattern, name -> reader.date(name).toEpochDay(),
        reader, new FileWatcherCheckpointStore(directory.resolve("state")));
  }

  @Test
  void shouldSampleThenResumeIncompleteLineAfterRestartAndDetectReplacement() throws IOException {
    Path file = directory.resolve("prefix_2026_10_29.log");
    String first = "\uFEFF06:12:00, CNC 1, Channel 0, CNCWARNING, Errornumber 55, 1, Störung, Achse 1";
    String second = "\uFEFF06:13:00, PLC, Errornumber 2, 0, OK";
    Files.writeString(file, first + "\r\n" + second, StandardCharsets.UTF_16LE);
    var expected = Map.of("timestamp", Instant.parse("2026-10-29T05:12:00Z").toEpochMilli(),
        "source", "CNC 1, Channel 0, CNCWARNING", "error_number", 55, "state", 1, "message", "Störung, Achse 1");
    assertEquals(expected, reader.sample(file).orElseThrow());
    var events = new ArrayList<Map<String, Object>>();
    watcher().poll("test", events::add);
    assertEquals(List.of(expected), events);
    events.clear();
    watcher().poll("test", events::add);
    assertTrue(events.isEmpty());

    Files.writeString(file, "\n", StandardCharsets.UTF_16LE, StandardOpenOption.APPEND);
    watcher().poll("test", events::add);
    assertEquals(1, events.size());
    assertEquals(2, events.getFirst().get("error_number"));
    events.clear();
    Files.writeString(file, first + "\n", StandardCharsets.UTF_16LE);
    watcher().poll("test", events::add);
    assertEquals(List.of(expected), events);
  }

  @Test
  void shouldKeepOnlyLatestDailyCheckpointAndIgnoreLateFilesAfterRestart() throws IOException {
    var store = new FileWatcherCheckpointStore(directory.resolve("state"));
    String line = "06:00:00, PLC, Errornumber 2, 0, OK";
    var first = directory.resolve("prefix_2026_10_28.log");
    var next = directory.resolve("prefix_2026_10_29.log");
    Files.writeString(first, line + "\n" + line, StandardCharsets.UTF_16LE);
    Files.writeString(next, line + "\n", StandardCharsets.UTF_16LE);
    var events = new ArrayList<Map<String, Object>>();
    watcher().poll("test", events::add);
    assertEquals(1, events.size());
    Files.writeString(first, "\n", StandardCharsets.UTF_16LE, StandardOpenOption.APPEND);
    watcher().poll("test", events::add);
    assertEquals(3, events.size());
    assertEquals(List.of(next.getFileName().toString()), List.copyOf(store.load("test").getGenerationStates().keySet()));

    Files.writeString(first, "invalid old record\n", StandardCharsets.UTF_16LE, StandardOpenOption.APPEND);
    Files.writeString(directory.resolve("prefix_2026_10_27.log"), line + "\n", StandardCharsets.UTF_16LE);
    Files.delete(next);
    watcher().poll("test", events::add);
    assertEquals(3, events.size());
    var later = directory.resolve("prefix_2026_10_30.log");
    Files.writeString(later, line + "\n", StandardCharsets.UTF_16LE);
    watcher().poll("test", events::add);
    assertEquals(4, events.size());
    assertEquals(List.of(later.getFileName().toString()), List.copyOf(store.load("test").getGenerationStates().keySet()));
  }

  @Test
  void shouldSupportDifferentFilenamesFieldLayoutAndEncoding() throws IOException {
    var custom = new DatedTextLogReader("events-(\\d{8})\\.txt", "ddMMuuuu", "(.*)\\|(.*)\\|(.*)",
        "sensor:string,value:double,observed:time", "HHmmss", "UTF-8", "UTC");
    var file = directory.resolve("events-29062026.txt");
    Files.writeString(file, "temp|12.25|125901\n");
    assertEquals(Map.of("sensor", "temp", "value", 12.25, "observed", Instant.parse("2026-06-29T12:59:01Z").toEpochMilli()),
        custom.sample(file).orElseThrow());
  }

  @Test
  void shouldReportMalformedCompleteLineWithoutCheckpointingPastIt() throws IOException {
    var file = directory.resolve("prefix_2026_10_29.log");
    Files.writeString(file, "00:00:00, PLC, Errornumber 1, 0, OK\nbroken\n", StandardCharsets.UTF_16LE);
    var events = new ArrayList<Map<String, Object>>();
    assertThrows(IOException.class, () -> watcher().poll("test", events::add));
    assertThrows(IOException.class, () -> watcher().poll("test", events::add));
    assertEquals(1, events.size());
  }
}
