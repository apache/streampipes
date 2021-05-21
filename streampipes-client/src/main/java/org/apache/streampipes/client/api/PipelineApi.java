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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;

public class PipelineApi extends AbstractClientApi<Pipeline> implements CRUDApi<String, Pipeline> {

  public PipelineApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, Pipeline.class);
  }

  @Override
  public Pipeline get(String pipelineId) {
    return getSingle(getBaseResourcePath().addToPath(pipelineId));
  }

  /**
   * Receives all pipelines owned by the current user
   * @return (list) {@link org.apache.streampipes.model.pipeline.Pipeline} a list of all pipelines
   */
  @Override
  public List<Pipeline> all() {
      return getAll(getBaseResourcePath().addToPath("own"));
  }

  @Override
  public void create(Pipeline element) {

  }

  /**
   * Deletes the pipeline with a given id
   * @param pipelineId The id of the pipeline
   */
  @Override
  public void delete(String pipelineId) {
    delete(getBaseResourcePath().addToPath(pipelineId), Message.class);
  }

  @Override
  public void update(Pipeline element) {

  }

  /**
   * Starts a pipeline by given id
   * @param pipelineId The id of the pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after invocation
   */
  public PipelineOperationStatus start(String pipelineId) {
    return getSingle(getBaseResourcePath().addToPath(pipelineId).addToPath("start"), PipelineOperationStatus.class);
  }

  /**
   * Starts a pipeline by given id
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after invocation
   */
  public PipelineOperationStatus start(Pipeline pipeline) {
    return start(pipeline.getPipelineId());
  }

  /**
   * Stops a pipeline by given id
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after detach
   */
  public PipelineOperationStatus stop(Pipeline pipeline) {
    return stop(pipeline.getPipelineId());
  }

  /**
   * Stops a pipeline by given id
   * @param pipelineId The id of the pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after detach
   */
  public PipelineOperationStatus stop(String pipelineId) {
    return getSingle(getBaseResourcePath().addToPath(pipelineId).addToPath("stop"), PipelineOperationStatus.class);
  }

  /**
   * Migrates pipeline elements at run-time
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after migration
   */
  public PipelineOperationStatus migrate(Pipeline pipeline) {
    return migrate(pipeline.getPipelineId(), pipeline);
  }

  /**
   * Migrates pipeline elements at run-time
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after migration
   */
  private PipelineOperationStatus migrate(String pipelineId, Pipeline pipeline) {
    return  post(getBaseResourcePath().addToPath("migrate").addToPath(pipelineId),
            pipeline, PipelineOperationStatus.class);
  }

  /**
   * Reconfigures individual pipeline elements at run-time
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after migration
   */
  public PipelineOperationStatus reconfigure(Pipeline pipeline) {
    return reconfigure(pipeline.getPipelineId(), pipeline);
  }

  /**
   * Reconfigures individual pipeline elements at run-time
   * @param pipeline The pipeline
   * @return {@link org.apache.streampipes.model.pipeline.PipelineOperationStatus} the status message after migration
   */
  private PipelineOperationStatus reconfigure(String pipelineId, Pipeline pipeline) {
    return put(getBaseResourcePath().addToPath("reconfigure").addToPath(pipelineId),
            pipeline, PipelineOperationStatus.class);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromUserApiPath(clientConfig.getCredentials())
            .addToPath("pipelines");
  }
}
