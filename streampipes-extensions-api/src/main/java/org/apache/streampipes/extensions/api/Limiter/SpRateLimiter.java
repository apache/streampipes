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

package org.apache.streampipes.extensions.api.Limiter;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.streampipes.commons.prometheus.spRateLimiter.SpRateLimiterStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A singleton rate limiter implementation for StreamPipes extensions.
 * This class provides rate limiting functionality using Google Guava's RateLimiter.
 * It supports configurable permits per second and warmup periods.
 */
public enum SpRateLimiter {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(SpRateLimiter.class);

  // Configuration constants
  private static final double DEFAULT_PERMITS_PER_SECOND = 100.0;
  private static final long DEFAULT_WARMUP_PERIOD = 1000L;
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
  private static final int SCHEDULER_INITIAL_DELAY_SECONDS = 0;
  private static final int SCHEDULER_PERIOD_SECONDS = 15;
  private static final long ZERO_RATE_WAIT_TIME_MS = 1000L;
  private static final int STATS_RESET_THRESHOLD = 1000;
  private static final int STATS_RESET_FACTOR = 999;
  private static final int STATS_RESET_DIVISOR = 1000;
  private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

  private RateLimiter rateLimiter;

  private double rateLimiterQueueSize = 0.0;
  private double rateLimiterAverageWaitTime = 0.0;
  
  private long totalWaitTime = 0L;
  private int waitTimeCount = 0;
  
  private volatile int currentQueueSize = 0;

  private SpRateLimiterStats stats;
  private static volatile boolean schedulerInitialized = false;
  private static ScheduledExecutorService scheduler;

    /**
   * Creates a rate limiter with default parameters.
   * Default: 100 permits per second, 1000ms warmup period.
   */
  public void createRateLimiter() {
    createRateLimiter(DEFAULT_PERMITS_PER_SECOND, DEFAULT_WARMUP_PERIOD, DEFAULT_TIME_UNIT);
    initScheduledTasks();
    LOG.info("RateLimiter created and scheduler initialized");
  }

  /**
   * Creates a rate limiter with the specified permits per second.
   * Uses default warmup period of 1000ms.
   *
   * @param permitsPerSecond The number of permits per second
   */
  public void createRateLimiter(double permitsPerSecond) {
    createRateLimiter(permitsPerSecond, DEFAULT_WARMUP_PERIOD, DEFAULT_TIME_UNIT);
    initScheduledTasks();
    LOG.info("RateLimiter created with {} permits/sec and scheduler initialized", permitsPerSecond);
  }

  public void initScheduledTasks() {
    if (!schedulerInitialized) {
      synchronized (SpRateLimiter.class) {
        if (!schedulerInitialized) {
          scheduler = Executors.newSingleThreadScheduledExecutor();
          scheduler.scheduleAtFixedRate(this::ScheduledTask, SCHEDULER_INITIAL_DELAY_SECONDS, SCHEDULER_PERIOD_SECONDS, TimeUnit.SECONDS);
          schedulerInitialized = true;
        }
      }
    }
  }

  public void ScheduledTask() {
    this.stats = new SpRateLimiterStats();
    stats.setAverageWaitTime(this.rateLimiterAverageWaitTime);
    stats.setQueueSize(this.rateLimiterQueueSize);
    stats.updateAllMetrics();
  }

  /**
   * Creates a rate limiter with the specified parameters.
   *
   * @param permitsPerSecond The number of permits per second
   * @param warmupPeriod     The warmup period
   * @param unit             The time unit for the warmup period
   * @throws IllegalArgumentException if parameters are invalid
   */
  public void createRateLimiter(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
    if (this.rateLimiter == null) {
      validateParameters(permitsPerSecond, warmupPeriod, unit);
      this.rateLimiter = RateLimiter.create(permitsPerSecond, warmupPeriod, unit);
      LOG.info("RateLimiter created with {} permits per second, warmup period: {} {}",
          permitsPerSecond, warmupPeriod, unit);
    } else {
      LOG.warn("RateLimiter already exists. Use setRate() to modify the rate instead.");
    }
  }

