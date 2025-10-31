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
package org.apache.streampipes.model.loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resource unit for load balancing operations
 * @param <T> Type of elements in the resource unit
 */
public class LoadBalanceResourceUnit<T> {

  private String pipelineId;
  private String serviceId;
  private final List<T> elements;
  private List<String> labels = Collections.EMPTY_LIST;

  /**
   * Default constructor
   */
  public LoadBalanceResourceUnit() {
    this.elements = new ArrayList<>();
  }

  /**
   * Add an element to the resource unit
   * @param element Element to add
   */
  public void addElement(T element) {
    elements.add(element);
  }

  /**
   * Get all elements in the resource unit
   * @return List of elements
   */
  public List<T> getElements() {
    return elements;
  }

  /**
   * Get pipeline ID
   * @return Pipeline ID
   */
  public String getPipelineId() {
    return pipelineId;
  }

  /**
   * Set pipeline ID
   * @param pipelineId Pipeline ID
   */
  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  /**
   * Get service ID
   * @return Service ID
   */
  public String getServiceId() {
    return serviceId;
  }

  /**
   * Set service ID
   * @param serviceId Service ID
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * Get labels
   * @return List of labels
   */
  public List<String> getLabels() {
    return labels;
  }

  /**
   * Set labels
   * @param labels List of labels
   */
  public void setLabels(List<String> labels) {
    this.labels = labels == null ? Collections.EMPTY_LIST : labels;
  }

  /**
   * Get unique ID for this resource unit
   * @return Unique identifier combining pipeline and service ID
   */
  public String getId() {
    if (pipelineId != null && serviceId != null) {
      return pipelineId + "_" + serviceId;
    } else if (pipelineId != null) {
      return pipelineId;
    } else if (serviceId != null) {
      return serviceId;
    }
    return String.valueOf(System.identityHashCode(this));
  }
}
