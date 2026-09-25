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

package org.apache.streampipes.dataexplorer.api.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** A bounded read-only batch from one series. Time uses the owning cursor's declared format; null cells are retained. */
public final class QueryBatch {
  private final List<String> columns;
  private final Map<String, String> tags;
  private final List<List<Object>> rows;

  /** Defensively copies caller-owned rows. */
  public QueryBatch(List<String> columns, Map<String, String> tags, List<List<Object>> rows) {
    this(columns, tags, rows, false);
  }

  private QueryBatch(List<String> columns, Map<String, String> tags, List<List<Object>> rows, boolean owned) {
    this.columns = List.copyOf(columns);
    this.tags = tags == null ? Map.of() : Map.copyOf(tags);
    var immutableRows = new ArrayList<List<Object>>(rows.size());
    for (var row : rows) {
      if (row.size() != columns.size()) {
        throw new IllegalArgumentException("Row width does not match query columns");
      }
      immutableRows.add(Collections.unmodifiableList(owned ? row : new ArrayList<>(row)));
    }
    this.rows = Collections.unmodifiableList(immutableRows);
  }

  /**
   * Transfers exclusively owned row lists without copying their cells. The producer must never mutate
   * or reuse those row lists after this call. Columns, tags and the outer row list are still copied.
   */
  public static QueryBatch takeOwnership(List<String> columns, Map<String, String> tags, List<List<Object>> rows) {
    return new QueryBatch(columns, tags, rows, true);
  }

  public List<String> columns() {
    return columns;
  }

  public Map<String, String> tags() {
    return tags;
  }

  public List<List<Object>> rows() {
    return rows;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof QueryBatch batch
        && columns.equals(batch.columns) && tags.equals(batch.tags) && rows.equals(batch.rows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columns, tags, rows);
  }

  @Override
  public String toString() {
    return "QueryBatch[columns=" + columns + ", tags=" + tags + ", rows=" + rows + "]";
  }
}
