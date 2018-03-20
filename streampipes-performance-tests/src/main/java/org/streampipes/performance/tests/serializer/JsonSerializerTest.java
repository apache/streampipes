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
 *
 */

package org.streampipes.performance.tests.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.performance.dataprovider.JsonDataProvider;
import org.streampipes.performance.dataprovider.SimpleSchemaProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonSerializerTest {

  private static final Logger LOG = LoggerFactory.getLogger(JsonSerializerTest.class);

  public static void main(String[] args) {
    List<String> data = new JsonDataProvider(new SimpleSchemaProvider().getSchema(), 1000000L).getPreparedItems();
    List<Map<String, Object>> events = new ArrayList<>();
    JsonDataFormatDefinition formatter = new JsonDataFormatDefinition();

    LOG.info("Total events: " +data.size());
    LOG.info("Starting deserializer test");
    Long startTime = System.currentTimeMillis();
    for(String item : data) {
      try {
        events.add(formatter.toMap(item.getBytes()));
      } catch (SpRuntimeException e) {
        e.printStackTrace();
      }
    }
    Long endTime = System.currentTimeMillis();

    LOG.info("Total time: " +String.valueOf(endTime - startTime));
    LOG.info("Per event: " +String.valueOf((endTime - startTime)/data.size()));

    LOG.info("Starting serializer test");
    startTime = System.currentTimeMillis();
    for(Map<String, Object> event : events) {
      try {
        formatter.fromMap(event);
      } catch (SpRuntimeException e) {
        e.printStackTrace();
      }
    }
    endTime = System.currentTimeMillis();

    LOG.info("Total time: " +String.valueOf(endTime - startTime));
    LOG.info("Per event: " +String.valueOf((endTime - startTime)/data.size()));
  }
}
