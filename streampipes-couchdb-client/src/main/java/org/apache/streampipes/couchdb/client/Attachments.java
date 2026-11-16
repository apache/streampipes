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

import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Attachments of documents in one database. Each write creates a new document revision.
 */
public final class Attachments {

  private static final String ANY = "*/*";

  private final CouchDatabase database;

  Attachments(CouchDatabase database) {
    this.database = database;
  }

  /**
   * Stores an attachment.
   *
   * @param docId       document id; the document is created if {@code rev} is {@code null} and it does not exist
   * @param rev         current document revision, {@code null} for a new document
   * @param name        attachment name
   * @param contentType content type
   * @param data        content
   * @return document id and its new revision
   */
  public DocumentRef put(String docId, String rev, String name, String contentType, byte[] data) {
    Objects.requireNonNull(contentType, "contentType");
    Objects.requireNonNull(data, "data");
    String path = attachmentPath(docId, name) + (rev != null ? CouchPaths.query(Map.of("rev", rev)) : "");
    return database.execute(new CouchRequest("PUT", path, CouchRequest.JSON, contentType, data),
        CouchJson::readRef);
  }

  public Optional<Attachment> get(String docId, String name) {
    try {
      return Optional.of(database.execute(CouchRequest.get(attachmentPath(docId, name)).withAccept(ANY),
          response -> new Attachment(response.contentType(), response.bodyBytes())));
    } catch (DocumentNotFoundException e) {
      return Optional.empty();
    }
  }

  /**
   * Opens an attachment for streaming; closing the stream releases the connection.
   *
   * @param docId document id
   * @param name  attachment name
   * @return the content
   * @throws DocumentNotFoundException if document or attachment do not exist
   */
  public InputStream open(String docId, String name) {
    CouchResponse response = database.open(CouchRequest.get(attachmentPath(docId, name)).withAccept(ANY));
    return new FilterInputStream(response.body()) {
      @Override
      public void close() throws IOException {
        response.close();
      }
    };
  }

  public DocumentRef delete(String docId, String rev, String name) {
    return database.execute(
        CouchRequest.delete(attachmentPath(docId, name) + CouchPaths.query(Map.of("rev", rev))),
        CouchJson::readRef);
  }

  private String attachmentPath(String docId, String name) {
    return database.documentPath(docId) + "/" + CouchPaths.attachment(name);
  }
}
