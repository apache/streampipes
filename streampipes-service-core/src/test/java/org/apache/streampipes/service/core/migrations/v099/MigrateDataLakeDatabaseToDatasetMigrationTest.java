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

import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;
import org.lightcouch.CouchDbClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateDataLakeDatabaseToDatasetMigrationTest {

  private final MigrateDataLakeDatabaseToDatasetMigration migration =
      spy(new MigrateDataLakeDatabaseToDatasetMigration());

  @Test
  void shouldExecuteUntilLegacyDatabaseIsRemoved() {
    doReturn(true, false).when(migration).databaseExists(Utils.LEGACY_DATA_LAKE_DB_NAME);

    assertTrue(migration.shouldExecute());
    assertFalse(migration.shouldExecute());
    verify(migration, never()).databaseExists(Utils.DATA_LAKE_DB_NAME);
  }

  @Test
  void copiesEachPageBeforeFetchingTheNextAndDeletesOnlyAfterCompletion() throws IOException {
    var firstPage = rows(0, MigrateDataLakeDatabaseToDatasetMigration.PAGE_SIZE + 1);
    var lastPage = rows(MigrateDataLakeDatabaseToDatasetMigration.PAGE_SIZE, 2);
    doReturn(firstPage).when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    doReturn(lastPage).when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, "doc-100");
    doNothing().when(migration).upsertDocument(anyString(), any());
    doNothing().when(migration).removeDatabase(anyString());

    try (var utils = mockStatic(Utils.class)) {
      var client = mock(CouchDbClient.class);
      utils.when(() -> Utils.getCouchDbClient(Utils.DATA_LAKE_DB_NAME, true)).thenReturn(client);
      migration.executeMigration();
      verify(client).shutdown();
    }

    var order = inOrder(migration);
    order.verify(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    for (int i = 0; i < 100; i++) {
      order.verify(migration).upsertDocument(Utils.DATA_LAKE_DB_NAME, document(i));
    }
    order.verify(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, "doc-100");
    order.verify(migration).upsertDocument(Utils.DATA_LAKE_DB_NAME, document(100));
    order.verify(migration).upsertDocument(Utils.DATA_LAKE_DB_NAME, document(101));
    order.verify(migration).removeDatabase(Utils.LEGACY_DATA_LAKE_DB_NAME);
    verify(migration, times(102)).upsertDocument(anyString(), any());
  }

  @Test
  void emptyDatabaseIsRemoved() throws IOException {
    assertFinalPage(0);
  }

  @Test
  void partialPageFinishesMigration() throws IOException {
    assertFinalPage(1);
  }

  @Test
  void exactlyFullPageFinishesMigration() throws IOException {
    assertFinalPage(100);
  }

  private void assertFinalPage(int count) throws IOException {
    doReturn(rows(0, count)).when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    doNothing().when(migration).upsertDocument(anyString(), any());
    doNothing().when(migration).removeDatabase(anyString());
    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getCouchDbClient(Utils.DATA_LAKE_DB_NAME, true)).thenReturn(mock(CouchDbClient.class));
      migration.executeMigration();
    }
    verify(migration, times(count)).upsertDocument(anyString(), any());
    verify(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    verify(migration, never()).getDocumentPage(anyString(), anyString());
    verify(migration).removeDatabase(Utils.LEGACY_DATA_LAKE_DB_NAME);
  }

  @Test
  void failedLaterPageRetainsLegacyDatabase() throws IOException {
    doReturn(rows(0, 101)).when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    doThrow(new IOException("Page failed"))
        .when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, "doc-100");
    doNothing().when(migration).upsertDocument(anyString(), any());
    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getCouchDbClient(Utils.DATA_LAKE_DB_NAME, true)).thenReturn(mock(CouchDbClient.class));
      assertThrows(IOException.class, migration::executeMigration);
    }
    verify(migration, times(100)).upsertDocument(anyString(), any());
    verify(migration, never()).removeDatabase(anyString());
  }

  @Test
  void failedCopyRetainsLegacyDatabaseForRetry() throws IOException {
    doReturn(true).when(migration).databaseExists(Utils.LEGACY_DATA_LAKE_DB_NAME);
    doThrow(new IOException("Copy failed")).when(migration).copyDocuments(anyString(), anyString());

    assertThrows(IOException.class, migration::executeMigration);

    verify(migration, never()).removeDatabase(anyString());
    assertTrue(migration.shouldExecute());
  }

  @Test
  void failedDeletionIsRetriedEvenAfterCopyCompletes() throws IOException {
    doReturn(true).when(migration).databaseExists(Utils.LEGACY_DATA_LAKE_DB_NAME);
    doNothing().when(migration).copyDocuments(anyString(), anyString());
    doThrow(new IOException("Delete failed")).doNothing().when(migration).removeDatabase(anyString());

    assertThrows(IOException.class, migration::executeMigration);
    assertTrue(migration.shouldExecute());
    migration.executeMigration();

    verify(migration, times(2)).removeDatabase(Utils.LEGACY_DATA_LAKE_DB_NAME);
  }

  @Test
  void conflictAbortsCopyWithoutDeletingLegacyDatabase() throws IOException {
    doReturn(rows(0, 1)).when(migration).getDocumentPage(Utils.LEGACY_DATA_LAKE_DB_NAME, null);
    doReturn("2-target").when(migration).getDocumentRev(anyString());
    var request = mock(Request.class, RETURNS_DEEP_STUBS);
    when(request.execute().returnResponse()).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, 409, "Conflict"));

    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getCouchDbClient(Utils.DATA_LAKE_DB_NAME, true)).thenReturn(mock(CouchDbClient.class));
      utils.when(() -> Utils.putRequest(anyString(), anyString())).thenReturn(request);

      assertThrows(IOException.class, migration::executeMigration);
    }
    verify(migration, never()).removeDatabase(anyString());
  }

  @Test
  void revisionLookupFailurePreventsWrite() throws IOException {
    var request = mock(Request.class, RETURNS_DEEP_STUBS);
    when(request.execute().returnResponse()).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, 503, "Unavailable"));
    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getRequest(anyString())).thenReturn(request);

      assertThrows(IOException.class, () -> migration.upsertDocument(Utils.DATA_LAKE_DB_NAME, document(0)));

      utils.verify(() -> Utils.putRequest(anyString(), anyString()), never());
    }
  }

  @Test
  void existingDocumentUsesTargetRevisionWithoutMutatingSource() throws IOException {
    var source = document(0);
    source.addProperty("_rev", "1-source");
    var expected = source.deepCopy();
    expected.addProperty("_rev", "2-target");
    var get = mock(Request.class, RETURNS_DEEP_STUBS);
    var response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
    response.setEntity(new StringEntity("{\"_rev\":\"2-target\"}"));
    when(get.execute().returnResponse()).thenReturn(response);
    var put = mock(Request.class, RETURNS_DEEP_STUBS);
    when(put.execute().returnResponse()).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, 201, "Created"));

    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getRequest(anyString())).thenReturn(get);
      utils.when(() -> Utils.putRequest(anyString(), anyString())).thenReturn(put);
      migration.upsertDocument(Utils.DATA_LAKE_DB_NAME, source);
      utils.verify(() -> Utils.getRequest(anyString()), times(1));
      utils.verify(() -> Utils.putRequest(anyString(), eq(expected.toString())));
    }
    assertEquals("1-source", source.get("_rev").getAsString());
  }

  @Test
  void pageRequestIsBoundedAndEncodesDocumentId() throws IOException {
    var request = mock(Request.class, RETURNS_DEEP_STUBS);
    when(request.execute().returnContent().asString()).thenReturn("{\"rows\":[]}");
    try (var utils = mockStatic(Utils.class)) {
      utils.when(() -> Utils.getDatabaseRoute("source")).thenReturn("http://localhost/source");
      utils.when(() -> Utils.getRequest(anyString())).thenReturn(request);

      assertTrue(migration.getDocumentPage("source", "a/\"&+雪").isEmpty());

      utils.verify(() -> Utils.getRequest("http://localhost/source/_all_docs?include_docs=true&limit=101"
          + "&startkey=%22a%2F%5C%22%26%2B%E9%9B%AA%22"));
    }
  }

  private JsonArray rows(int start, int count) {
    var rows = new JsonArray();
    for (int i = start; i < start + count; i++) {
      var row = new JsonObject();
      row.addProperty("id", "doc-" + i);
      row.add("doc", document(i));
      rows.add(row);
    }
    return rows;
  }

  private JsonObject document(int index) {
    var document = new JsonObject();
    document.addProperty("_id", "doc-" + index);
    return document;
  }
}
