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

package org.apache.streampipes.export.resolver;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataSourceImportTest {
  @Test
  void importPreservesTopicAndChannelOptionsEvenWithLegacyOverrideFlag() throws Exception {
    var storage = mock(INoSqlStorage.class);
    var streams = mock(IDataStreamStorage.class);
    when(storage.getDataStreamStorage()).thenReturn(streams);
    var resolver = new DataSourceResolver() {
      @Override
      protected INoSqlStorage getNoSqlStore() {
        return storage;
      }
    };
    var grounding = new EventGrounding();
    grounding.setTopicDefinition(new SimpleTopicDefinition("original.topic"));
    grounding.setOptions(Map.of("groupId", "original-group", "offset", "earliest", "lingerMs", "3"));
    var stream = new SpDataStream();
    stream.setEventGrounding(grounding);
    var mapper = JacksonSerializer.getObjectMapper();
    var configuration = new ObjectMapper().readValue(
        "{\"overrideBrokerSettings\":true}", AssetExportConfiguration.class);
    resolver.writeDocument(mapper.writeValueAsString(resolver.modifyDocumentForExport(stream)), configuration);
    var saved = ArgumentCaptor.forClass(SpDataStream.class);
    verify(streams).persist(saved.capture());
    assertEquals("original.topic", saved.getValue().getEventGrounding().getTopicDefinition().getActualTopicName());
    assertEquals(grounding.getOptions(), saved.getValue().getEventGrounding().getOptions());
    assertFalse(mapper.writeValueAsString(configuration).contains("overrideBrokerSettings"));
  }
}
