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

import org.apache.streampipes.commons.prometheus.core.PrometheusMetrics;
import org.apache.streampipes.commons.prometheus.core.PrometheusStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Service Statistics Manager
 * Uses new Map<serviceId, Stats> structure with metrics as internal objects of stats
 */
public class ElementServiceStats extends PrometheusStats {
    
private static final Logger log = LoggerFactory.getLogger(ElementServiceStats.class);

public double cpuUsage = 0.0;
public double memoryUsage = 0.0;
public double weight = 1.0;
public double systemLoad = 0.0;
public double historicalSystemLoad = 0.0;
public double currentSystemLoad = 0.0;

public ElementServiceStats(String serviceId) {
    super(serviceId);
}

@Override
protected PrometheusMetrics createMetrics() {
    return new ElementServiceMetrics(this.getId());
}

  /**
   * Update CPU usage
   * @param cpuUsage CPU usage
   */
  public void setCpuUsage(double cpuUsage) {
    this.cpuUsage = cpuUsage;
    setGaugeValue("cpu_usage", cpuUsage);
  }

  /**
   * Update memory usage
   * @param memoryUsage Memory usage
   */
  public void setMemoryUsage(double memoryUsage) {
    this.memoryUsage = memoryUsage;
    setGaugeValue("memory_usage", memoryUsage);
  }

  /**
   * Update weight
   * @param weight Weight
   */
  public void setWeight(double weight) {
    this.weight = weight;
    setGaugeValue("weight", weight);
  }

  /**
   * Update system load
   * @param systemLoad System load
   */
  public void setSystemLoad(double systemLoad) {
    this.systemLoad = systemLoad;
    setGaugeValue("system_load", systemLoad);
  }

  /**
   * Update historical system load
   * @param historicalSystemLoad Historical system load
   */
  public void setHistoricalSystemLoad(double historicalSystemLoad) {
    this.historicalSystemLoad = historicalSystemLoad;
    setGaugeValue("historical_system_load", historicalSystemLoad);
  }

  /**
   * Update current system load
   * @param currentSystemLoad Current system load
   */
  public void setCurrentSystemLoad(double currentSystemLoad) {
    this.currentSystemLoad = currentSystemLoad;
    setGaugeValue("current_system_load", currentSystemLoad);
  }

  /**
   * Update basic metrics
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   */
  public void updateBasicMetrics(double cpuUsage, double memoryUsage, double weight) {
    this.cpuUsage = cpuUsage;
    this.memoryUsage = memoryUsage;
    this.weight = weight;
    setGaugeValue("cpu_usage", cpuUsage);
    setGaugeValue("memory_usage", memoryUsage);
    setGaugeValue("weight", weight);
  }

  /**
   * Update system load metrics
   * @param systemLoad System load
   * @param historicalSystemLoad Historical system load
   * @param currentSystemLoad Current system load
   */
  public void updateSystemLoadMetrics(double systemLoad, double historicalSystemLoad, double currentSystemLoad) {
    this.systemLoad = systemLoad;
    this.historicalSystemLoad = historicalSystemLoad;
    this.currentSystemLoad = currentSystemLoad;
    setGaugeValue("system_load", systemLoad);
    setGaugeValue("historical_system_load", historicalSystemLoad);
    setGaugeValue("current_system_load", currentSystemLoad);
  }

  /**
   * Update all metrics
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   * @param systemLoad System load
   * @param historicalSystemLoad Historical system load
   * @param currentSystemLoad Current system load
   */
  public void updateAllMetrics(double cpuUsage, double memoryUsage, double weight,
                            double systemLoad, double historicalSystemLoad, double currentSystemLoad) {
    this.cpuUsage = cpuUsage;
    this.memoryUsage = memoryUsage;
    this.weight = weight;
    this.systemLoad = systemLoad;
    this.historicalSystemLoad = historicalSystemLoad;
    this.currentSystemLoad = currentSystemLoad;
    setGaugeValue("cpu_usage", cpuUsage);
    setGaugeValue("memory_usage", memoryUsage);
    setGaugeValue("weight", weight);
    setGaugeValue("system_load", systemLoad);
    setGaugeValue("historical_system_load", historicalSystemLoad);
    setGaugeValue("current_system_load", currentSystemLoad);
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

  /**
   * 通过报告更新指标（向后兼容）
   * @param serviceId 服务ID
   * @param cpuUsage CPU使用率
   * @param memoryUsage 内存使用率
   * @param weight 权重
   */
  public static void metricsByReport(String serviceId, double cpuUsage, double memoryUsage, double weight) {
    ElementServiceStats stats = getOrCreateStats(serviceId);
    stats.updateBasicMetrics(cpuUsage, memoryUsage, weight);
    updateServiceCount();
  }

  /**
   * 更新所有指标（向后兼容）
   */
  public static void metrics() {
    PrometheusStats.updateAllMetrics();
    updateServiceCount();
  }

  /**
   * 获取或创建统计
   * @param serviceId 服务ID
   * @return 统计
   */
  private static ElementServiceStats getOrCreateStats(String serviceId) {
    PrometheusStats stats = PrometheusStats.getStats(serviceId);
    if (stats instanceof ElementServiceStats) {
      return (ElementServiceStats) stats;
    }
    // 如果没找到或类型不匹配，创建新的
    return new ElementServiceStats(serviceId);
  }

  /**
   * 检查服务是否存在（向后兼容）
   * @param serviceId 服务ID
   * @return 是否存在
   */
  public static boolean containsKey(String serviceId) {
    PrometheusStats stats = PrometheusStats.getStats(serviceId);
    return stats instanceof ElementServiceStats;
  }

  /**
   * 获取服务统计（向后兼容）
   * @param serviceId 服务ID
   * @return 统计
   */
  public static ElementServiceStats get(String serviceId) {
    PrometheusStats stats = PrometheusStats.getStats(serviceId);
    return stats instanceof ElementServiceStats ? (ElementServiceStats) stats : null;
  }

  /**
   * 更新服务计数
   */
  private static void updateServiceCount() {
    ElementServiceMetrics.serviceCount.set(PrometheusStats.getStatsCount());
  }
}
