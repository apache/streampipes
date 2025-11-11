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
package org.apache.streampipes.commons.prometheus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Statistics
 */
public class ElementServiceStats {

  private static final Logger log = LoggerFactory.getLogger(ElementServiceStats.class);

  public double cpuUsage = 0.0;
  public double memoryUsage = 0.0;
  public double weight = 1.0;
  public double systemLoad = 0.0;
  public double historicalSystemLoad = 0.0;
  public double currentSystemLoad = 0.0;

  private final ElementServiceMetrics metrics;


  public ElementServiceStats(String serviceId) {
    this.metrics = new ElementServiceMetrics(serviceId);
  }


  /**
   * Update CPU usage
   *
   * @param cpuUsage CPU usage
   */
  public void setCpuUsage(double cpuUsage) {
    this.cpuUsage = cpuUsage;
  }

  /**
   * Update memory usage
   *
   * @param memoryUsage Memory usage
   */
  public void setMemoryUsage(double memoryUsage) {
    this.memoryUsage = memoryUsage;
  }

  /**
   * Update weight
   *
   * @param weight Weight
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * Update system load
   *
   * @param systemLoad System load
   */
  public void setSystemLoad(double systemLoad) {
    this.systemLoad = systemLoad;
  }

  /**
   * Update historical system load
   *
   * @param historicalSystemLoad Historical system load
   */
  public void setHistoricalSystemLoad(double historicalSystemLoad) {
    this.historicalSystemLoad = historicalSystemLoad;
  }

  /**
   * Update current system load
   *
   * @param currentSystemLoad Current system load
   */
  public void setCurrentSystemLoad(double currentSystemLoad) {
    this.currentSystemLoad = currentSystemLoad;
  }


  /**
   * Update all metrics
   */
  public void updateAllMetrics() {
    metrics.reportMetrics(cpuUsage, memoryUsage, weight, systemLoad, historicalSystemLoad);
  }

  // Getters
  public double getCpuUsage() {
    return cpuUsage;
  }

  public double getMemoryUsage() {
    return memoryUsage;
  }

  public double getWeight() {
    return weight;
  }

  public double getSystemLoad() {
    return systemLoad;
  }

  public double getHistoricalSystemLoad() {
    return historicalSystemLoad;
  }

  public double getCurrentSystemLoad() {
    return currentSystemLoad;
  }
}
