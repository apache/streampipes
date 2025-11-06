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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rate Limiter Statistics
 */
public class SpRateLimiterStats {
    
  private static final Logger log = LoggerFactory.getLogger(SpRateLimiterStats.class);

  public double queueSize = 0.0;
  public double averageWaitTime = 0.0;

  private final SpRateLimiterMetrics metrics;

  public SpRateLimiterStats() {
    this.metrics = new SpRateLimiterMetrics();
  }

  /**
   * Update all metrics
   */
  public void updateAllMetrics() {
    SpRateLimiterMetrics.updateCoreMetrics(queueSize, averageWaitTime);
  }

  public double getQueueSize() {
    return queueSize;
  }

  public void setQueueSize(double queueSize) {
    this.queueSize = queueSize;
  }

  public double getAverageWaitTime() {
    return averageWaitTime;
  }

  public void setAverageWaitTime(double averageWaitTime) {
    this.averageWaitTime = averageWaitTime;
  }

  public SpRateLimiterMetrics getMetrics() {
    return metrics;
  }
}
