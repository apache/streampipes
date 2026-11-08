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

import org.apache.streampipes.couchdb.client.codec.DocumentCodec;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.query.MangoQuery;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Documents of one type in a database, serialized with one codec. Thread-safe.
 *
 * <p>Writes take {@code _id} and {@code _rev} from the encoded document, so the codec decides how the model names
 * them. A missing {@code _id} on create is generated (32 hex characters, as LightCouch did). By default the passed
 * document is not modified; see {@link #withMetadataWriter(MetadataWriter)}.</p>
 *
 * @param <T> document type
 */
public final class TypedCollection<T> {

  private static final int DEFAULT_PAGE_SIZE = 1000;
  private static final Set<String> METADATA_FIELDS = Set.of("_id", "_rev");
  private static final Set<String> REV_FIELD = Set.of("_rev");

  private final CouchDatabase database;
  private final Type type;
  private final DocumentCodec codec;
  private final MetadataWriter<? super T> metadataWriter;

  TypedCollection(CouchDatabase database, Type type, DocumentCodec codec, MetadataWriter<? super T> metadataWriter) {
    this.database = database;
    this.type = type;
    this.codec = codec;
    this.metadataWriter = metadataWriter;
  }

  /**
   * Copy of this collection that writes id and revision back into documents after create, update and upsert.
   *
   * @param writer writer for the document type
   * @return the new collection
   */
  public TypedCollection<T> withMetadataWriter(MetadataWriter<? super T> writer) {
    return new TypedCollection<>(database, type, codec, writer);
  }

  public CouchDatabase database() {
    return database;
  }

  public Optional<T> find(String id) {
    try {
      return Optional.of(get(id));
    } catch (DocumentNotFoundException e) {
      return Optional.empty();
    }
  }

  /**
   * Reads a document.
   *
   * @param id document id
   * @return the document
   * @throws DocumentNotFoundException if it does not exist
   */
  public T get(String id) {
    return database.execute(CouchRequest.get(database.documentPath(id)),
        response -> CouchJson.readDocument(response.body(), codec, type));
  }

  public boolean exists(String id) {
    return revision(id).isPresent();
  }

  /**
   * Current revision of a document, read with {@code HEAD} without transferring the body.
   *
   * @param id document id
   * @return the revision, empty if the document does not exist
   */
  public Optional<String> revision(String id) {
    try {
      return database.execute(CouchRequest.head(database.documentPath(id)),
          response -> Optional.ofNullable(stripQuotes(response.header("etag"))));
    } catch (DocumentNotFoundException e) {
      return Optional.empty();
    }
  }

  /**
   * Creates a document.
   *
   * @param document document without {@code _rev}
   * @return id and revision
   * @throws DocumentConflictException if a document with the id exists
   */
  public DocumentRef create(T document) {
    byte[] json = codec.encode(document);
    CouchJson.DocumentMeta meta = CouchJson.scan(json);
    if (meta.rev() != null) {
      throw new IllegalArgumentException("A document to create must not have a _rev");
    }
    String id = meta.id() != null ? meta.id() : generateId();
    if (meta.hasNullMetadata()) {
      json = CouchJson.withoutFields(json, METADATA_FIELDS);
    }
    return write(document, CouchRequest.put(database.documentPath(id), json));
  }

  /**
   * Writes a document with {@code POST /{db}}. Unlike {@link #create(Object)}, CouchDB assigns the id if the
   * document has none; with the default {@code sequential} algorithm those ids sort in creation order. A document
   * with {@code _id} and {@code _rev} updates that revision, as CouchDB does for this endpoint.
   *
   * @param document document
   * @return id and revision
   * @throws DocumentConflictException if a document with the id exists or the revision is not the current one
   */
  public DocumentRef post(T document) {
    byte[] json = codec.encode(document);
    CouchJson.DocumentMeta meta = CouchJson.scan(json);
    if (meta.hasNullMetadata()) {
      // the body carries the id here, so only the null fields are removed
      json = CouchJson.withoutFields(json, meta.id() == null ? METADATA_FIELDS : REV_FIELD);
    }
    return write(document, CouchRequest.post(database.path(""), json));
  }

  /**
   * Updates a document.
   *
   * @param document document with {@code _id} and the current {@code _rev}
   * @return id and new revision
   * @throws DocumentConflictException if the revision is not the current one
   */
  public DocumentRef update(T document) {
    byte[] json = codec.encode(document);
    CouchJson.DocumentMeta meta = CouchJson.scan(json);
    if (meta.id() == null || meta.rev() == null) {
      throw new IllegalArgumentException("A document to update needs _id and _rev");
    }
    return write(document, CouchRequest.put(database.documentPath(meta.id()), json));
  }

  /**
   * Creates the document or overwrites the current revision, ignoring any {@code _rev} the document carries.
   * Intended for migrations and imports; a concurrent write between reading the revision and writing still fails
   * with {@link DocumentConflictException}.
   *
   * @param document document
   * @return id and new revision
   */
  public DocumentRef upsert(T document) {
    byte[] json = codec.encode(document);
    CouchJson.DocumentMeta meta = CouchJson.scan(json);
    if (meta.id() == null) {
      return create(document);
    }
    Optional<String> current = revision(meta.id());
    byte[] body = meta.rev() != null || meta.hasNullMetadata() ? CouchJson.withoutFields(json, REV_FIELD) : json;
    String path = database.documentPath(meta.id())
        + current.map(rev -> CouchPaths.query(Map.of("rev", rev))).orElse("");
    return write(document, CouchRequest.put(path, body));
  }

  public DocumentRef delete(String id, String rev) {
    return database.execute(
        CouchRequest.delete(database.documentPath(id) + CouchPaths.query(Map.of("rev", rev))),
        CouchJson::readRef);
  }

  /**
   * Deletes the current revision of a document.
   *
   * @param id document id
   * @return true if a document was deleted, false if none existed
   */
  public boolean delete(String id) {
    Optional<String> rev = revision(id);
    if (rev.isEmpty()) {
      return false;
    }
    try {
      delete(id, rev.get());
      return true;
    } catch (DocumentNotFoundException e) {
      return false;
    }
  }

  /**
   * All documents of the database, without design documents.
   *
   * @return the documents
   */
  public List<T> findAll() {
    return allDocs().docs();
  }

  public ViewQuery<T> allDocs() {
    return new ViewQuery<>(this, database.path("/_all_docs"), true);
  }

  public ViewQuery<T> view(String designDocument, String view) {
    String design = designDocument.startsWith("_design/") ? designDocument.substring("_design/".length())
        : designDocument;
    return new ViewQuery<>(this,
        database.path("/_design/" + CouchPaths.segment(design) + "/_view/" + CouchPaths.segment(view)),
        false);
  }

  /**
   * Runs one Mango query. Without a limit CouchDB returns at most 25 documents; use
   * {@link #findAll(MangoQuery)} to get every match.
   *
   * @param query query
   * @return one page of results
   */
  public FindResult<T> find(MangoQuery query) {
    return database.execute(CouchRequest.post(database.path("/_find"), CouchJson.write(query.toMap())),
        response -> CouchJson.readFindResult(response.body(), codec, type));
  }

  /**
   * Returns every match of a Mango query, paging with bookmarks in pages of {@value #DEFAULT_PAGE_SIZE}. See
   * {@link #findAll(MangoQuery, int)}.
   *
   * @param query query
   * @return all matching documents, at most the query's limit
   */
  public List<T> findAll(MangoQuery query) {
    return findAll(query, DEFAULT_PAGE_SIZE);
  }

  /**
   * Returns every match of a Mango query, paging with bookmarks. A limit on the query caps the total number of
   * documents, as in a single {@code _find}; without one, all matches are returned. The query's skip applies to the
   * first page only.
   *
   * @param query    query
   * @param pageSize documents per request
   * @return all matching documents, at most the query's limit
   */
  public List<T> findAll(MangoQuery query, int pageSize) {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("Page size must be positive");
    }
    Integer total = query.getLimit();
    var result = new ArrayList<T>();
    MangoQuery page = query.copy();
    while (total == null || result.size() < total) {
      int requested = total == null ? pageSize : Math.min(pageSize, total - result.size());
      page.limit(requested);
      FindResult<T> pageResult = find(page);
      result.addAll(pageResult.docs());
      if (pageResult.docs().size() < requested || pageResult.bookmark() == null
          || pageResult.bookmark().equals(page.getBookmark())) {
        break;
      }
      page = page.copy().bookmark(pageResult.bookmark()).clearSkip();
    }
    return result;
  }

  Type type() {
    return type;
  }

  DocumentCodec codec() {
    return codec;
  }

  private DocumentRef write(T document, CouchRequest request) {
    DocumentRef ref = database.execute(request, CouchJson::readRef);
    if (metadataWriter != null) {
      metadataWriter.write(document, ref);
    }
    return ref;
  }

  private static String generateId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private static String stripQuotes(String etag) {
    if (etag != null && etag.length() >= 2 && etag.startsWith("\"") && etag.endsWith("\"")) {
      return etag.substring(1, etag.length() - 1);
    }
    return etag;
  }
}
