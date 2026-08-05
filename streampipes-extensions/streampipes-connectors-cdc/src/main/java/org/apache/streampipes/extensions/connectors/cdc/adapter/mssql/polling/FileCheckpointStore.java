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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class FileCheckpointStore implements CheckpointStore {

  private static final ObjectMapper MAPPER = JacksonSerializer.getObjectMapper();

  private final Path baseDirectory;

  public FileCheckpointStore() {
    this(resolveDefaultDirectory());
  }

  public FileCheckpointStore(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  @Override
  public CheckpointSnapshot load(String adapterElementId) throws IOException {
    return withLock(adapterElementId, () -> loadUnlocked(adapterElementId));
  }

  @Override
  public Optional<CheckpointSnapshot> save(String adapterElementId,
                                           long expectedRevision,
                                           Optional<BigDecimal> cursor) throws IOException {
    return withLock(adapterElementId, () -> {
      PersistedCheckpoint persisted = loadPersistedUnlocked(adapterElementId);
      CheckpointSnapshot current = persisted.toSnapshot();
      if (current.revision() != expectedRevision) {
        return Optional.empty();
      }

      CheckpointSnapshot saved = CheckpointSnapshot.present(cursor, expectedRevision + 1);
      persisted.apply(saved);
      writeAtomically(adapterElementId, persisted);
      return Optional.of(saved);
    });
  }

  @Override
  public boolean delete(String adapterElementId, long expectedRevision) throws IOException {
    return withLock(adapterElementId, () -> {
      PersistedCheckpoint persisted = loadPersistedUnlocked(adapterElementId);
      CheckpointSnapshot current = persisted.toSnapshot();
      if (current.revision() != expectedRevision) {
        return false;
      }

      persisted.apply(CheckpointSnapshot.absent(expectedRevision + 1));
      writeAtomically(adapterElementId, persisted);
      return true;
    });
  }

  @Override
  public Optional<List<MsSqlColumn>> loadExpectedSchema(String adapterElementId) throws IOException {
    return withLock(adapterElementId, () -> {
      Path path = schemaPath(adapterElementId);
      if (!Files.exists(path)) {
        return Optional.empty();
      }
      return Optional.of(List.of(MAPPER.readValue(path.toFile(), MsSqlColumn[].class)));
    });
  }

  @Override
  public void saveExpectedSchema(String adapterElementId, List<MsSqlColumn> schema) throws IOException {
    withLock(adapterElementId, () -> {
      writeAtomically(schemaPath(adapterElementId), List.copyOf(schema));
      return null;
    });
  }

  public Path statePath(String adapterElementId) {
    return baseDirectory.resolve(sanitizeAdapterElementId(adapterElementId) + ".json");
  }

  public Path schemaPath(String adapterElementId) {
    return baseDirectory.resolve(sanitizeAdapterElementId(adapterElementId) + ".schema.json");
  }

  private CheckpointSnapshot loadUnlocked(String adapterElementId) throws IOException {
    return loadPersistedUnlocked(adapterElementId).toSnapshot();
  }

  private PersistedCheckpoint loadPersistedUnlocked(String adapterElementId) throws IOException {
    Path path = statePath(adapterElementId);
    if (!Files.exists(path)) {
      return PersistedCheckpoint.from(CheckpointSnapshot.absent(0));
    }

    return MAPPER.readValue(path.toFile(), PersistedCheckpoint.class);
  }

  private void writeAtomically(String adapterElementId, PersistedCheckpoint checkpoint) throws IOException {
    writeAtomically(statePath(adapterElementId), checkpoint);
  }

  private void writeAtomically(Path target, Object value) throws IOException {
    Files.createDirectories(baseDirectory);
    Path temporary = Files.createTempFile(baseDirectory, target.getFileName().toString(), ".tmp");
    try {
      MAPPER.writeValue(temporary.toFile(), value);
      Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  private <T> T withLock(String adapterElementId, IoSupplier<T> operation) throws IOException {
    Files.createDirectories(baseDirectory);
    Path lockPath = baseDirectory.resolve(sanitizeAdapterElementId(adapterElementId) + ".lock");
    try (FileChannel channel = FileChannel.open(
        lockPath,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE
    ); FileLock ignored = channel.lock()) {
      return operation.get();
    }
  }

  private String sanitizeAdapterElementId(String adapterElementId) {
    if (adapterElementId == null || adapterElementId.isBlank()) {
      throw new IllegalArgumentException("Adapter element ID is required for checkpoint persistence.");
    }
    return adapterElementId.replaceAll("[^A-Za-z0-9._-]", "_");
  }

  private static Path resolveDefaultDirectory() {
    String baseDir = Environments.getEnvironment()
        .getExtAssetBaseDir()
        .getValueOrReturn(System.getProperty("user.home"));
    return Path.of(baseDir, ".streampipes", "service", "mssql-table-polling-checkpoints");
  }

  @FunctionalInterface
  private interface IoSupplier<T> {
    T get() throws IOException;
  }

  public static class PersistedCheckpoint {
    private boolean present;
    private String cursor;
    private long revision;

    public PersistedCheckpoint() {
    }

    static PersistedCheckpoint from(CheckpointSnapshot snapshot) {
      PersistedCheckpoint checkpoint = new PersistedCheckpoint();
      checkpoint.present = snapshot.present();
      checkpoint.cursor = snapshot.cursor().map(BigDecimal::toPlainString).orElse(null);
      checkpoint.revision = snapshot.revision();
      return checkpoint;
    }

    void apply(CheckpointSnapshot snapshot) {
      this.present = snapshot.present();
      this.cursor = snapshot.cursor().map(BigDecimal::toPlainString).orElse(null);
      this.revision = snapshot.revision();
    }

    CheckpointSnapshot toSnapshot() {
      Optional<BigDecimal> parsedCursor = cursor == null ? Optional.empty() : Optional.of(new BigDecimal(cursor));
      return new CheckpointSnapshot(present, parsedCursor, revision);
    }

    public boolean isPresent() {
      return present;
    }

    public void setPresent(boolean present) {
      this.present = present;
    }

    public String getCursor() {
      return cursor;
    }

    public void setCursor(String cursor) {
      this.cursor = cursor;
    }

    public long getRevision() {
      return revision;
    }

    public void setRevision(long revision) {
      this.revision = revision;
    }
  }
}
