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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.codec.JacksonCodec;
import org.apache.streampipes.couchdb.client.exception.BadRequestException;
import org.apache.streampipes.couchdb.client.exception.CouchDbAuthenticationException;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;
import org.apache.streampipes.couchdb.client.exception.DatabaseNotFoundException;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.exception.PreconditionFailedException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CouchJsonTest {

  private static final java.lang.reflect.Type MAP = Map.class;

  @Test
  public void scansTopLevelMetadataOnly() {
    var meta = CouchJson.scan(bytes("{\"a\":{\"_id\":\"nested\"},\"_id\":\"x\",\"_rev\":\"1-a\",\"b\":[1,2]}"));
    assertEquals("x", meta.id());
    assertEquals("1-a", meta.rev());
    assertFalse(meta.hasNullMetadata());

    var withNull = CouchJson.scan(bytes("{\"_id\":\"x\",\"_rev\":null}"));
    assertEquals("x", withNull.id());
    assertNull(withNull.rev());
    assertTrue(withNull.hasNullMetadata());

    assertThrows(IllegalArgumentException.class, () -> CouchJson.scan(bytes("[1]")));
  }

  @Test
  public void removesTopLevelFields() {
    byte[] result = CouchJson.withoutFields(
        bytes("{\"_id\":\"x\",\"_rev\":null,\"inner\":{\"_rev\":\"keep\"},\"n\":1.10}"), Set.of("_rev"));
    assertEquals("{\"_id\":\"x\",\"inner\":{\"_rev\":\"keep\"},\"n\":1.10}", new String(result, StandardCharsets.UTF_8));
  }

  @Test
  public void readsRowDocumentsAndSkipsDesignAndMissingRows() throws IOException {
    String json = "{\"total_rows\":4,\"offset\":0,\"rows\":["
        + "{\"id\":\"_design/x\",\"key\":\"_design/x\",\"value\":{\"rev\":\"1\"},\"doc\":{\"_id\":\"_design/x\"}},"
        + "{\"id\":\"a\",\"key\":\"a\",\"value\":{\"rev\":\"1\"},\"doc\":{\"_id\":\"a\",\"n\":1}},"
        + "{\"key\":\"missing\",\"error\":\"not_found\"},"
        + "{\"id\":\"b\",\"key\":\"b\",\"value\":{\"rev\":\"2\",\"deleted\":true},\"doc\":null}"
        + "]}";
    List<Map<String, Object>> docs = CouchJson.readRowDocuments(stream(json), JacksonCodec.defaults(), MAP, true);
    assertEquals(1, docs.size());
    assertEquals("a", docs.get(0).get("_id"));

    List<Map<String, Object>> withDesign =
        CouchJson.readRowDocuments(stream(json), JacksonCodec.defaults(), MAP, false);
    assertEquals(2, withDesign.size());
  }

  @Test
  public void doesNotDecodeSkippedDesignDocuments() throws IOException {
    String json = "{\"rows\":["
        + "{\"id\":\"_design/x\",\"key\":\"_design/x\",\"value\":{\"rev\":\"1\"},\"doc\":{\"_id\":\"_design/x\","
        + "\"views\":{}}},"
        + "{\"id\":\"a\",\"key\":\"a\",\"value\":{\"rev\":\"1\"},\"doc\":{\"_id\":\"a\",\"n\":1}}"
        + "]}";
    var strict = JacksonCodec.of(JsonMapper.builder()
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build());
    List<Doc> docs = CouchJson.readRowDocuments(stream(json), strict, Doc.class, true);
    assertEquals(List.of(new Doc("a", 1)), docs);
  }

  record Doc(@JsonProperty("_id") String id, int n) {
  }

  @Test
  public void readsRows() throws IOException {
    String json = "{\"rows\":[{\"id\":\"p1\",\"key\":[\"t\",1],\"value\":\"adapter\"},"
        + "{\"key\":\"x\",\"error\":\"not_found\"}]}";
    List<ViewRow> rows = CouchJson.readRows(stream(json), false);
    assertEquals(1, rows.size());
    assertEquals("p1", rows.get(0).id());
    assertEquals("t", rows.get(0).key().get(0).asText());
    assertEquals("adapter", rows.get(0).value().asText());
  }

  @Test
  public void readsFindResults() throws IOException {
    String json = "{\"docs\":[{\"_id\":\"a\"},{\"_id\":\"b\"}],\"bookmark\":\"g1\","
        + "\"warning\":\"No matching index found\",\"execution_stats\":{\"a\":1}}";
    FindResult<Map<String, Object>> result = CouchJson.readFindResult(stream(json), JacksonCodec.defaults(), MAP);
    assertEquals(2, result.docs().size());
    assertEquals("g1", result.bookmark());
    assertEquals("No matching index found", result.warning());
  }

  @Test
  public void reportsUnexpectedResponseShapesAsMappingErrors() {
    assertThrows(DocumentMappingException.class,
        () -> CouchJson.readDocument(stream("[]"), JacksonCodec.defaults(), MAP));
    assertThrows(DocumentMappingException.class,
        () -> CouchJson.readFindResult(stream("[]"), JacksonCodec.defaults(), MAP));
    assertThrows(DocumentMappingException.class, () -> CouchJson.readRows(stream("\"text\""), false));
  }

  @Test
  public void mapsErrorStatuses() {
    assertInstanceOf(DatabaseNotFoundException.class,
        error(404, "{\"error\":\"not_found\",\"reason\":\"Database does not exist.\"}"));
    assertInstanceOf(DocumentNotFoundException.class, error(404, "{\"error\":\"not_found\",\"reason\":\"missing\"}"));
    assertInstanceOf(DocumentConflictException.class, error(409, "{\"error\":\"conflict\"}"));
    assertInstanceOf(PreconditionFailedException.class, error(412, "{\"error\":\"file_exists\"}"));
    assertInstanceOf(CouchDbAuthenticationException.class, error(401, "{\"error\":\"unauthorized\"}"));
    assertInstanceOf(BadRequestException.class, error(400, "not json"));
    CouchDbException serverError = error(500, "{\"error\":\"unknown\",\"reason\":\"boom\"}");
    assertEquals(CouchDbException.class, serverError.getClass());
    assertEquals("boom", serverError.getReason());
  }

  @Test
  public void keepsQueryStringOutOfErrorMessages() {
    var request = CouchRequest.get("/users/_design/users/_view/refresh-token?key=%22secret-hash%22");
    var exception = CouchJson.toException(request, response(404, "{\"error\":\"not_found\",\"reason\":\"missing\"}"));
    assertFalse(exception.getMessage().contains("secret-hash"));
    assertTrue(exception.getMessage().contains("/users/_design/users/_view/refresh-token"));
  }

  private static CouchDbException error(int status, String body) {
    return CouchJson.toException(CouchRequest.get("/db/doc"), response(status, body));
  }

  private static CouchResponse response(int status, String body) {
    return new CouchResponse(status, Map.of(), stream(body));
  }

  private static byte[] bytes(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }

  private static InputStream stream(String value) {
    return new ByteArrayInputStream(bytes(value));
  }
}
