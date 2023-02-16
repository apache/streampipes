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

package org.apache.streampipes.manager.execution.task;

import org.apache.streampipes.manager.execution.PipelineExecutionInfo;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.lightcouch.DocumentConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class StorePipelineStatusTask implements PipelineExecutionTask {

  private static final Logger LOG = LoggerFactory.getLogger(StorePipelineStatusTask.class);

  private final boolean start;
  private final boolean forceStop;

  public StorePipelineStatusTask(boolean start,
                                 boolean forceStop) {
    this.start = start;
    this.forceStop = forceStop;
  }

  @Override
  public boolean shouldExecute(PipelineExecutionInfo executionInfo) {
    return executionInfo.isOperationSuccessful() || forceStop;
  }

  @Override
  public void executeTask(Pipeline pipeline,
                          PipelineExecutionInfo executionInfo) {
    if (this.start) {
      pipeline.setHealthStatus(PipelineHealthStatus.OK);
      setPipelineStarted(pipeline);
    } else {
      setPipelineStopped(pipeline);
    }
  }

  private void setPipelineStarted(Pipeline pipeline) {
    pipeline.setRunning(true);
    pipeline.setStartedAt(new Date().getTime());
    try {
      getPipelineStorageApi().updatePipeline(pipeline);
    } catch (DocumentConflictException dce) {
      LOG.error("Could not update pipeline {}", pipeline.getPipelineId(), dce);
    }
  }

  private void setPipelineStopped(Pipeline pipeline) {
    pipeline.setRunning(false);
    getPipelineStorageApi().updatePipeline(pipeline);
  }

  private IPipelineStorage getPipelineStorageApi() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
  }
}
