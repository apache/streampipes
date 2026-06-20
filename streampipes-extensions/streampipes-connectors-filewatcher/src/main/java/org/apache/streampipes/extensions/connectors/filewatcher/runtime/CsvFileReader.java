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

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.connectors.filewatcher.model.CsvParserSettings;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class CsvFileReader {

  private static final Logger LOG = LoggerFactory.getLogger(CsvFileReader.class);

  private static final List<Charset> FALLBACK_CHARSETS = List.of(
      StandardCharsets.UTF_8,
      Charset.forName("windows-1252"),
      StandardCharsets.ISO_8859_1
  );

  public FileWatchReadResult readFrom(Path path,
                                      CsvParserSettings parserSettings,
                                      long startRecordIndex,
                                      BiConsumer<Long, Map<String, Object>> eventConsumer) throws IOException {
    IOException lastException = null;
    for (Charset charset : FALLBACK_CHARSETS) {
      try {
        LOG.debug("Trying to read CSV file '{}' with charset '{}'.", path.getFileName(), charset.name());
        return readFrom(path, parserSettings, startRecordIndex, eventConsumer, charset);
      } catch (MalformedInputException e) {
        LOG.debug("Could not decode CSV file '{}' with charset '{}': {}.",
            path.getFileName(), charset.name(), e.getMessage());
        lastException = e;
      }
    }

    throw new IOException("Could not decode CSV file " + path.getFileName() + " using supported charsets.", lastException);
  }

  private FileWatchReadResult readFrom(Path path,
                                       CsvParserSettings parserSettings,
                                       long startRecordIndex,
                                       BiConsumer<Long, Map<String, Object>> eventConsumer,
                                       Charset charset) throws IOException {
    try (var bufferedReader = Files.newBufferedReader(path, charset);
         var csvReader = newCsvReader(bufferedReader, parserSettings)) {
      LOG.debug("Reading CSV file '{}' using charset '{}'.", path.getFileName(), charset.name());

      String[] header = parserSettings.hasHeader() ? csvReader.readNext() : null;
      String[] row;
      long currentRecordIndex = -1L;
      long lastProcessedRecord = startRecordIndex - 1;

      while ((row = csvReader.readNext()) != null) {
        currentRecordIndex++;

        if (header == null) {
          header = IntStream.range(0, row.length)
              .mapToObj(i -> "key_" + i)
              .toArray(String[]::new);
        }

        if (currentRecordIndex < startRecordIndex) {
          continue;
        }

        eventConsumer.accept(currentRecordIndex, toMap(header, row));
        lastProcessedRecord = currentRecordIndex;
      }

      int emittedEvents = lastProcessedRecord < startRecordIndex
          ? 0
          : (int) (lastProcessedRecord - startRecordIndex + 1);

      LOG.debug("Completed reading CSV file '{}'. Emitted {} records starting from record {}.",
          path.getFileName(), emittedEvents, startRecordIndex);

      return new FileWatchReadResult(lastProcessedRecord, emittedEvents, true);
    } catch (CsvValidationException e) {
      throw new ParseException("Could not parse CSV file " + path.getFileName(), e);
    }
  }

  private CSVReader newCsvReader(BufferedReader reader, CsvParserSettings parserSettings) {
    var csvParser = new CSVParserBuilder()
        .withSeparator(parserSettings.delimiter())
        .build();

    return new CSVReaderBuilder(reader)
        .withSkipLines(0)
        .withCSVParser(csvParser)
        .build();
  }

  private Map<String, Object> toMap(String[] header, String[] row) {
    Map<String, Object> event = new LinkedHashMap<>();
    for (int i = 0; i < header.length; i++) {
      event.put(header[i], i < row.length ? row[i] : "");
    }

    if (row.length > header.length && header.length > 0) {
      String lastHeader = header[header.length - 1];
      String mergedValue = String.valueOf(event.get(lastHeader));
      for (int i = header.length; i < row.length; i++) {
        mergedValue = mergedValue + row[i];
      }
      event.put(lastHeader, mergedValue);
    }

    return event;
  }
}