  /**
   * Acquires a permit from the rate limiter, blocking if necessary.
   * If the rate limiter is not initialized, logs a warning and returns immediately.
   * If the rate is zero or negative, logs a warning and sleeps for 1 second.
   *
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public void limit() throws InterruptedException {
    if (this.rateLimiter == null) {
      LOG.warn("RateLimiter has not been initialized. Please call createRateLimiter() first.");
      return;
    }

    long startTime = System.currentTimeMillis();
    
    synchronized (this) {
      currentQueueSize++;
      rateLimiterQueueSize = currentQueueSize;
    }
    
    try {
      if (rateLimiter.getRate() <= 0) {
        LOG.warn("RateLimiter is set to zero or negative rate. No permits will be acquired.");
          updateAverageWaitTime(ZERO_RATE_WAIT_TIME_MS);
        Thread.sleep(ZERO_RATE_WAIT_TIME_MS);
      } else {
        this.rateLimiter.acquire();
        long waitTime = System.currentTimeMillis() - startTime;
        
        updateAverageWaitTime(waitTime);
      }
    } finally {
      synchronized (this) {
        currentQueueSize = Math.max(0, currentQueueSize - 1);
        rateLimiterQueueSize = currentQueueSize;
        
      }
    }
  }

  /**
   * Updates the rate of the rate limiter.
   *
   * @param permitsPerSecond The new rate in permits per second
   * @throws IllegalStateException if the rate limiter is not initialized
   */
  public void setRate(double permitsPerSecond) {
    if (this.rateLimiter != null) {
      this.rateLimiter.setRate(permitsPerSecond);
      LOG.info("RateLimiter rate updated to {} permits per second", permitsPerSecond);
    } else {
      throw new IllegalStateException("RateLimiter has not been initialized.");
    }
  }

  /**
   * Gets the current rate of the rate limiter.
   *
   * @return The current rate in permits per second
   * @throws IllegalStateException if the rate limiter is not initialized
   */
  public double getRate() {
    if (this.rateLimiter != null) {
      return this.rateLimiter.getRate();
    } else {
      throw new IllegalStateException("RateLimiter has not been initialized.");
    }
  }

  /**
   * Checks if the rate limiter has been initialized.
   *
   * @return true if initialized, false otherwise
   */
  public boolean isInitialized() {
    return this.rateLimiter != null;
  }

  /**
   * Resets the rate limiter to its uninitialized state.
   */
  public void reset() {
    if (this.rateLimiter != null) {
      this.rateLimiter = null;
      LOG.info("RateLimiter has been reset");
    }
  }

  /**
   * Gets the rate limiter queue size metric.
   *
   * @return The current queue size
   */
  public double getRATE_LIMITER_QUEUE_SIZE() {
    return rateLimiterQueueSize;
  }

  /**
   * Sets the rate limiter queue size metric.
   *
   * @param queueSize The queue size to set
   */
  public void setRATE_LIMITER_QUEUE_SIZE(double queueSize) {
    this.rateLimiterQueueSize = queueSize;
  }

  /**
   * Gets the rate limiter average wait time metric.
   *
   * @return The average wait time in seconds
   */
  public double getRATE_LIMITER_AVERAGE_WAIT_TIME() {
    return rateLimiterAverageWaitTime;
  }

  /**
   * Sets the rate limiter average wait time metric.
   *
   * @param averageWaitTime The average wait time in seconds
   */
  public void setRATE_LIMITER_AVERAGE_WAIT_TIME(double averageWaitTime) {
    this.rateLimiterAverageWaitTime = averageWaitTime;
  }


  private void validateParameters(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
    if (permitsPerSecond <= 0) {
      throw new IllegalArgumentException("permitsPerSecond must be positive, got: " + permitsPerSecond);
    }
    if (warmupPeriod < 0) {
      throw new IllegalArgumentException("warmupPeriod must be non-negative, got: " + warmupPeriod);
    }
    if (unit == null) {
      throw new IllegalArgumentException("TimeUnit cannot be null");
    }
  }

  private void updateAverageWaitTime(long waitTimeMs) {
    totalWaitTime += waitTimeMs;
    waitTimeCount++;
    
    rateLimiterAverageWaitTime = (double) totalWaitTime / waitTimeCount / 1000.0;
    
    if (waitTimeCount > STATS_RESET_THRESHOLD) {
      totalWaitTime = totalWaitTime * STATS_RESET_FACTOR / STATS_RESET_DIVISOR;
      waitTimeCount = STATS_RESET_FACTOR;
    }
  }

  public SpRateLimiterStats getStats() {
    return stats;
  }
  
  public int getCurrentQueueSize() {
    return currentQueueSize;
  }
  
  public void resetQueueSize() {
    currentQueueSize = 0;
    rateLimiterQueueSize = 0.0;
    LOG.info("Queue size has been reset");
  }

  public static void shutdown() {
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdown();
      try {
        if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
          scheduler.shutdownNow();
        }
      } catch (InterruptedException e) {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }
}
