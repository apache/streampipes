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

import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileRecordReader;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileWatchReadResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

class DatedTextLogReader implements FileRecordReader {

  static final String DEFAULT_FILE_PATTERN = "prefix_(\\d{4}_\\d{2}_\\d{2})\\.log";
  static final String DEFAULT_LINE_PATTERN =
      "(\\d{2}:\\d{2}:\\d{2}),\\s*(.+?),\\s*Errornumber\\s+(\\d+),\\s*(\\d+),\\s*(.*)";
  static final String DEFAULT_FIELDS = "timestamp:time,source:string,error_number:integer,state:integer,message:string";

  private record Field(String name, String type) {
  }

  final Pattern filePattern;
  private final Pattern linePattern;
  private final DateTimeFormatter dateFormat;
  private final DateTimeFormatter timeFormat;
  private final Charset charset;
  private final ZoneId zone;
  private final List<Field> fields = new ArrayList<>();

  DatedTextLogReader(String fileExpression, String dateFormat, String lineExpression, String fieldList,
                     String timeFormat, String charset, String zone) {
    this.filePattern = Pattern.compile(fileExpression);
    this.linePattern = Pattern.compile(lineExpression);
    this.dateFormat = formatter(dateFormat);
    this.timeFormat = formatter(timeFormat);
    this.charset = Charset.forName(charset);
    this.zone = ZoneId.of(zone);
    if (filePattern.matcher("").groupCount() != 1) {
      throw new IllegalArgumentException("The filename expression must capture exactly one date.");
    }
    var names = new HashSet<String>();
    int timeFields = 0;
    for (String spec : fieldList.split(",", -1)) {
      String[] parts = spec.trim().split(":", -1);
      if (parts.length != 2 || !parts[0].matches("[A-Za-z_][A-Za-z0-9_]*") || !names.add(parts[0])
          || !List.of("time", "string", "integer", "long", "double").contains(parts[1])) {
        throw new IllegalArgumentException("Invalid or duplicate field specification: " + spec);
      }
      fields.add(new Field(parts[0], parts[1]));
      if (parts[1].equals("time")) {
        timeFields++;
      }
    }
    if (timeFields != 1 || fields.size() != linePattern.matcher("").groupCount()) {
      throw new IllegalArgumentException("Provide one field per capture group, including exactly one time field.");
    }
  }

  private static DateTimeFormatter formatter(String pattern) {
    return DateTimeFormatter.ofPattern(pattern, Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
  }

  LocalDate date(String filename) {
    var match = filePattern.matcher(filename);
    if (!match.matches()) {
      throw new IllegalArgumentException("Unexpected log filename: " + filename);
    }
    try {
      return LocalDate.parse(match.group(1), dateFormat);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException("Invalid date in log filename " + filename, e);
    }
  }

  Optional<Map<String, Object>> sample(Path path) throws IOException {
    var sample = new ArrayList<Map<String, Object>>(1);
    read(path, 0, (index, event) -> sample.add(event), true);
    return sample.stream().findFirst();
  }

  @Override
  public FileWatchReadResult readFrom(Path path, long start, BiConsumer<Long, Map<String, Object>> consumer)
      throws IOException {
    return read(path, start, consumer, false);
  }

  private FileWatchReadResult read(Path path, long start, BiConsumer<Long, Map<String, Object>> consumer,
                                   boolean firstOnly) throws IOException {
    LocalDate date = date(path.getFileName().toString());
    long index = 0;
    int emitted = 0;
    try (var input = new BufferedReader(new InputStreamReader(Files.newInputStream(path), charset))) {
      var line = new StringBuilder();
      int ch;
      while ((ch = input.read()) != -1) {
        if (ch != '\n') {
          if (index >= start) {
            line.append((char) ch);
          }
          continue;
        }
        if (index >= start) {
          if (!line.isEmpty() && line.charAt(line.length() - 1) == '\r') {
            line.setLength(line.length() - 1);
          }
          if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
            line.deleteCharAt(0);
          }
          if (!line.isEmpty()) {
            consumer.accept(index, parse(line, date, path, index));
            emitted++;
            if (firstOnly) {
              return new FileWatchReadResult(index, emitted, false);
            }
          }
          line.setLength(0);
        }
        index++;
      }
      return new FileWatchReadResult(index - 1, emitted, line.isEmpty());
    }
  }

  private Map<String, Object> parse(CharSequence line, LocalDate date, Path path, long index) throws IOException {
    var match = linePattern.matcher(line);
    if (!match.matches()) {
      throw new IOException("Line " + (index + 1) + " in " + path + " does not match the line expression.");
    }
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i);
      String value = match.group(i + 1);
      try {
        if (value == null) {
          throw new IllegalArgumentException("Capture group did not participate in match");
        }
        Object typed = switch (field.type()) {
          case "time" -> date.atTime(LocalTime.parse(value, timeFormat)).atZone(zone).toInstant().toEpochMilli();
          case "integer" -> Integer.valueOf(value);
          case "long" -> Long.valueOf(value);
          case "double" -> Double.valueOf(value);
          default -> value;
        };
        event.put(field.name(), typed);
      } catch (IllegalArgumentException | DateTimeException e) {
        throw new IOException("Invalid field " + field.name() + " at " + path + " line " + (index + 1), e);
      }
    }
    return event;
  }
}
