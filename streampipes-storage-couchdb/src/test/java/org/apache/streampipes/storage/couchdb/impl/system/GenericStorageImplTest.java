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
package org.apache.streampipes.storage.couchdb.impl.system;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;
import org.apache.streampipes.storage.api.exceptions.StorageConflictException;
import org.apache.streampipes.storage.api.exceptions.StorageException;
import org.apache.streampipes.storage.api.exceptions.StorageNotFoundException;
import org.apache.streampipes.storage.api.exceptions.StorageQueryException;
import org.apache.streampipes.storage.api.exceptions.StorageUnavailableException;
import org.apache.streampipes.storage.couchdb.CouchDbCodecs;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenericStorageImplTest {

  private static final String DOCUMENT_MISSING = "{\"error\":\"not_found\",\"reason\":\"deleted\"}";
  private static final String DATABASE_MISSING =
      "{\"error\":\"not_found\",\"reason\":\"Database does not exist.\"}";

  @Test
  public void reportsAMissingDocumentAsNotFound() {
    var storage = storageAnswering(404, DOCUMENT_MISSING);

    assertThrows(StorageNotFoundException.class, () -> storage.findOne("a"));
    assertThrows(StorageNotFoundException.class, () -> storage.delete("a", "1-a"));
    assertThrows(StorageNotFoundException.class, () -> storage.findAttachment("a", "file"));
  }

  @Test
  public void reportsAConflict() {
    var storage = storageAnswering(409, "{\"error\":\"conflict\",\"reason\":\"Document update conflict.\"}");

    assertThrows(StorageConflictException.class, () -> storage.delete("a", "1-a"));
    assertThrows(StorageConflictException.class, () -> storage.update("a", "{\"_rev\":\"1-a\"}"));
    assertThrows(StorageConflictException.class, () -> storage.update("a", "{}"));
    assertThrows(StorageConflictException.class,
        () -> storage.createAttachment("a", "file", "text/plain", new byte[1], "1-a"));
  }

  @Test
  public void reportsInvalidQueriesAndDocuments() {
    var storage = storageAnswering(400, "{\"error\":\"bad_request\",\"reason\":\"invalid selector\"}");

    assertThrows(StorageQueryException.class, () -> storage.find("type", Map.of("selector", Map.of())));
    assertThrows(StorageQueryException.class, () -> storage.find("type", Map.of("selector", "not-an-object")));
    assertThrows(StorageQueryException.class, () -> storage.find("type", Map.of("limit", 1)));
    assertThrows(StorageQueryException.class, () -> storage.create("not json"));
    assertThrows(StorageQueryException.class, () -> storage.update("a", "[]"));
    assertThrows(StorageQueryException.class, () -> storage.delete("a", null));
  }

  @Test
  public void reportsAnUnavailableStorage() {
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(401, "{}").findOne("a"));
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(403, "{}").findOne("a"));
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(500, "{}").findOne("a"));
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(503, "{}").findOne("a"));
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(404, DATABASE_MISSING).findOne("a"));
    assertThrows(StorageUnavailableException.class, () -> storageFailing().findOne("a"));
  }

  @Test
  public void reportsAMissingViewAsUnavailableRatherThanAsAMissingDocument() {
    var storage = storageAnswering(404, "{\"error\":\"not_found\",\"reason\":\"missing\"}");

    assertThrows(StorageUnavailableException.class, () -> storage.findAll("type"));
    assertThrows(StorageUnavailableException.class, storage::getAllAppDocTypes);
  }

  @Test
  public void reportsOtherStatusesWithTheBaseException() {
    var exception = assertThrows(StorageException.class, () -> storageAnswering(413, "{}").findOne("a"));

    assertEquals(StorageException.class, exception.getClass());
  }

  @Test
  public void reportsAResponseThatIsNotADocumentAsStorageErrorRatherThanUnavailable() {
    var exception = assertThrows(StorageException.class, () -> storageAnswering(200, "[]").findOne("a"));

    assertEquals(StorageException.class, exception.getClass());
  }

  @Test
  public void returnsNullOnlyForAMissingDocument() {
    assertNull(storageAnswering(404, DOCUMENT_MISSING).findOneWithNullIfEmpty("a"));
    assertThrows(StorageUnavailableException.class, () -> storageAnswering(500, "{}").findOneWithNullIfEmpty("a"));
    assertThrows(StorageUnavailableException.class, () -> storageFailing().findOneWithNullIfEmpty("a"));
  }

  private static GenericStorageImpl storageAnswering(int status, String body) {
    return storage(() -> new CouchResponse(status, Map.of(),
        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))));
  }

  private static GenericStorageImpl storageFailing() {
    return storage(() -> {
      throw new CouchDbTransportException("connection refused", new IOException("connection refused"));
    });
  }

  /**
   * The database itself always exists, so that the answer applies to the request under test.
   */
  private static GenericStorageImpl storage(Supplier<CouchResponse> answer) {
    var transport = new CouchTransport() {
      @Override
      public CouchResponse send(CouchRequest request, Map<String, String> headers) {
        return "HEAD".equals(request.method())
            ? new CouchResponse(200, Map.of(), new ByteArrayInputStream(new byte[0]))
            : answer.get();
      }

      @Override
      public void close() {
      }
    };
    var server = CouchServer.builder().transport(config -> transport).build();
    return new GenericStorageImpl(new CouchDbStores(server, CouchDbCodecs.defaults()));
  }
}
