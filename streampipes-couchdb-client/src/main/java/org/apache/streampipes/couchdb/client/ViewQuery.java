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

import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Query against a view or {@code _all_docs}. Keys are any JSON-serializable value: strings, numbers, lists for
 * compound keys, and {@code Map.of()} for the {@code {}} high key. Not thread-safe; build one per query.
 *
 * <p>On {@code _all_docs}, design documents are left out unless {@link #includeDesignDocuments()} is set. The
 * filter applies after {@code limit}, so a page may hold fewer rows than the limit.</p>
 *
 * @param <T> document type of the collection
 */
public final class ViewQuery<T> {

  private final TypedCollection<T> collection;
  private final String path;
  private final boolean allDocs;
  private final Map<String, String> params = new LinkedHashMap<>();
  private List<?> keys;
  private boolean includeDesignDocuments;

  ViewQuery(TypedCollection<T> collection, String path, boolean allDocs) {
    this.collection = collection;
    this.path = path;
    this.allDocs = allDocs;
  }

  public ViewQuery<T> key(Object key) {
    params.put("key", CouchJson.keyJson(key));
    return this;
  }

  /**
   * Restricts the result to the given keys, sent in a {@code POST} body so long key lists do not hit URL limits.
   *
   * @param keys keys
   * @return this query
   */
  public ViewQuery<T> keys(Collection<?> keys) {
    this.keys = List.copyOf(keys);
    return this;
  }

  public ViewQuery<T> startKey(Object startKey) {
    params.put("start_key", CouchJson.keyJson(startKey));
    return this;
  }

  public ViewQuery<T> endKey(Object endKey) {
    params.put("end_key", CouchJson.keyJson(endKey));
    return this;
  }

  public ViewQuery<T> startKeyDocId(String docId) {
    params.put("start_key_doc_id", docId);
    return this;
  }

  public ViewQuery<T> limit(int limit) {
    params.put("limit", Integer.toString(limit));
    return this;
  }

  public ViewQuery<T> skip(int skip) {
    params.put("skip", Integer.toString(skip));
    return this;
  }

  public ViewQuery<T> descending() {
    params.put("descending", "true");
    return this;
  }

  public ViewQuery<T> inclusiveEnd(boolean inclusiveEnd) {
    params.put("inclusive_end", Boolean.toString(inclusiveEnd));
    return this;
  }

  public ViewQuery<T> reduce(boolean reduce) {
    params.put("reduce", Boolean.toString(reduce));
    return this;
  }

  public ViewQuery<T> includeDesignDocuments() {
    this.includeDesignDocuments = true;
    return this;
  }

  /**
   * Documents of the matching rows ({@code include_docs=true}), decoded with the collection's codec. Rows whose
   * document is missing or deleted are skipped.
   *
   * @return the documents
   */
  public List<T> docs() {
    return collection.database().execute(request(true),
        response -> CouchJson.readRowDocuments(response.body(), collection.codec(), collection.type(),
            excludeDesignDocuments()));
  }

  public Optional<T> first() {
    return limit(1).docs().stream().findFirst();
  }

  /**
   * Rows with id, key and value, without documents. Rows with an {@code error} (unknown keys) are skipped.
   *
   * @return the rows
   */
  public List<ViewRow> rows() {
    return collection.database().execute(request(false),
        response -> CouchJson.readRows(response.body(), excludeDesignDocuments()));
  }

  /**
   * Emitted values, converted with the client's Jackson mapper.
   *
   * @param valueType value class
   * @param <V>       value type
   * @return the values
   */
  public <V> List<V> values(Class<V> valueType) {
    return rows().stream().map(row -> {
      try {
        return CouchJson.MAPPER.treeToValue(row.value(), valueType);
      } catch (JsonProcessingException e) {
        throw new DocumentMappingException("Could not convert view value to " + valueType.getName(), e);
      }
    }).toList();
  }

  public List<String> ids() {
    return rows().stream().map(ViewRow::id).toList();
  }

  private boolean excludeDesignDocuments() {
    return allDocs && !includeDesignDocuments;
  }

  private CouchRequest request(boolean includeDocs) {
    var query = new LinkedHashMap<>(params);
    if (includeDocs) {
      query.put("include_docs", "true");
    }
    String url = path + CouchPaths.query(query);
    if (keys != null) {
      return CouchRequest.post(url, CouchJson.write(Map.of("keys", keys)));
    }
    return CouchRequest.get(url);
  }
}
