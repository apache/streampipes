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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileCheckpointStoreTest {

  private static final java.util.List<MsSqlColumn> SCHEMA = java.util.List.of(
      new MsSqlColumn("id", java.sql.Types.BIGINT, "bigint", 19, 0, false)
  );

  @TempDir
  Path directory;

  @Test
  void preservesExactCursorAndRejectsSaveAfterDeletion() throws Exception {
    FileCheckpointStore store = new FileCheckpointStore(directory);
    BigDecimal cursor = new BigDecimal("99999999999999999999999999999999999999");

    CheckpointSnapshot saved = store.save("urn:adapter/one", 0, Optional.of(cursor)).orElseThrow();
    assertEquals(cursor, store.load("urn:adapter/one").cursor().orElseThrow());
    assertTrue(Files.readString(directory.resolve("urn_adapter_one.json")).contains(cursor.toPlainString()));
    assertTrue(store.delete("urn:adapter/one", saved.revision()));
    assertFalse(store.save("urn:adapter/one", saved.revision(), Optional.of(BigDecimal.TEN)).isPresent());
    assertFalse(store.load("urn:adapter/one").present());

    assertEquals(saved.revision() + 1, store.load("urn:adapter/one").revision());
  }

  @Test
  void preservesGuessedSchemaAcrossStoreInstancesAndCheckpointChanges() throws Exception {
    FileCheckpointStore first = new FileCheckpointStore(directory);
    first.saveExpectedSchema("adapter-1", SCHEMA);
    CheckpointSnapshot saved = first.save("adapter-1", 0, Optional.of(BigDecimal.TEN)).orElseThrow();
    assertTrue(first.delete("adapter-1", saved.revision()));
    Files.delete(first.statePath("adapter-1"));

    FileCheckpointStore restarted = new FileCheckpointStore(directory);
    assertEquals(SCHEMA, restarted.loadExpectedSchema("adapter-1").orElseThrow());
  }
}
