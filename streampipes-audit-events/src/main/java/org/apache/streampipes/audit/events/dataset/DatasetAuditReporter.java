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

package org.apache.streampipes.audit.events.dataset;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import java.util.Objects;

/** Publishes dataset lifecycle events with safe, resource-free details. */
public final class DatasetAuditReporter {
  private final AuditService auditService;

  public DatasetAuditReporter(AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
  }

  public void created(String actor, String datasetId) {
    auditService.record(StandardAuditEvents.DATASET_CREATE, AuditOutcome.SUCCEEDED, actor, datasetId,
        new DatasetCreatedDetails());
  }

  public void edited(String actor, String datasetId) {
    auditService.record(StandardAuditEvents.DATASET_EDIT, AuditOutcome.SUCCEEDED, actor, datasetId,
        new DatasetEditedDetails());
  }

  public void deleted(String actor, String datasetId) {
    auditService.record(StandardAuditEvents.DATASET_DELETE, AuditOutcome.SUCCEEDED, actor, datasetId,
        new DatasetDeletedDetails());
  }
}
