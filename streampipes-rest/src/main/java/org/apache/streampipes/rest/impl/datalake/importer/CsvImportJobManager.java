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

package org.apache.streampipes.rest.impl.datalake.importer;

import org.apache.streampipes.model.dataset.importer.CsvImportJobStartResult;
import org.apache.streampipes.model.dataset.importer.CsvImportJobState;
import org.apache.streampipes.model.dataset.importer.CsvImportJobStatus;
import org.apache.streampipes.model.dataset.importer.CsvImportRequest;
import org.apache.streampipes.model.dataset.importer.CsvImportResult;
import org.apache.streampipes.model.dataset.importer.CsvImportValidationMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CsvImportJobManager {

  private static final Duration DEFAULT_TTL = Duration.ofHours(12);

  private final CsvDataLakeImportService importService;
  private final ConcurrentMap<String, StoredJob> jobs;
  private final ExecutorService executorService;
  private final Duration ttl;

  CsvImportJobManager(CsvDataLakeImportService importService) {
    this(importService, Executors.newCachedThreadPool(), DEFAULT_TTL);
  }

  CsvImportJobManager(
      CsvDataLakeImportService importService,
      ExecutorService executorService,
      Duration ttl) {
    this.importService = importService;
    this.executorService = executorService;
    this.ttl = ttl;
    this.jobs = new ConcurrentHashMap<>();
  }

  CsvImportJobStartResult start(CsvImportRequest request, String ownerSid, int totalRows) {
    cleanupExpired();

    var jobId = UUID.randomUUID().toString();
    var job = new StoredJob(jobId, ownerSid, totalRows);
    jobs.put(jobId, job);

    executorService.submit(() -> runImport(job, request, ownerSid));
    return new CsvImportJobStartResult(jobId);
  }

  Optional<CsvImportJobStatus> getStatus(String jobId, String ownerSid) {
    cleanupExpired();

    return Optional.ofNullable(jobs.get(jobId))
        .filter(job -> Objects.equals(job.ownerSid(), ownerSid))
        .map(StoredJob::status);
  }

  private void runImport(StoredJob job, CsvImportRequest request, String ownerSid) {
    try {
      var result = importService.importData(request, ownerSid, job::setProcessedRows);
      job.succeeded(result);
    } catch (CsvImportValidationException e) {
      job.failed(e.getValidationMessages());
    } catch (RuntimeException e) {
      job.failed(List.of(new CsvImportValidationMessage("import", "CSV import failed.")));
    } finally {
      importService.cleanupUpload(request);
    }
  }

  private void cleanupExpired() {
    var expiresBefore = Instant.now().minus(ttl);
    jobs.entrySet().removeIf(entry -> entry.getValue().createdAt().isBefore(expiresBefore));
  }

  private static final class StoredJob {

    private final String jobId;
    private final String ownerSid;
    private final int totalRows;
    private final Instant createdAt;
    private CsvImportJobState state;
    private int processedRows;
    private CsvImportResult result;
    private List<CsvImportValidationMessage> validationMessages;

    private StoredJob(String jobId, String ownerSid, int totalRows) {
      this.jobId = jobId;
      this.ownerSid = ownerSid;
      this.totalRows = totalRows;
      this.createdAt = Instant.now();
      this.state = CsvImportJobState.RUNNING;
      this.validationMessages = List.of();
    }

    synchronized CsvImportJobStatus status() {
      var status = new CsvImportJobStatus();
      status.setJobId(jobId);
      status.setState(state);
      status.setProcessedRows(processedRows);
      status.setTotalRows(totalRows);
      status.setProgress(calculateProgress());
      status.setResult(result);
      status.setValidationMessages(validationMessages);
      return status;
    }

    synchronized void setProcessedRows(int processedRows) {
      this.processedRows = Math.min(processedRows, totalRows);
    }

    synchronized void succeeded(CsvImportResult result) {
      this.result = result;
      this.processedRows = totalRows > 0 ? totalRows : result.getImportedRowCount();
      this.state = CsvImportJobState.SUCCEEDED;
      this.validationMessages = List.of();
    }

    synchronized void failed(List<CsvImportValidationMessage> validationMessages) {
      this.state = CsvImportJobState.FAILED;
      this.validationMessages = validationMessages;
    }

    String ownerSid() {
      return ownerSid;
    }

    Instant createdAt() {
      return createdAt;
    }

    private int calculateProgress() {
      if (totalRows <= 0) {
        return state == CsvImportJobState.SUCCEEDED ? 100 : 0;
      }
      return Math.min(100, Math.round((processedRows * 100.0f) / totalRows));
    }
  }
}
