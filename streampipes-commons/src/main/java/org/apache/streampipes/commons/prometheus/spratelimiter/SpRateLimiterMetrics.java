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

package org.apache.streampipes.commons.prometheus.spratelimiter;

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import io.prometheus.client.Gauge;

/**
 * Rate Limiter Metrics Manager
 */
public class SpRateLimiterMetrics {
  @Deprecated
  public static final Gauge RATE_LIMITER_QUEUE_SIZE_LEGACY = StreamPipesCollectorRegistry.registerGauge(
        "sp_rate_limiter_queue_size",
        "DEPRECATED: Use sp_extension_rate_limiter_queue_total instead. Current size of the waiting queue"
  );
  @Deprecated
  public static final Gauge RATE_LIMITER_AVERAGE_WAIT_TIME_LEGACY = StreamPipesCollectorRegistry.registerGauge(
        "sp_rate_limiter_average_wait_time_seconds",
        "DEPRECATED: Use sp_extension_rate_limiter_average_wait_time_seconds instead. Average wait time for permit acquisition in seconds"
  );

  public static final Gauge RATE_LIMITER_QUEUE_SIZE = StreamPipesCollectorRegistry.registerGauge(
        "sp_extension_rate_limiter_queue_total",
        "Current size of the waiting queue"
  );

  public static final Gauge RATE_LIMITER_AVERAGE_WAIT_TIME = StreamPipesCollectorRegistry.registerGauge(
        "sp_extension_rate_limiter_average_wait_time_seconds",
        "Average wait time for permit acquisition in seconds"
  );

  public static void updateCoreMetrics(double queueSize, double averageWaitTime) {
    RATE_LIMITER_QUEUE_SIZE.set(queueSize);
    RATE_LIMITER_AVERAGE_WAIT_TIME.set(averageWaitTime);

    RATE_LIMITER_QUEUE_SIZE_LEGACY.set(queueSize);
    RATE_LIMITER_AVERAGE_WAIT_TIME_LEGACY.set(averageWaitTime);
  }
}
