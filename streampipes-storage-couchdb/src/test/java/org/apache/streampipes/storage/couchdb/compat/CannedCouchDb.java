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

package org.apache.streampipes.storage.couchdb.compat;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A database holding one stored document, served to the new client without a server: {@code GET} of a document
 * returns it, {@code _all_docs} and view queries return it as the only row.
 */
final class CannedCouchDb implements CouchTransport {

  private final byte[] document;
  private final byte[] rows;

  private CannedCouchDb(String id, byte[] document) {
    this.document = document;
    this.rows = rowsResponse(id, document);
  }

  static CouchServer serving(String id, byte[] document) {
    var transport = new CannedCouchDb(id, document);
    return CouchServer.builder().transport(config -> transport).build();
  }

  /**
   * The response CouchDB gives for a view or {@code _all_docs} query with {@code include_docs=true}.
   */
  static byte[] rowsResponse(String id, byte[] document) {
    try {
      String quotedId = JsonTrees.MAPPER.writeValueAsString(id);
      return ("{\"total_rows\":1,\"offset\":0,\"rows\":[{\"id\":" + quotedId + ",\"key\":" + quotedId
          + ",\"value\":{\"rev\":\"1-a\"},\"doc\":" + new String(document, StandardCharsets.UTF_8) + "}]}")
          .getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public CouchResponse send(CouchRequest request, Map<String, String> headers) {
    if (!"GET".equals(request.method())) {
      return response(405, "{\"error\":\"method_not_allowed\"}".getBytes(StandardCharsets.UTF_8));
    }
    String path = request.path();
    return response(200, path.contains("/_all_docs") || path.contains("/_view/") ? rows : document);
  }

  @Override
  public void close() {
  }

  private static CouchResponse response(int status, byte[] body) {
    return new CouchResponse(status, Map.of("content-type", "application/json"), new ByteArrayInputStream(body));
  }
}
