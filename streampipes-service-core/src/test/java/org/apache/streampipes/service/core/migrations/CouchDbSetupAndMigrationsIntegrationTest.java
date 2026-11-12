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


package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;
import org.apache.streampipes.couchdb.client.codec.DocumentCodec;
import org.apache.streampipes.couchdb.client.codec.JacksonCodec;
import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.design.SyncResult;
import org.apache.streampipes.manager.setup.CouchDbInstallationStep;
import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.service.core.migrations.v0980.AddDatasetMetadataViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddAssetManagementViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddFunctionStateViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddRefreshTokenViewsMigration;
import org.apache.streampipes.service.core.migrations.v099.AddScriptTemplateViewMigration;
import org.apache.streampipes.service.core.migrations.v099.MigrateDataLakeDatabaseToDatasetMigration;
import org.apache.streampipes.service.core.migrations.v099.RemoveInternalNotificationSinkMigration;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.CouchDbCodecs;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.impl.pipeline.CompactPipelineTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.AssetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.utils.CouchDbViewGenerator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Installation step and CouchDB migrations against CouchDB 3.3.1. Enable with
 * {@code -Dstreampipes.couchdb.integration=true}.
 */
public class CouchDbSetupAndMigrationsIntegrationTest {

  private static final boolean ENABLED = Boolean.getBoolean("streampipes.couchdb.integration");

  // Reads what CouchDB returns without rounding, to prove that the copy did not change a value.
  private static final ObjectMapper EXACT_MAPPER = JsonMapper.builder()
      .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
      .build();
  private static final DocumentCodec EXACT_JSON = JacksonCodec.of(EXACT_MAPPER);

  private static GenericContainer<?> couchDb;
  private static CouchServer server;
  private static CouchDbStores stores;

  @BeforeAll
  public static void startCouchDb() {
    if (!ENABLED) {
      return;
    }
    couchDb = new GenericContainer<>("couchdb:3.3.1")
        .withEnv("COUCHDB_USER", "admin")
        .withEnv("COUCHDB_PASSWORD", "admin")
        .withExposedPorts(5984)
        .waitingFor(Wait.forHttp("/_up").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)));
    couchDb.start();
    server = CouchServer.builder()
        .url("http", couchDb.getHost(), couchDb.getMappedPort(5984))
        .credentials("admin", "admin")
        .build();
    stores = new CouchDbStores(server, CouchDbCodecs.defaults());
  }

  @AfterAll
  public static void stopCouchDb() {
    if (server != null) {
      server.close();
    }
    if (couchDb != null) {
      couchDb.stop();
    }
  }

  @BeforeEach
  public void removeDatabases() {
    if (ENABLED) {
      server.allDatabases().stream()
          .filter(name -> !name.startsWith("_"))
          .forEach(name -> server.database(name).delete());
    }
  }

  @Test
  @EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
  public void installationCreatesWhatTheStoragesQuery() {
    new CouchDbViewGenerator(server).createGenericDatabaseIfNotExists();
    var genericStorage = new GenericStorageImpl(stores);

    var step = new CouchDbInstallationStep(stores, genericStorage, new CompactPipelineTemplateStorageImpl(stores));
    step.install();

    assertEquals(0, step.getErrorCount());
    assertTrue(server.allDatabases().containsAll(List.of("users", "pipeline", "dataset", "genericstorage")));
    assertEquals(List.of(), new AssetStorageImpl(stores).findAll());
    assertEquals(List.of(), new PipelineStorageImpl(stores).getPipelinesUsingAdapter("adapter"));
    assertEquals(SyncResult.UNCHANGED, stores.database("users").design().sync(new UserDesignDocument().make()));
  }

  @Test
  @EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
  public void viewMigrationsRunOnceOnAnExistingInstallation() throws IOException {
    var migrations = List.of(
        new AddDatasetMetadataViewMigration(stores),
        new AddAssetManagementViewMigration(stores),
        new AddScriptTemplateViewMigration(stores),
        new AddFunctionStateViewMigration(stores),
        new AddRefreshTokenViewsMigration(stores));

    for (var migration : migrations) {
      assertTrue(migration.shouldExecute(), migration.getDescription());
      migration.executeMigration();
      assertFalse(migration.shouldExecute(), migration.getDescription());
    }
  }

  @Test
  @EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
  public void dataLakeMigrationCopiesEverythingAndRemovesTheLegacyDatabase() throws IOException {
    var legacyDatabase = server.database("data-lake", DatabaseOptions.createIfMissing());
    legacyDatabase.create();
    var legacy = legacyDatabase.typed(JsonNode.class, EXACT_JSON);
    for (int i = 0; i < 250; i++) {
      legacy.create(EXACT_MAPPER.createObjectNode().put("_id", "doc-%03d".formatted(i)).put("index", i));
    }
    legacy.create(EXACT_MAPPER.readTree("""
        {"_id":"a/\\"&+\u96EA","nothing":null,"big":12345678901234567890,
         "ratio":0.12345678901234567890123,"small":1.5e-300,"nested":{"list":[1,"a",{"b":true}]}}"""));
    stores.database("dataset").create();
    stores.database("dataset").raw().create(new LinkedHashMap<>(Map.of("_id", "doc-000", "index", -1)));
    legacyDatabase.design().sync(DesignDocument.named("measurement")
        .withView("by-measure-name", "function (doc) { emit(doc.measureName, doc); }"));

    var special = legacy.get("a/\"&+\u96EA");
    var migration = new MigrateDataLakeDatabaseToDatasetMigration(stores);
    assertTrue(migration.shouldExecute());

    migration.executeMigration();

    assertFalse(migration.shouldExecute());
    assertFalse(server.allDatabases().contains("data-lake"));
    var target = stores.database("dataset").typed(JsonNode.class, EXACT_JSON);
    assertEquals(252, target.allDocs().includeDesignDocuments().ids().size());
    assertEquals(0, target.get("doc-000").get("index").asInt(), "the existing document was overwritten");

    var copied = target.get("a/\"&+\u96EA");
    for (String field : List.of("nothing", "big", "ratio", "small", "nested")) {
      assertEquals(special.get(field), copied.get(field), field);
    }
    assertTrue(stores.database("dataset").design().get("measurement").isPresent());
  }

  @Test
  @EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
  public void notificationDatabaseIsRemoved() throws IOException {
    server.database("notification").create();
    var migration = new RemoveInternalNotificationSinkMigration(
        stores, mock(IPipelineStorage.class), mock(IDataSinkStorage.class));
    assertTrue(migration.shouldExecute());

    migration.executeMigration();

    assertFalse(server.allDatabases().contains("notification"));
    assertFalse(migration.shouldExecute());
    assertFalse(server.allDatabases().contains("notification"), "checking did not create it again");
  }
}
