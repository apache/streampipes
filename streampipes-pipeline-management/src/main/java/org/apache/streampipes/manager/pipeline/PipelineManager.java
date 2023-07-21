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

package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.commons.random.UUIDGenerator;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.permission.PermissionManager;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.NotificationsResourceManager;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipelineManager {

  /**
   * Returns all pipelines
   *
   * @return all pipelines
   */
  public static List<Pipeline> getAllPipelines() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getAllPipelines();
  }

  /**
   * Returns the stored pipeline with the given pipeline id
   *
   * @param pipelineId id of pipeline
   * @return pipeline resulting pipeline with given id
   */
  public static Pipeline getPipeline(String pipelineId) {
    return getPipelineStorage().getPipeline(pipelineId);
  }

  /**
   * Adds a new pipeline for the user with the username to the storage
   *
   * @param principalSid the ID of the owner principal
   * @param pipeline     to be added
   * @return pipelineId of the stored pipeline
   */
  public static String addPipeline(String principalSid,
                                   Pipeline pipeline) {

    String pipelineId = Objects.isNull(pipeline.getPipelineId())
        ? UUIDGenerator.generateUuid()
        : pipeline.getPipelineId();
    preparePipelineBasics(principalSid, pipeline, pipelineId);
    Operations.storePipeline(pipeline);

    Permission permission = new PermissionManager().makePermission(pipeline, principalSid);
    getPermissionStorage().addPermission(permission);

    return pipelineId;
  }


  /**
   * Starts all processing elements of the pipeline with the pipelineId
   *
   * @param pipelineId of pipeline to be started
   * @return pipeline status of the start operation
   */
  public static PipelineOperationStatus startPipeline(String pipelineId) {
    Pipeline pipeline = getPipeline(pipelineId);
    return Operations.startPipeline(pipeline);
  }

  /**
   * Stops all  processing elements of the pipeline
   *
   * @param pipelineId of pipeline to be stopped
   * @param forceStop  when it is true, the pipeline is stopped, even if not all processing element
   *                   containers could be reached
   * @return pipeline status of the start operation
   */
  public static PipelineOperationStatus stopPipeline(String pipelineId,
                                                     boolean forceStop) {
    Pipeline pipeline = getPipeline(pipelineId);

    return Operations.stopPipeline(pipeline, forceStop);
  }

  /**
   * Deletes the pipeline with the pipeline Id
   *
   * @param pipelineId of pipeline to be deleted
   */
  public static void deletePipeline(String pipelineId) {
    var pipeline = getPipeline(pipelineId);
    if (Objects.nonNull(pipeline)) {
      getPipelineStorage().deletePipeline(pipelineId);
      new NotificationsResourceManager().deleteNotificationsForPipeline(pipeline);
    }
  }


  /**
   * Checks for the pipelines that contain the processing element
   *
   * @param elementId the id of the processing Element
   * @return all pipelines containing the element
   */
  public static List<Pipeline> getPipelinesContainingElements(String elementId) {
    return PipelineManager.getAllPipelines().stream()
        .filter(pipeline ->
            mergePipelineElement(pipeline)
                .anyMatch(el -> el.getElementId().equals(elementId)))
        .collect(Collectors.toList());
  }

  private static Stream<? extends NamedStreamPipesEntity> mergePipelineElement(Pipeline pipeline) {
    return Stream.concat(
        Stream.concat(pipeline.getStreams().stream(), pipeline.getSepas().stream()),
        pipeline.getActions().stream());
  }

  private static void preparePipelineBasics(String username,
                                            Pipeline pipeline,
                                            String pipelineId) {
    pipeline.setPipelineId(pipelineId);
    pipeline.setRunning(false);
    pipeline.setCreatedByUser(username);
    pipeline.setCreatedAt(new Date().getTime());
    pipeline.getSepas().forEach(processor -> processor.setCorrespondingUser(username));
    pipeline.getActions().forEach(action -> action.setCorrespondingUser(username));
  }

  private static IPipelineStorage getPipelineStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
  }

  private static IPermissionStorage getPermissionStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage();
  }
}
