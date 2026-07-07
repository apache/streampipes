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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.permission.PermissionManager;
import org.apache.streampipes.manager.storage.PipelineStorageService;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.CrudResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipelineManager {

  private final SpResourceManager resourceManager;
  private final IPipelineStorage pipelineStorage;

  public PipelineManager(SpResourceManager resourceManager) {
    this.pipelineStorage = resourceManager.managePipelines().getDb();
    this.resourceManager = resourceManager;
  }

  /**
   * Returns all pipelines
   *
   * @return all pipelines
   */
  public List<Pipeline> getAllPipelines() {
    return pipelineStorage.findAll();
  }

  /**
   * Returns the stored pipeline with the given pipeline id
   *
   * @param pipelineId id of pipeline
   * @return pipeline resulting pipeline with given id
   */
  public Pipeline getPipeline(String pipelineId) {
    return pipelineStorage.getElementById(pipelineId);
  }

  /**
   * Adds a new pipeline for the user with the username to the storage
   *
   * @param principalSid the ID of the owner principal
   * @param pipeline     to be added
   * @return pipelineId of the stored pipeline
   */
  public String addPipeline(
      String principalSid,
      Pipeline pipeline
  ) {

    String pipelineId = Objects.isNull(pipeline.getPipelineId())
        ? UUIDGenerator.generateUuid()
        : pipeline.getPipelineId();
    preparePipelineBasics(principalSid, pipeline, pipelineId);
    new PipelineStorageService(pipelineStorage, pipeline).addPipeline();

    Permission permission = new PermissionManager().makePermission(pipeline, principalSid);
    resourceManager.managePermissions().create(permission);

    return pipelineId;
  }

  /**
   * Starts all processing elements of the pipeline with the pipelineId
   *
   * @param pipelineId of pipeline to be started
   * @return pipeline status of the start operation
   */
  public PipelineOperationStatus startPipeline(String pipelineId,
                                                      ExtensionServiceRequestManager requestManager) {
    Pipeline pipeline = getPipeline(pipelineId);
    return new PipelineExecutor(pipeline, requestManager, resourceManager).startPipeline();
  }

  /**
   * Stops all processing elements of the pipeline
   *
   * @param pipelineId of pipeline to be stopped
   * @param forceStop  when it is true, the pipeline is stopped, even if not all
   *                   processing element
   *                   containers could be reached
   * @return pipeline status of the start operation
   */
  public PipelineOperationStatus stopPipeline(
      String pipelineId,
      boolean forceStop,
      ExtensionServiceRequestManager requestManager
  ) {
    Pipeline pipeline = getPipeline(pipelineId);

    return new PipelineExecutor(pipeline, requestManager, resourceManager).stopPipeline(forceStop);
  }

  /**
   * Deletes the pipeline with the pipeline Id
   *
   * @param pipelineId of pipeline to be deleted
   */
  public void deletePipeline(String pipelineId) {
    var pipelineCrudResourceManager = new CrudResourceManager<>(
        pipelineStorage, Pipeline.class, resourceManager.managePermissions()
    );

    var pipeline = getPipeline(pipelineId);
    if (Objects.nonNull(pipeline)) {
      pipelineCrudResourceManager.delete(pipelineId);
    }
  }

  public List<PipelineOperationStatus> stopAllPipelines(boolean forceStop,
                                                               ExtensionServiceRequestManager requestManager) {
    List<PipelineOperationStatus> status = new ArrayList<>();
    List<Pipeline> pipelines = pipelineStorage.findAll();

    pipelines.forEach(p -> {
      if (p.isRunning()) {
        status.add(new PipelineExecutor(p, requestManager, resourceManager).stopPipeline(forceStop));
      }
    });
    return status;
  }

  /**
   * Checks for the pipelines that contain the processing element
   *
   * @param elementId the id of the processing Element
   * @return all pipelines containing the element
   */
  public List<Pipeline> getPipelinesContainingElements(String elementId) {
    return getAllPipelines()
                          .stream()
                          .filter(pipeline -> mergePipelineElement(pipeline)
                              .anyMatch(el -> el.getElementId()
                                                .equals(elementId)))
                          .collect(Collectors.toList());
  }

  private Stream<? extends NamedStreamPipesEntity> mergePipelineElement(Pipeline pipeline) {
    return Stream.concat(
        Stream.concat(
            pipeline.getStreams()
                    .stream(),
            pipeline.getSepas()
                    .stream()
        ),
        pipeline.getActions()
                .stream()
    );
  }

  private void preparePipelineBasics(
      String username,
      Pipeline pipeline,
      String pipelineId
  ) {
    pipeline.setPipelineId(pipelineId);
    pipeline.setRunning(false);
    pipeline.setHealthStatus(PipelineHealthStatus.OK);
    pipeline.setCreatedByUser(username);
    pipeline.setCreatedAt(new Date().getTime());
  }
}
