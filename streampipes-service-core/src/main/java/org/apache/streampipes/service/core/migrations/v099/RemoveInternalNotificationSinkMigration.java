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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoveInternalNotificationSinkMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(RemoveInternalNotificationSinkMigration.class);

  static final String INTERNAL_NOTIFICATION_SINK_APP_ID =
      "org.apache.streampipes.sinks.internal.jvm.notification";
  static final String NOTIFICATION_DB_NAME = "notification";
  static final String PIPELINE_WARNING =
      "Internal notification sink was removed during migration. Review and update this pipeline before using it again.";

  private final IPipelineStorage pipelineStorage;
  private final IDataSinkStorage dataSinkStorage;

  public RemoveInternalNotificationSinkMigration(IPipelineStorage pipelineStorage,
                                                 IDataSinkStorage dataSinkStorage) {
    this.pipelineStorage = pipelineStorage;
    this.dataSinkStorage = dataSinkStorage;
  }

  public RemoveInternalNotificationSinkMigration(IPipelineStorage pipelineStorage) {
    this(pipelineStorage,
        StorageDispatcher.INSTANCE.getNoSqlStore().getDataSinkStorage());
  }

  @Override
  public boolean shouldExecute() {
    return databaseExists()
        || containsDeprecatedSinkInPipelines()
        || !dataSinkStorage.getDataSinksByAppId(INTERNAL_NOTIFICATION_SINK_APP_ID).isEmpty();
  }

  @Override
  public void executeMigration() throws IOException {
    migratePipelines();
    removeSinkDescriptions();
    removeDatabase();
  }

  @Override
  public String getDescription() {
    return "Remove deprecated internal notification sink and notification database";
  }

  private boolean containsDeprecatedSinkInPipelines() {
    return pipelineStorage.findAll()
        .stream()
        .map(Pipeline::getActions)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .map(DataSinkInvocation::getAppId)
        .anyMatch(INTERNAL_NOTIFICATION_SINK_APP_ID::equals);
  }

  private void migratePipelines() {
    pipelineStorage.findAll().forEach(pipeline -> {
      var actions = pipeline.getActions();
      if (actions == null || actions.isEmpty()) {
        return;
      }

      var remainingActions = actions.stream()
          .filter(action -> !INTERNAL_NOTIFICATION_SINK_APP_ID.equals(action.getAppId()))
          .toList();

      if (remainingActions.size() != actions.size()) {
        pipeline.setActions(remainingActions);
        pipeline.setRunning(false);
        pipeline.setValid(false);
        pipeline.setRestartOnSystemReboot(false);
        pipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);
        pipeline.setPipelineNotifications(appendMigrationWarning(pipeline.getPipelineNotifications()));
        pipelineStorage.updateElement(pipeline);
        LOG.info("Removed deprecated internal notification sink from pipeline '{}'", pipeline.getPipelineId());
      }
    });
  }

  private List<String> appendMigrationWarning(List<String> existingWarnings) {
    var warnings = existingWarnings == null ? new ArrayList<String>() : new ArrayList<>(existingWarnings);
    if (!warnings.contains(PIPELINE_WARNING)) {
      warnings.add(PIPELINE_WARNING);
    }
    return warnings;
  }

  private void removeSinkDescriptions() {
    dataSinkStorage.getDataSinksByAppId(INTERNAL_NOTIFICATION_SINK_APP_ID)
        .forEach(dataSinkStorage::deleteElement);
  }

  protected boolean databaseExists() {
    try {
      var response = Utils.getRequest(Utils.getDatabaseRoute(NOTIFICATION_DB_NAME))
          .execute()
          .returnResponse();
      int statusCode = response.getStatusLine().getStatusCode();
      return statusCode == HttpStatus.SC_OK;
    } catch (IOException e) {
      LOG.warn("Could not determine whether notification database exists", e);
      return false;
    }
  }

  protected void removeDatabase() throws IOException {
    var response = Utils.deleteRequest(Utils.getDatabaseRoute(NOTIFICATION_DB_NAME))
        .execute()
        .returnResponse();
    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED
        || statusCode == HttpStatus.SC_NOT_FOUND) {
      LOG.info("Notification database removal finished with status {}", statusCode);
    } else {
      throw new IOException("Unexpected response while deleting notification database: " + statusCode);
    }
  }
}
