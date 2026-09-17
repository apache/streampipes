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

package org.apache.streampipes.service.core.migrations.v099;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ExtractBrokerConfigurationMigrationTest {
  private static final String LEGACY = """
      {"_id":"resource","_rev":"1-r","eventGrounding":{"transportProtocols":[{
      "@class":"org.apache.streampipes.model.grounding.NatsTransportProtocol","brokerHostname":"old",
      "topicDefinition":{"@class":"org.apache.streampipes.model.grounding.SimpleTopicDefinition",
      "actualTopicName":"original.topic"}}]},"schema":{"unchanged":true}}
      """;

  private GroundingMigrationStorage storage() throws IOException {
    var storage = mock(GroundingMigrationStorage.class);
    when(storage.collections()).thenReturn(List.of("data-stream"));
    when(storage.readPage("data-stream", null, 100)).thenReturn(List.of(LEGACY));
    when(storage.readPage("data-stream", "resource", 100)).thenReturn(List.of());
    return storage;
  }

  @Test
  void revisionConflictReReadsAndPreservesConcurrentChanges() throws IOException {
    var storage = storage();
    var updated = LEGACY.replace("1-r", "2-r").replace("unchanged", "concurrent");
    when(storage.read("data-stream", "resource")).thenReturn(updated);
    when(storage.update(eq("data-stream"), eq("resource"), anyString())).thenReturn(false, true);
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertTrue(migration.shouldExecute());
    migration.executeMigration();
    var argument = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(storage, times(2)).update(eq("data-stream"), eq("resource"), argument.capture());
    String finalDocument = argument.getValue();
    assertTrue(finalDocument.contains("concurrent"));
    assertTrue(finalDocument.contains("2-r"));
    assertTrue(finalDocument.contains("original.topic"));
    assertFalse(finalDocument.contains("brokerHostname"));
    var order = inOrder(storage);
    order.verify(storage).readPage("data-stream", "resource", 100);
    order.verify(storage).markCompleted();
    when(storage.isCompleted()).thenReturn(true);
    assertFalse(migration.shouldExecute());
  }

  @Test
  void partialFailurePropagatesAndRestartContinuesOnlyRemainingDocuments() throws IOException {
    var storage = storage();
    when(storage.update(eq("data-stream"), eq("resource"), anyString())).thenThrow(new IOException("unavailable"));
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertThrows(IllegalStateException.class, migration::executeMigration);
    verify(storage, never()).markCompleted();
    assertTrue(migration.shouldExecute());
  }

  @Test
  void conflictingTopicFailsWithoutWritingResource() throws IOException {
    var storage = storage();
    var document = JsonParser.parseString(LEGACY).getAsJsonObject();
    document.getAsJsonObject("eventGrounding").add("topicDefinition", JsonParser.parseString("{}"));
    when(storage.readPage("data-stream", null, 100)).thenReturn(List.of(document.toString()));
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertThrows(IllegalStateException.class, migration::executeMigration);
    verify(storage, never()).markCompleted();
    verify(storage, times(0)).update(anyString(), anyString(), anyString());
  }

  @Test
  void repeatedConflictsStopStartupAfterBoundedRetries() throws IOException {
    var storage = storage();
    when(storage.read("data-stream", "resource")).thenReturn(LEGACY);
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertThrows(IllegalStateException.class, migration::executeMigration);
    verify(storage, never()).markCompleted();
    verify(storage, times(3)).update(anyString(), anyString(), anyString());
  }

  @Test
  void completedMigrationDoesNotScanDocuments() throws IOException {
    var storage = mock(GroundingMigrationStorage.class);
    when(storage.isCompleted()).thenReturn(true);
    assertFalse(new ExtractBrokerConfigurationMigration(storage, "nats").shouldExecute());
    verify(storage).isCompleted();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void emptyDatabaseIsMarkedCompleted() throws IOException {
    var storage = mock(GroundingMigrationStorage.class);
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertTrue(migration.shouldExecute());
    migration.executeMigration();
    verify(storage).markCompleted();
  }

  @Test
  void alreadyConvertedDocumentsAreNotRewrittenButMigrationIsMarkedCompleted() throws IOException {
    var storage = storage();
    when(storage.readPage("data-stream", null, 100))
        .thenReturn(List.of("{\"_id\":\"resource\",\"eventGrounding\":{}}"));
    new ExtractBrokerConfigurationMigration(storage, "nats").executeMigration();
    verify(storage, never()).update(anyString(), anyString(), anyString());
    verify(storage).markCompleted();
  }

  @Test
  void markerReadFailureStopsStartupWithoutScanning() throws IOException {
    var storage = mock(GroundingMigrationStorage.class);
    when(storage.isCompleted()).thenThrow(new IOException("unavailable"));
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertThrows(IllegalStateException.class, migration::shouldExecute);
    verify(storage).isCompleted();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void markerWriteFailureStopsStartupAndAllowsRetry() throws IOException {
    var storage = mock(GroundingMigrationStorage.class);
    doThrow(new IOException("unavailable")).when(storage).markCompleted();
    var migration = new ExtractBrokerConfigurationMigration(storage, "nats");
    assertThrows(IllegalStateException.class, migration::executeMigration);
    assertTrue(migration.shouldExecute());
  }

}
