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

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import io.prometheus.client.Gauge;

/**
 * Memory Manager Metrics Manager
 */
public class SpMemoryManagerMetrics {
    
  public static final Gauge MEMORY_USED_BYTES = StreamPipesCollectorRegistry.registerGauge(
        "sp_memory_used_bytes",
        "Amount of memory used in bytes"
  );

  public static final Gauge MEMORY_ALLOCATION_RATE = StreamPipesCollectorRegistry.registerGauge(
        "sp_memory_allocation_rate_bytes_per_second",
        "Memory allocation rate in bytes per second"
  );

  public static void updateCoreMetrics(double memoryUsedBytes, double allocationRate) {
    MEMORY_USED_BYTES.set(memoryUsedBytes);
    MEMORY_ALLOCATION_RATE.set(allocationRate);
  }
}
