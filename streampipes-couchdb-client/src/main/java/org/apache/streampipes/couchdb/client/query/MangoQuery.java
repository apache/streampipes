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

package org.apache.streampipes.couchdb.client.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Mango query for {@code POST /{db}/_find}.
 *
 * <p>CouchDB returns at most 25 documents when no limit is given. {@code TypedCollection.findAll} pages through all
 * matches with bookmarks instead, up to the limit if one is set. Not thread-safe.</p>
 */
public final class MangoQuery {

  private static final Set<String> KNOWN_KEYS =
      Set.of("selector", "fields", "sort", "limit", "skip", "bookmark", "use_index");

  private Map<String, Object> selector;
  private List<String> fields;
  private final List<Map<String, String>> sort = new ArrayList<>();
  private Integer limit;
  private Integer skip;
  private String bookmark;
  private Object useIndex;
  private final Map<String, Object> otherOptions = new LinkedHashMap<>();

  private MangoQuery(Map<String, Object> selector) {
    this.selector = selector;
  }

  public static MangoQuery where(Selector selector) {
    return new MangoQuery(selector.toMap());
  }

  public static MangoQuery where(Map<String, Object> selector) {
    return new MangoQuery(selector);
  }

  /**
   * Query from a full request body in Mango syntax ({@code selector}, {@code fields}, {@code limit}, ...), as built
   * by existing code. Other options ({@code execution_stats}, {@code r}, ...) are sent unchanged.
   *
   * @param query request body
   * @return the query
   */
  @SuppressWarnings("unchecked")
  public static MangoQuery fromMap(Map<String, Object> query) {
    var selector = query.get("selector");
    var result = new MangoQuery(selector instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of());
    if (query.get("fields") instanceof List<?> list) {
      result.fields = list.stream().map(String::valueOf).toList();
    }
    if (query.get("sort") instanceof List<?> list) {
      for (Object entry : list) {
        if (entry instanceof Map<?, ?> map) {
          map.forEach((k, v) -> result.sort.add(Map.of(String.valueOf(k), String.valueOf(v))));
        } else {
          result.sort.add(Map.of(String.valueOf(entry), "asc"));
        }
      }
    }
    if (query.get("limit") instanceof Number number) {
      result.limit = number.intValue();
    }
    if (query.get("skip") instanceof Number number) {
      result.skip = number.intValue();
    }
    if (query.get("bookmark") instanceof String value) {
      result.bookmark = value;
    }
    result.useIndex = query.get("use_index");
    query.forEach((key, value) -> {
      if (!KNOWN_KEYS.contains(key)) {
        result.otherOptions.put(key, value);
      }
    });
    return result;
  }

  public MangoQuery fields(String... fields) {
    this.fields = List.of(fields);
    return this;
  }

  public MangoQuery fields(List<String> fields) {
    this.fields = List.copyOf(fields);
    return this;
  }

  public MangoQuery sortAscending(String field) {
    sort.add(Map.of(field, "asc"));
    return this;
  }

  public MangoQuery sortDescending(String field) {
    sort.add(Map.of(field, "desc"));
    return this;
  }

  public MangoQuery limit(int limit) {
    this.limit = limit;
    return this;
  }

  public MangoQuery skip(int skip) {
    this.skip = skip;
    return this;
  }

  public MangoQuery bookmark(String bookmark) {
    this.bookmark = bookmark;
    return this;
  }

  public MangoQuery useIndex(String designDocument) {
    this.useIndex = designDocument;
    return this;
  }

  public MangoQuery useIndex(String designDocument, String indexName) {
    this.useIndex = List.of(designDocument, indexName);
    return this;
  }

  public Integer getLimit() {
    return limit;
  }

  public Integer getSkip() {
    return skip;
  }

  public String getBookmark() {
    return bookmark;
  }

  public MangoQuery copy() {
    var copy = new MangoQuery(selector);
    copy.fields = fields;
    copy.sort.addAll(sort);
    copy.limit = limit;
    copy.skip = skip;
    copy.bookmark = bookmark;
    copy.useIndex = useIndex;
    copy.otherOptions.putAll(otherOptions);
    return copy;
  }

  /**
   * Clears skip, for follow-up pages that continue from a bookmark.
   *
   * @return this query
   */
  public MangoQuery clearSkip() {
    this.skip = null;
    return this;
  }

  /**
   * Clears the limit, so that {@code TypedCollection.findAll} returns every match.
   *
   * @return this query
   */
  public MangoQuery clearLimit() {
    this.limit = null;
    return this;
  }

  public Map<String, Object> toMap() {
    var body = new LinkedHashMap<String, Object>();
    body.put("selector", selector);
    if (fields != null) {
      body.put("fields", fields);
    }
    if (!sort.isEmpty()) {
      body.put("sort", sort);
    }
    if (limit != null) {
      body.put("limit", limit);
    }
    if (skip != null) {
      body.put("skip", skip);
    }
    if (bookmark != null) {
      body.put("bookmark", bookmark);
    }
    if (useIndex != null) {
      body.put("use_index", useIndex);
    }
    body.putAll(otherOptions);
    return body;
  }
}
