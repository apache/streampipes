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

import org.apache.streampipes.audit.api.AuditDetails;
import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasetAuditReporterTest {

  @Test
  void reportsDatasetLifecycleEvents() {
    var auditService = new RecordingAuditService();
    var reporter = new DatasetAuditReporter(auditService);

    reporter.created("user-1", "dataset-1");
    reporter.edited("user-1", "dataset-1");
    reporter.deleted("user-1", "dataset-1");

    assertEquals(List.of(
        new AuditRecord(StandardAuditEvents.DATASET_CREATE, AuditOutcome.SUCCEEDED,
            "user-1", "dataset-1", new DatasetCreatedDetails()),
        new AuditRecord(StandardAuditEvents.DATASET_EDIT, AuditOutcome.SUCCEEDED,
            "user-1", "dataset-1", new DatasetEditedDetails()),
        new AuditRecord(StandardAuditEvents.DATASET_DELETE, AuditOutcome.SUCCEEDED,
            "user-1", "dataset-1", new DatasetDeletedDetails())
    ), auditService.records);
  }

  private static final class RecordingAuditService implements AuditService {
    private final List<AuditRecord> records = new ArrayList<>();

    @Override
    public <T extends AuditDetails> void record(AuditEventDefinition<T> definition,
                                                AuditOutcome outcome,
                                                String actor,
                                                String resourceId,
                                                T details) {
      records.add(new AuditRecord(definition, outcome, actor, resourceId, details));
    }
  }

  private record AuditRecord(AuditEventDefinition<?> definition,
                            AuditOutcome outcome,
                            String actor,
                            String resourceId,
                            AuditDetails details) {
  }
}
