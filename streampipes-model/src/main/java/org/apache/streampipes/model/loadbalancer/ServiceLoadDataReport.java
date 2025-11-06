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

import java.util.Objects;

/**
 * Service Load Data Report
 * Contains service resource usage snapshot with current and historical data
 */
public class ServiceLoadDataReport {

  /**
   * Resource snapshot containing usage data at a specific time
   */
  public static class ResourceSnapshot {
    private Usage cpu;
    private Usage memory;
    private long timestamp;

    public ResourceSnapshot() {
      this.timestamp = System.currentTimeMillis();
    }

    public ResourceSnapshot(Usage cpu, Usage memory) {
      this.cpu = cpu;
      this.memory = memory;
      this.timestamp = System.currentTimeMillis();
    }

    public Usage getCpu() {
      return cpu;
    }

    public void setCpu(Usage cpu) {
      this.cpu = cpu;
    }

    public Usage getMemory() {
      return memory;
    }

    public void setMemory(Usage memory) {
      this.memory = memory;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }

    public boolean isComplete() {
      return cpu != null && memory != null;
    }

    public double getAverageUsagePercent() {
      if (!isComplete()) {
        return 0.0;
      }
      return (cpu.percentUsage() + memory.percentUsage()) / 2.0;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ResourceSnapshot that = (ResourceSnapshot) o;
      return timestamp == that.timestamp
              && Objects.equals(cpu, that.cpu)
              && Objects.equals(memory, that.memory);
    }

    @Override
    public int hashCode() {
      return Objects.hash(cpu, memory, timestamp);
    }

    @Override
    public String toString() {
      return "ResourceSnapshot{"
              + "cpu=" + cpu
              + ", memory=" + memory
              + ", timestamp=" + timestamp
              + '}';
    }
  }

  private ResourceSnapshot current;
  private ResourceSnapshot historical;
  private int weight;

  /**
   * Default constructor
   */
  public ServiceLoadDataReport() {
    this.current = new ResourceSnapshot();
    this.historical = new ResourceSnapshot();
    this.weight = 0;
  }

  /**
   * Constructor with current snapshot
   * @param current Current resource snapshot
   * @param weight Weight
   */
  public ServiceLoadDataReport(ResourceSnapshot current, int weight) {
    this.current = current;
    this.historical = new ResourceSnapshot();
    this.weight = validateWeight(weight);
  }

  /**
   * Constructor with current and historical snapshots
   * @param current Current resource snapshot
   * @param historical Historical resource snapshot
   * @param weight Weight
   */
  public ServiceLoadDataReport(ResourceSnapshot current, ResourceSnapshot historical, int weight) {
    this.current = current;
    this.historical = historical;
    this.weight = validateWeight(weight);
  }

  /**
   * Get current resource snapshot
   * @return Current snapshot
   */
  public ResourceSnapshot getCurrent() {
    return current;
  }

  /**
   * Set current resource snapshot
   * @param current Current snapshot
   */
  public void setCurrent(ResourceSnapshot current) {
    this.current = current;
  }

  /**
   * Get historical resource snapshot
   * @return Historical snapshot
   */
  public ResourceSnapshot getHistorical() {
    return historical;
  }

  /**
   * Set historical resource snapshot
   * @param historical Historical snapshot
   */
  public void setHistorical(ResourceSnapshot historical) {
    this.historical = historical;
  }

  /**
   * Get weight
   * @return Weight value
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Set weight
   * @param weight Weight value
   */
  public void setWeight(int weight) {
    this.weight = validateWeight(weight);
  }

  // Convenience methods for backward compatibility

  /**
   * Get current CPU usage (backward compatible)
   * @return Current CPU usage
   */
  public Usage getCpu() {
    return current != null ? current.getCpu() : null;
  }

  /**
   * Set current CPU usage (backward compatible)
   * @param cpu CPU usage
   */
  public void setCpu(Usage cpu) {
    if (current == null) {
      current = new ResourceSnapshot();
    }
    current.setCpu(cpu);
  }

  /**
   * Get current memory usage (backward compatible)
   * @return Current memory usage
   */
  public Usage getMemory() {
    return current != null ? current.getMemory() : null;
  }

  /**
   * Set current memory usage (backward compatible)
   * @param memory Memory usage
   */
  public void setMemory(Usage memory) {
    if (current == null) {
      current = new ResourceSnapshot();
    }
    current.setMemory(memory);
  }

  /**
   * Calculate weight based on CPU and memory usage percentage
   * @param cpuPercent CPU usage percentage
   * @param memoryPercent Memory usage percentage
   */
  public void setWeight(int cpuPercent, int memoryPercent) {
    if (cpuPercent < 0 || memoryPercent < 0) {
      throw new IllegalArgumentException("CPU and memory percentages must be non-negative");
    }

    int cpuWeight = cpuPercent / LoadBalancerConstants.DEFAULT_CPU_STANDARD;
    int memoryWeight = memoryPercent / LoadBalancerConstants.DEFAULT_MEMORY_STANDARD;
    this.weight = Math.min(cpuWeight, memoryWeight);
  }

  /**
   * Automatically calculate weight based on current CPU and memory usage
   */
  public void calculateWeight() {
    if (current != null && current.isComplete()) {
      setWeight((int) current.getCpu().percentUsage(), (int) current.getMemory().percentUsage());
    }
  }

  /**
   * Check if report is complete
   * @return Whether contains all necessary information
   */
  public boolean isComplete() {
    return current != null && current.isComplete();
  }

  /**
   * Get total usage rate (average of CPU and memory)
   * @return Total usage percentage
   */
  public double getTotalUsagePercent() {
    if (!isComplete()) {
      return 0.0;
    }
    return current.getAverageUsagePercent();
  }

  /**
   * Check if historical data is available
   * @return Whether historical data is present
   */
  public boolean hasHistoricalData() {
    return historical != null && historical.isComplete();
  }

  /**
   * Validate weight value
   * @param weight Weight value
   * @return Validated weight value
   */
  private int validateWeight(int weight) {
    if (weight < LoadBalancerConstants.MIN_WEIGHT) {
      throw new IllegalArgumentException("Weight must be non-negative");
    }
    return weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceLoadDataReport that = (ServiceLoadDataReport) o;
    return weight == that.weight
            && Objects.equals(current, that.current)
            && Objects.equals(historical, that.historical);
  }

  @Override
  public int hashCode() {
    return Objects.hash(current, historical, weight);
  }

  @Override
  public String toString() {
    return "ServiceLoadDataReport{"
            + "current=" + current
            + ", historical=" + historical
            + ", weight=" + weight
            + '}';
  }
}
