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

package org.apache.streampipes.commons.prometheus.spRateLimiter;

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

/**
 * Rate Limiter Metrics Manager
 */
public class SpRateLimiterMetrics {
    
  public static final Gauge RATE_LIMITER_QUEUE_SIZE = StreamPipesCollectorRegistry.registerGauge(
        "sp_rate_limiter_queue_size",
        "Current size of the waiting queue"
  );

  public static final Gauge RATE_LIMITER_AVERAGE_WAIT_TIME = StreamPipesCollectorRegistry.registerGauge(
        "sp_rate_limiter_average_wait_time_seconds",
        "Average wait time for permit acquisition in seconds"
  );

  public static void updateCoreMetrics(double queueSize, double averageWaitTime) {
    double safeQueueSize = Math.max(0.0, Math.min(queueSize, 10000.0));
    double safeWaitTime = Math.max(0.0, Math.min(averageWaitTime, 3600.0));

    RATE_LIMITER_QUEUE_SIZE.set(safeQueueSize);
    RATE_LIMITER_AVERAGE_WAIT_TIME.set(safeWaitTime);
  }
}
