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

package org.apache.streampipes.commons.prometheus.spmemorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory Manager Statistics
 */
public class SpMemoryManagerStats {
    
  private static final Logger log = LoggerFactory.getLogger(SpMemoryManagerStats.class);

  public double memoryUsedBytes = 0.0;
  public double allocationRate = 0.0;

  private final SpMemoryManagerMetrics metrics;

  public SpMemoryManagerStats() {
    this.metrics = new SpMemoryManagerMetrics();
  }

  /**
   * Update all metrics with custom total memory
   */
  public void updateAllMetrics() {
    SpMemoryManagerMetrics.updateCoreMetrics(memoryUsedBytes, allocationRate);
  }

  public double getMemoryUsedBytes() {
    return memoryUsedBytes;
  }

  public void setMemoryUsedBytes(double memoryUsedBytes) {
    this.memoryUsedBytes = memoryUsedBytes;
  }

  public double getAllocationRate() {
    return allocationRate;
  }

  public void setAllocationRate(double allocationRate) {
    this.allocationRate = allocationRate;
  }

  public SpMemoryManagerMetrics getMetrics() {
    return metrics;
  }
}
