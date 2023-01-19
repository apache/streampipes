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

package org.apache.streampipes.model.api;

/**
 * Interface for pipeline elements which are invoked upon pipeline start (processors, sinks, sets)
 */
public interface EndpointSelectable {

  /**
   * Gets the name of this selectable endpoint.
   *
   * @return The name of this object.
   */
  String getName();

  /**
   * Gets the URL of the currently selected endpoint.
   *
   * @return endpoint URL.
   */
  String getSelectedEndpointUrl();

  /**
   * Sets the URL of the currently selected endpoint.
   *
   * @param selectedEndpointUrl new URL.
   */
  void setSelectedEndpointUrl(String selectedEndpointUrl);

  /**
   * Gets the corresponding pipeline ID for the endpoint.
   *
   * @return corresponding pipeline ID.
   */
  String getCorrespondingPipeline();


  /**
   * Sets the ID of the corresponding pipeline.
   *
   * @param pipelineId The ID of the pipeline to set as corresponding to the currently selected endpoint.
   */
  void setCorrespondingPipeline(String pipelineId);

  /**
   * Gets the path detach that can be called to detatch the endpoint.
   * When an endpoint is detached it usally is also sopped
   *
   * @return detach path.
   */
  String getDetachPath();
}
