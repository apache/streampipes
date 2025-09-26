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
 * Contains service CPU, memory usage and weight information
 */
public class ServiceLoadDataReport {
    
  // Using configuration from constants class

  private Usage cpu;
  private Usage memory;
  private int weight;

  /**
   * Default constructor
   */
  public ServiceLoadDataReport() {
    this.weight = 0;
}

  /**
   * Constructor
   * @param cpu CPU usage
   * @param memory Memory usage
   * @param weight Weight
   */
  public ServiceLoadDataReport(Usage cpu, Usage memory, int weight) {
    this.cpu = cpu;
    this.memory = memory;
    this.weight = validateWeight(weight);
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
   * Automatically calculate weight based on CPU and memory usage
   */
  public void calculateWeight() {
    if (cpu != null && memory != null) {
      setWeight((int) cpu.percentUsage(), (int) memory.percentUsage());
    }
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

  /**
   * Get CPU usage
   * @return CPU usage
   */
  public Usage getCpu() {
    return cpu;
}

  /**
   * Set CPU usage
   * @param cpu CPU usage
   */
  public void setCpu(Usage cpu) {
    this.cpu = cpu;
}

  /**
   * Get memory usage
   * @return Memory usage
   */
  public Usage getMemory() {
    return memory;
  }

  /**
   * Set memory usage
   * @param memory Memory usage
   */
  public void setMemory(Usage memory) {
    this.memory = memory;
}

  /**
   * Check if report is complete
   * @return Whether contains all necessary information
   */
  public boolean isComplete() {
    return cpu != null && memory != null;
}

  /**
   * Get total usage rate (average of CPU and memory)
   * @return Total usage percentage
   */
  public double getTotalUsagePercent() {
    if (!isComplete()) {
      return 0.0;
    }
    return (cpu.percentUsage() + memory.percentUsage()) / 2.0;
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
            && Objects.equals(cpu, that.cpu)
            && Objects.equals(memory, that.memory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cpu, memory, weight);
  }

  @Override
  public String toString() {
    return "ServiceLoadDataReport{"
            + "cpu="
            + cpu
            + ", memory="
            + memory
            + ", weight="
            + weight
            +
            '}';
  }
}
