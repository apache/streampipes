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

import org.apache.streampipes.service.core.migrations.InMemoryCouchTransport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.apache.streampipes.service.core.migrations.v099.MigrateDataLakeDatabaseToDatasetMigration.LEGACY_DATA_LAKE_DB_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrateDataLakeDatabaseToDatasetMigrationTest {

  private static final String TARGET = "dataset";
  private static final String PAGE_REQUEST = "GET /data-lake/_all_docs";

  private InMemoryCouchTransport couch;
  private MigrateDataLakeDatabaseToDatasetMigration migration;

  @BeforeEach
  void setUp() {
    couch = new InMemoryCouchTransport();
    migration = new MigrateDataLakeDatabaseToDatasetMigration(couch.stores());
  }

  @Test
  void shouldExecuteUntilLegacyDatabaseIsRemoved() throws IOException {
    assertFalse(migration.shouldExecute());
    assertFalse(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME), "checking must not create the database");

    legacyDocuments(1);
    assertTrue(migration.shouldExecute());
    migration.executeMigration();
    assertFalse(migration.shouldExecute());
  }

  @Test
  void copiesEachPageBeforeFetchingTheNextAndDeletesOnlyAfterCompletion() throws IOException {
    legacyDocuments(102);

    migration.executeMigration();

    assertEquals(102, couch.documentIds(TARGET).size());
    assertFalse(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));

    var requests = couch.requests();
    int firstPage = requests.indexOf(PAGE_REQUEST + "?limit=101&include_docs=true");
    int secondPage = requests.indexOf(PAGE_REQUEST + "?limit=101&start_key=%22doc-100%22&include_docs=true");
    int delete = requests.indexOf("DELETE /data-lake");
    assertTrue(firstPage >= 0 && firstPage < secondPage && secondPage < delete, requests.toString());
    assertEquals(100, writes(requests.subList(firstPage, secondPage)));
    assertEquals(2, writes(requests.subList(secondPage, delete)));
    assertEquals(delete, requests.size() - 1, "nothing follows the deletion");
  }

  @Test
  void emptyDatabaseIsRemoved() throws IOException {
    assertFinalPage(0);
    assertTrue(couch.hasDatabase(TARGET), "the target exists even if there was nothing to copy");
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
    legacyDocuments(count);

    migration.executeMigration();

    assertEquals(count, couch.documentIds(TARGET).size());
    assertEquals(1, couch.requestsStartingWith(PAGE_REQUEST).size());
    assertFalse(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));
  }

  @Test
  void failedLaterPageRetainsLegacyDatabase() {
    legacyDocuments(101);
    couch.fail(PAGE_REQUEST + "?limit=101&start_key", 503);

    assertThrows(IOException.class, migration::executeMigration);

    assertEquals(100, couch.documentIds(TARGET).size());
    assertTrue(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));
    assertTrue(migration.shouldExecute());
  }

  @Test
  void failedDeletionIsRetriedEvenAfterCopyCompletes() throws IOException {
    legacyDocuments(2);
    couch.failOnce("DELETE /data-lake", 500);

    assertThrows(IOException.class, migration::executeMigration);
    assertTrue(migration.shouldExecute());

    migration.executeMigration();

    assertFalse(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));
    assertEquals(2, couch.documentIds(TARGET).size());
  }

  @Test
  void legacyDatabaseAlreadyGoneCountsAsRemoved() throws IOException {
    legacyDocuments(1);
    couch.fail("DELETE /data-lake", 404);

    migration.executeMigration();
  }

  @Test
  void conflictAbortsCopyWithoutDeletingLegacyDatabase() {
    legacyDocuments(1);
    couch.fail("PUT /dataset/doc-000", 409);

    assertThrows(IOException.class, migration::executeMigration);

    assertTrue(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));
  }

  @Test
  void revisionLookupFailurePreventsWrite() {
    legacyDocuments(1);
    couch.fail("HEAD /dataset/doc-000", 503);

    assertThrows(IOException.class, migration::executeMigration);

    assertEquals(List.of(), couch.requestsStartingWith("PUT /dataset/doc-000"));
    assertTrue(couch.hasDatabase(LEGACY_DATA_LAKE_DB_NAME));
  }

  @Test
  void existingDocumentUsesTargetRevision() throws IOException {
    couch.createDatabase(LEGACY_DATA_LAKE_DB_NAME);
    couch.createDatabase(TARGET);
    couch.store(LEGACY_DATA_LAKE_DB_NAME, "{\"_id\":\"doc\",\"_rev\":\"1-source\",\"value\":\"new\"}");
    couch.store(TARGET, "{\"_id\":\"doc\",\"_rev\":\"2-target\",\"value\":\"old\"}");

    migration.executeMigration();

    var copied = couch.document(TARGET, "doc");
    assertEquals("new", copied.get("value").asText());
    assertEquals("3-memory", copied.get("_rev").asText());
  }

  @Test
  void documentsAreCopiedCompletelyIncludingDesignDocuments() throws IOException {
    couch.createDatabase(LEGACY_DATA_LAKE_DB_NAME);
    couch.store(LEGACY_DATA_LAKE_DB_NAME, "{\"_id\":\"_design/measurement\",\"language\":\"javascript\",\"views\":{}}");
    couch.store(LEGACY_DATA_LAKE_DB_NAME,
        "{\"_id\":\"doc\",\"nothing\":null,\"count\":12345678901234567890,\"ratio\":0.1,\"nested\":{\"list\":[1,\"a\"]}}");

    migration.executeMigration();

    assertEquals("javascript", couch.document(TARGET, "_design/measurement").get("language").asText());
    var document = couch.document(TARGET, "doc");
    assertTrue(document.has("nothing") && document.get("nothing").isNull());
    assertEquals("12345678901234567890", document.get("count").asText());
    assertEquals(0.1, document.get("ratio").asDouble());
    assertEquals("a", document.at("/nested/list/1").asText());
  }

  @Test
  void pageRequestEncodesDocumentId() throws IOException {
    legacyDocuments(100);
    String id = "z/\"&+\u96EA";
    couch.store(LEGACY_DATA_LAKE_DB_NAME, "{\"_id\":\"z/\\\"&+\u96EA\"}");

    migration.executeMigration();

    assertTrue(couch.requests().contains(
        PAGE_REQUEST + "?limit=101&start_key=%22z%2F%5C%22%26%2B%E9%9B%AA%22&include_docs=true"), couch.requests().toString());
    assertTrue(couch.documentIds(TARGET).contains(id));
    assertEquals(1, couch.requestsStartingWith("PUT /dataset/z%2F%22%26%2B%E9%9B%AA").size());
  }

  private void legacyDocuments(int count) {
    couch.createDatabase(LEGACY_DATA_LAKE_DB_NAME);
    for (int i = 0; i < count; i++) {
      couch.store(LEGACY_DATA_LAKE_DB_NAME, "{\"_id\":\"doc-%03d\"}".formatted(i));
    }
  }

  private long writes(List<String> requests) {
    return requests.stream().filter(request -> request.startsWith("PUT /dataset/")).count();
  }
}
