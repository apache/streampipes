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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvFileReaderTest {

  @TempDir
  Path tempDir;

  @Test
  void shouldKeepQuotedSeparatorsInsideOneField() throws IOException {
    Path file = tempDir.resolve("Meldungsarchiv1.csv");
    Files.writeString(file, "Time_ms;MsgText;PLC\n46120366239;\"Alarm; text\";PLC_1\n");

    List<Map<String, Object>> events = new ArrayList<>();
    new CsvFileReader().readFrom(file, new CsvParserSettings(true, ';'), 0, (recordIndex, event) -> events.add(event));

    assertEquals(1, events.size());
    assertEquals("Alarm; text", events.get(0).get("MsgText"));
  }

  @Test
  void shouldPadMissingColumnsWithEmptyStrings() throws IOException {
    Path file = tempDir.resolve("Meldungsarchiv1.csv");
    Files.writeString(file, "Time_ms;MsgText;PLC\n46120366239;Alarm text\n");

    List<Map<String, Object>> events = new ArrayList<>();
    new CsvFileReader().readFrom(file, new CsvParserSettings(true, ';'), 0, (recordIndex, event) -> events.add(event));

    assertEquals(1, events.size());
    assertEquals("", events.get(0).get("PLC"));
  }

  @Test
  void shouldFallbackToWindows1252ForWinCCExports() throws IOException {
    Path file = tempDir.resolve("Meldungsarchiv1.csv");
    Files.write(
        file,
        "Time_ms;MsgText;PLC\n46120366239;Störung Überfüllung;PLC_1\n".getBytes(Charset.forName("windows-1252"))
    );

    List<Map<String, Object>> events = new ArrayList<>();
    new CsvFileReader().readFrom(file, new CsvParserSettings(true, ';'), 0, (recordIndex, event) -> events.add(event));

    assertEquals(1, events.size());
    assertEquals("Störung Überfüllung", events.get(0).get("MsgText"));
  }
}
