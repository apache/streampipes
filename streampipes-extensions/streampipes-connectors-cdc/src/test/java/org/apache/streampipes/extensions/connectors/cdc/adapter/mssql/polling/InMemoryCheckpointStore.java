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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryCheckpointStore implements CheckpointStore {

  private final Map<String, CheckpointSnapshot> snapshots = new HashMap<>();
  private final Map<String, java.util.List<MsSqlColumn>> schemas = new HashMap<>();

  @Override
  public synchronized CheckpointSnapshot load(String adapterElementId) {
    return snapshots.getOrDefault(adapterElementId, CheckpointSnapshot.absent(0));
  }

  @Override
  public synchronized Optional<CheckpointSnapshot> save(String adapterElementId,
                                                        long expectedRevision,
                                                        Optional<BigDecimal> cursor) {
    CheckpointSnapshot current = load(adapterElementId);
    if (current.revision() != expectedRevision) {
      return Optional.empty();
    }

    CheckpointSnapshot saved = CheckpointSnapshot.present(cursor, expectedRevision + 1);
    snapshots.put(adapterElementId, saved);
    return Optional.of(saved);
  }

  @Override
  public synchronized boolean delete(String adapterElementId, long expectedRevision) {
    CheckpointSnapshot current = load(adapterElementId);
    if (current.revision() != expectedRevision) {
      return false;
    }

    snapshots.put(adapterElementId, CheckpointSnapshot.absent(expectedRevision + 1));
    return true;
  }

  @Override
  public synchronized Optional<java.util.List<MsSqlColumn>> loadExpectedSchema(String adapterElementId) {
    return Optional.ofNullable(schemas.get(adapterElementId)).map(java.util.List::copyOf);
  }

  @Override
  public synchronized void saveExpectedSchema(String adapterElementId, java.util.List<MsSqlColumn> schema) {
    schemas.put(adapterElementId, java.util.List.copyOf(schema));
  }
}
