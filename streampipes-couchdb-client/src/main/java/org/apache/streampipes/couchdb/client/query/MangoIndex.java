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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition of a JSON Mango index for {@code POST /{db}/_index}.
 *
 * @param fields          indexed fields, in order
 * @param name            index name, {@code null} to let CouchDB derive one
 * @param designDocument  design document holding the index, {@code null} to let CouchDB derive one
 * @param partialFilter   partial filter selector, {@code null} for none
 */
public record MangoIndex(List<String> fields,
                         String name,
                         String designDocument,
                         Map<String, Object> partialFilter) {

  public MangoIndex {
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("An index needs at least one field");
    }
    fields = List.copyOf(fields);
  }

  public static MangoIndex on(String... fields) {
    return new MangoIndex(List.of(fields), null, null, null);
  }

  public MangoIndex named(String name) {
    return new MangoIndex(fields, name, designDocument, partialFilter);
  }

  public MangoIndex inDesignDocument(String designDocument) {
    return new MangoIndex(fields, name, designDocument, partialFilter);
  }

  public MangoIndex partialFilter(Selector selector) {
    return new MangoIndex(fields, name, designDocument, selector.toMap());
  }

  public Map<String, Object> toMap() {
    var index = new LinkedHashMap<String, Object>();
    index.put("fields", fields);
    if (partialFilter != null) {
      index.put("partial_filter_selector", partialFilter);
    }
    var body = new LinkedHashMap<String, Object>();
    body.put("index", index);
    body.put("type", "json");
    if (name != null) {
      body.put("name", name);
    }
    if (designDocument != null) {
      body.put("ddoc", designDocument);
    }
    return body;
  }
}
