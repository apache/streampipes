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

package org.apache.streampipes.rest.impl.datalake;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CsvImportUploadStorage {

  private static final Duration DEFAULT_TTL = Duration.ofHours(12);

  private final ConcurrentMap<String, StoredUpload> uploads;
  private final Duration ttl;

  CsvImportUploadStorage() {
    this(DEFAULT_TTL);
  }

  CsvImportUploadStorage(Duration ttl) {
    this.uploads = new ConcurrentHashMap<>();
    this.ttl = ttl;
  }

  StoredUpload store(MultipartFile file, String ownerSid) throws IOException {
    cleanupExpired();

    var uploadId = UUID.randomUUID().toString();
    var tempFile = Files.createTempFile("streampipes-csv-import-", ".csv");
    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
    }

    var upload = new StoredUpload(
        uploadId,
        tempFile,
        ownerSid,
        Instant.now()
    );
    uploads.put(uploadId, upload);
    return upload;
  }

  Optional<StoredUpload> get(String uploadId) {
    cleanupExpired();
    return Optional.ofNullable(uploads.get(uploadId));
  }

  void remove(String uploadId) {
    var removed = uploads.remove(uploadId);
    if (removed != null) {
      deleteQuietly(removed.path());
    }
  }

  private void cleanupExpired() {
    var expiresBefore = Instant.now().minus(ttl);
    uploads.entrySet().removeIf(entry -> {
      var expired = entry.getValue().createdAt().isBefore(expiresBefore);
      if (expired) {
        deleteQuietly(entry.getValue().path());
      }
      return expired;
    });
  }

  private void deleteQuietly(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException ignored) {
      // Best-effort cleanup of temporary uploads.
    }
  }

  static final class StoredUpload {

    private final String uploadId;
    private final Path path;
    private final String ownerSid;
    private final Instant createdAt;

    StoredUpload(String uploadId, Path path, String ownerSid, Instant createdAt) {
      this.uploadId = uploadId;
      this.path = path;
      this.ownerSid = ownerSid;
      this.createdAt = createdAt;
    }

    String uploadId() {
      return uploadId;
    }

    Path path() {
      return path;
    }

    String ownerSid() {
      return ownerSid;
    }

    Instant createdAt() {
      return createdAt;
    }
  }
}
