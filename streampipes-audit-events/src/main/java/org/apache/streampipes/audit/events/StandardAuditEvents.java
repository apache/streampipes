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

package org.apache.streampipes.audit.events;

import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditEventProvider;
import org.apache.streampipes.audit.events.adapter.AdapterCreatedDetails;
import org.apache.streampipes.audit.events.adapter.AdapterEditedDetails;
import org.apache.streampipes.audit.events.adapter.AdapterLifecycleDetails;
import org.apache.streampipes.audit.events.authentication.AuthenticationDetails;
import org.apache.streampipes.audit.events.dataset.DatasetCreatedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetDataClearedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetDataDeletedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetDataTruncatedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetDeletedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetDroppedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetEditedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetRetentionConfiguredDetails;
import org.apache.streampipes.audit.events.dataset.DatasetRetentionRemovedDetails;
import org.apache.streampipes.audit.events.dataset.DatasetRetentionRunDetails;

import java.util.List;

public final class StandardAuditEvents implements AuditEventProvider {

  public static final AuditEventDefinition<DatasetCreatedDetails> DATASET_CREATE =
      new AuditEventDefinition<>("sp.dataset.create", DatasetCreatedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetEditedDetails> DATASET_EDIT =
      new AuditEventDefinition<>("sp.dataset.edit", DatasetEditedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetDeletedDetails> DATASET_DELETE =
      new AuditEventDefinition<>("sp.dataset.delete", DatasetDeletedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetDataDeletedDetails> DATASET_DATA_DELETE =
      new AuditEventDefinition<>("sp.dataset.data.delete", DatasetDataDeletedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetDataTruncatedDetails> DATASET_DATA_TRUNCATE =
      new AuditEventDefinition<>("sp.dataset.data.truncate", DatasetDataTruncatedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetDroppedDetails> DATASET_DROP =
      new AuditEventDefinition<>("sp.dataset.drop", DatasetDroppedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetDataClearedDetails> DATASET_DATA_CLEAR =
      new AuditEventDefinition<>("sp.dataset.data.clear", DatasetDataClearedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetRetentionConfiguredDetails> DATASET_RETENTION_CONFIGURE =
      new AuditEventDefinition<>("sp.dataset.retention.configure", DatasetRetentionConfiguredDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetRetentionRemovedDetails> DATASET_RETENTION_REMOVE =
      new AuditEventDefinition<>("sp.dataset.retention.remove", DatasetRetentionRemovedDetails.class, "dataset");
  public static final AuditEventDefinition<DatasetRetentionRunDetails> DATASET_RETENTION_RUN =
      new AuditEventDefinition<>("sp.dataset.retention.run", DatasetRetentionRunDetails.class, "dataset");

  public static final AuditEventDefinition<AdapterCreatedDetails> ADAPTER_CREATE =
      new AuditEventDefinition<>("sp.adapter.create", AdapterCreatedDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterLifecycleDetails> ADAPTER_START =
      new AuditEventDefinition<>("sp.adapter.start", AdapterLifecycleDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterLifecycleDetails> ADAPTER_STOP =
      new AuditEventDefinition<>("sp.adapter.stop", AdapterLifecycleDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterEditedDetails> ADAPTER_EDIT =
      new AuditEventDefinition<>("sp.adapter.edit", AdapterEditedDetails.class, "adapter");

  public static final AuditEventDefinition<AuthenticationDetails> AUTH_LOGIN =
      new AuditEventDefinition<>("sp.auth.login", AuthenticationDetails.class);
  public static final AuditEventDefinition<AuthenticationDetails> AUTH_LOGOUT =
      new AuditEventDefinition<>("sp.auth.logout", AuthenticationDetails.class);


  private static final List<AuditEventDefinition<?>> EVENT_TYPES = List.of(
      DATASET_CREATE, DATASET_EDIT, DATASET_DELETE,
      DATASET_DATA_DELETE, DATASET_DATA_TRUNCATE, DATASET_DROP, DATASET_DATA_CLEAR,
      DATASET_RETENTION_CONFIGURE, DATASET_RETENTION_REMOVE, DATASET_RETENTION_RUN,
      ADAPTER_CREATE, ADAPTER_START, ADAPTER_STOP, ADAPTER_EDIT, AUTH_LOGIN, AUTH_LOGOUT);

  @Override
  public List<AuditEventDefinition<?>> eventTypes() {
    return EVENT_TYPES;
  }
}
