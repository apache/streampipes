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

import org.apache.streampipes.model.dataset.DatasetMetadata;

import java.util.List;
import java.util.Map;

/** Storage SPI. No HTTP parameters, export formats or presentation metadata cross this boundary. */
public interface DatasetQueryBackend extends AutoCloseable {
  /** Releases application-owned query resources at shutdown, never after an individual query. */
  @Override
  default void close() {
  }

  QueryCapabilities capabilities();

  /** Optional optimized timestamps, keyed by catalog ID; omitted IDs use a typed-query fallback. */
  default Map<String, Long> latestTimestamps(List<DatasetMetadata> datasets) {
    return Map.of();
  }

  DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query);

  /** Backends may push down timestamp encoding; the cursor declares its actual representation. */
  default DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query, QueryExecutionOptions options) {
    return open(dataset, query);
  }
}
