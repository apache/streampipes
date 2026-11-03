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

package org.apache.streampipes.storage.couchdb;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.query.MangoIndex;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.PermissionEntry;
import org.apache.streampipes.model.client.user.PrincipalType;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.api.exceptions.StorageConflictException;
import org.apache.streampipes.storage.api.exceptions.StorageException;
import org.apache.streampipes.storage.api.exceptions.StorageNotFoundException;
import org.apache.streampipes.storage.api.exceptions.StorageQueryException;
import org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants;
import org.apache.streampipes.storage.couchdb.impl.explorer.DatasetMetadataStorage;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.FileMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ImageStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PermissionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserStorage;
import org.apache.streampipes.storage.couchdb.utils.CouchDbViewGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The storages on the new client against CouchDB 3.3.1, with the views the installation step creates. Enable with
 * {@code -Dstreampipes.couchdb.integration=true}.
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
public class CouchDbStoragesIntegrationTest {

  private static GenericContainer<?> couchDb;
  private static CouchServer server;
  private static CouchDbStores stores;

  @BeforeAll
  public static void startCouchDb() {
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

  @Test
  public void crudStorageKeepsItsContract() {
    var storage = new FileMetadataStorageImpl(stores);
    stores.database("filemetadata").design().sync(DesignDocument.named("other").withView("all",
        "function(doc) { emit(doc._id, null); }"));

    var file = new FileMetadata();
    file.setElementId("file-1");
    file.setFilename("a.csv");
    var created = storage.persist(file);
    assertTrue(created.k);
    assertEquals("file-1", created.v);
    assertNull(file.getRev(), "the stored object is not modified");
    assertThrows(StorageConflictException.class, () -> storage.persist(file));

    var stored = storage.getElementById("file-1");
    stored.setFilename("b.csv");
    var updated = storage.updateElement(stored);
    assertEquals("b.csv", updated.getFilename());
    assertNotEquals(stored.getRev(), updated.getRev());
    assertThrows(StorageConflictException.class, () -> storage.updateElement(stored), "stale revision");

    var generated = new FileMetadata();
    generated.setFilename("c.csv");
    assertTrue(storage.persist(generated).v.matches("[0-9a-f]{32}"));
    assertEquals(2, storage.findAll().size(), "design documents are not returned");

    storage.deleteElement(updated);
    assertNull(storage.getElementById("file-1"));
    assertFalse(storage.delete("file-1"));
  }

  @Test
  public void principalStoragesUseTheUserViews() {
    var users = new UserStorage(stores);
    var permissions = new PermissionStorageImpl(stores, "users/permissions");
    stores.database(CouchDbStores.USER_DB_NAME).design().sync(DesignDocument.named("users")
        .withView("username",
            "function(doc) { if(doc.properties.username) { emit(doc.properties.username.toLowerCase(), doc); } }")
        .withView("permissions", "function(doc) { if(doc.$type === 'permission') { emit(doc._id, doc); } }")
        .withView("userpermissions", "function(doc) { if (doc.$type === 'permission') {emit(doc.ownerSid, doc); "
            + "for(var i = 0; i < doc.grantedAuthorities.length; i++) {emit(doc.grantedAuthorities[i].sid,doc)}}}")
        .withView("objectpermissions",
            "function(doc) { if (doc.$type === 'permission') {emit(doc.objectInstanceId, doc);}}"));
    assertTrue(users.existsDatabase());
    assertFalse(new UserStorage(new CouchDbStores(server, CouchDbCodecs.defaults()) {
      @Override
      public CouchDbStore principal(String database) {
        return new CouchDbStore(server.database("missing-users"), codecs().principal());
      }
    }).existsDatabase());

    var account = UserAccount.from("Alice@Example.com", "hash", Set.of("ROLE_ADMIN"));
    account.setPrincipalId("alice");
    users.storeUser(account);
    var loaded = users.getUserAccount("alice@example.com");
    assertEquals("alice", loaded.getPrincipalId());
    assertEquals(Set.of("ROLE_ADMIN"), loaded.getRoles());
    assertTrue(users.checkUserExists("ALICE@example.com"));
    assertEquals(1, users.getAllUserAccounts().size());

    var permission = new Permission();
    permission.setElementId("permission-1");
    permission.setObjectInstanceId("pipeline-1");
    permission.setOwnerSid("alice");
    permission.addPermissionEntry(new PermissionEntry("group-1", PrincipalType.GROUP));
    permissions.persist(permission);
    assertEquals(Set.of("pipeline-1"), permissions.getObjectPermissions(List.of("group-1", "unknown")));
    assertEquals(1, permissions.getUserPermissionsForObject("pipeline-1").size());
    assertEquals(1, permissions.findAll().size());
  }

  @Test
  public void pipelineStorageReadsAdapterUsageFromViewValues() {
    var pipelines = new PipelineStorageImpl(stores);
    var database = stores.database("pipeline");
    database.design().sync(DesignDocument.named("adapters").withView("used-adapters", "function (doc) {\n"
        + "  for(var i = 0; i < doc.streams.length; i++) {\n"
        + "    var stream = doc.streams[i];\n"
        + "    if (stream.correspondingAdapterId) {\n"
        + "      emit(stream.correspondingAdapterId, doc._id);\n"
        + "    }\n"
        + "  }\n"
        + "}"));
    database.design().sync(DesignDocument.named("pipelines").withView("all", "function (doc) {\n"
        + "  emit(doc._id, doc);\n"
        + "}"));

    var stream = new SpDataStream();
    stream.setCorrespondingAdapterId("adapter-1");
    var pipeline = new Pipeline();
    pipeline.setPipelineId("pipeline-1");
    pipeline.setStreams(List.of(stream));
    pipelines.persist(pipeline);

    var users = pipelines.getPipelinesUsingAdapter("adapter-1");
    assertEquals(List.of("pipeline-1"), users);
    users.add("mutable, as before");
    assertEquals(List.of(), pipelines.getPipelinesUsingAdapter("adapter-2"));
    assertEquals(1, pipelines.findAll().size());
  }

  @Test
  public void datasetStorageFindsByMeasureName() {
    var datasets = new DatasetMetadataStorage(stores.gson(CouchDbStores.DATA_LAKE_DB_NAME), DatasetMetadata.class);
    stores.database(CouchDbStores.DATA_LAKE_DB_NAME).design().sync(DesignDocument.named("measurement")
        .withView("by-measure-name", "function (doc) { if (doc.measureName) { emit(doc.measureName, doc); } }"));
    datasets.persist(new DatasetMetadata("temperature", new EventSchema()));
    datasets.persist(new DatasetMetadata("pressure", new EventSchema()));

    assertEquals("temperature", datasets.getByMeasureName("temperature").getMeasureName());
    assertNull(datasets.getByMeasureName("unknown"));
    assertEquals(2, datasets.findAll().size());
  }

  @Test
  public void genericStorageKeepsItsContract() throws IOException {
    var generator = new CouchDbViewGenerator(server);
    generator.createGenericDatabaseIfNotExists();
    generator.createGenericDatabaseIfNotExists();
    var storage = new GenericStorageImpl(stores);

    var ids = new ArrayList<String>();
    for (int i = 0; i < 5; i++) {
      ids.add((String) storage.create("{\"appDocType\":\"note\",\"n\":" + i + ",\"ratio\":1.10}").get("_id"));
    }
    storage.create("{\"_id\":\"other-1\",\"appDocType\":\"other\"}");
    assertEquals(ids.stream().sorted().toList(), ids, "CouchDB assigns ids that sort in creation order");
    assertEquals(List.of("note", "other"), storage.getAllAppDocTypes());

    var notes = storage.findAll("note");
    assertEquals(ids, notes.stream().map(doc -> doc.get("_id")).toList());
    assertEquals(1.1, notes.get(0).get("ratio"));

    var found = storage.find("note", Map.of(
        "selector", Map.of("appDocType", "note", "n", Map.of("$gte", 3)),
        "fields", List.of("_id", "n"),
        "execution_stats", true));
    assertEquals(2, found.size());
    assertEquals(Set.of("_id", "n"), found.get(0).keySet());

    for (int i = 0; i < 30; i++) {
      storage.create("{\"appDocType\":\"bulk\",\"owner\":\"o1\"}");
    }
    assertTrue(stores.database(GenericCouchDbConstants.DB_NAME).indexes()
        .ensure(MangoIndex.on("appDocType", "owner").named("bulk-by-owner")));
    var bulkQuery = Map.<String, Object>of("selector", Map.of("appDocType", "bulk", "owner", "o1"));
    assertEquals(25, storage.find("bulk", bulkQuery).size(), "find returns one page");
    assertEquals(30, storage.findAll("bulk", bulkQuery).size(), "findAll with a query returns every match");
    assertEquals(27, storage.findAll("bulk", Map.of("selector", bulkQuery.get("selector"), "limit", 27)).size());

    var first = storage.findOne(ids.get(0));
    first.put("n", 42);
    var updated = storage.update(ids.get(0), new ObjectMapper().writeValueAsString(first));
    assertEquals(42, updated.get("n"));
    assertThrows(StorageConflictException.class, () -> storage.update(ids.get(0), "{\"appDocType\":\"note\"}"),
        "an update without revision of an existing document conflicts");
    assertThrows(StorageConflictException.class, () -> storage.update(ids.get(0), new ObjectMapper()
        .writeValueAsString(first)), "an update with a stale revision conflicts");
    assertEquals("note",
        storage.update("never-stored", "{\"_rev\":\"1-a\",\"appDocType\":\"note\"}").get("appDocType"),
        "CouchDB stores a missing document even if a revision is given");
    assertEquals("note", storage.update("new-id", "{\"appDocType\":\"note\"}").get("appDocType"),
        "an update without revision creates a missing document");

    storage.createAttachment(ids.get(1), "report.json", "application/json; charset=UTF-8",
        "{}".getBytes(StandardCharsets.UTF_8), (String) storage.findOne(ids.get(1)).get("_rev"));
    var attachment = storage.findAttachment(ids.get(1), "report.json");
    assertEquals("application/json", attachment.getContentType());
    assertArrayEquals("{}".getBytes(StandardCharsets.UTF_8), attachment.getAttachment());
    assertThrows(StorageNotFoundException.class, () -> storage.findAttachment(ids.get(1), "missing"));

    storage.delete(ids.get(2), (String) storage.findOne(ids.get(2)).get("_rev"));
    assertNull(storage.findOneWithNullIfEmpty(ids.get(2)));
    assertThrows(StorageNotFoundException.class, () -> storage.findOne(ids.get(2)));
    assertThrows(StorageNotFoundException.class, () -> storage.delete(ids.get(2), "1-a"));
    assertThrows(StorageQueryException.class, () -> storage.delete(ids.get(3), null));
    assertThrows(StorageQueryException.class, () -> storage.find("note", Map.of("selector", "not-an-object")));
    assertThrows(StorageQueryException.class, () -> storage.create("not json"));
  }

  @Test
  public void imageStorageReadsAttachments() throws IOException {
    var images = new ImageStorageImpl(stores);
    byte[] jpeg = {(byte) 0xff, (byte) 0xd8, 1, 2, 3};
    stores.database("images").attachments().put("image-1", null, "image-1", "image/jpeg", jpeg);

    try (var in = images.getImageBytes("image-1")) {
      assertArrayEquals(jpeg, in.readAllBytes());
    }
    assertThrows(StorageException.class, () -> images.getImageBytes("missing"));
    assertNotNull(stores.database("images").raw().get("image-1").get("_attachments"));
  }
}
