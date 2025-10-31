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

package org.apache.streampipes.extensions.api.limiter;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.prometheus.spratelimiter.SpRateLimiterStats;

import com.google.common.util.concurrent.RateLimiter;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A singleton rate limiter implementation for StreamPipes extensions.
 * This class provides rate limiting functionality using Google Guava's RateLimiter.
 * It supports configurable permits per second and warmup periods.
 */
public enum SpRateLimiter {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(SpRateLimiter.class);

  // Configuration constants
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
  private final Environment env = Environments.getEnvironment();

  private RateLimiter rateLimiter;

  private double rateLimiterAverageWaitTime = 0.0;
  
  private long totalWaitTime = 0L;
  private final AtomicInteger waitTimeCount = new AtomicInteger(0);
  
  private final AtomicInteger currentQueueSize = new AtomicInteger(0);

  private SpRateLimiterStats stats;
  private static volatile boolean schedulerInitialized = false;
  private static ScheduledExecutorService scheduler;

    /**
   * Creates a rate limiter with default parameters.
   * Default: calculated permits per second based on memory, 1000ms warmup period.
   */
  public void createRateLimiter() {
    var defaultPermits = setPermit();
    var defaultWarmupPeriod = env.getRateLimiterDefaultWarmupPeriod().getValueOrDefault();
    createRateLimiter(defaultPermits, defaultWarmupPeriod, DEFAULT_TIME_UNIT);
    initScheduledTasks();
    LOG.info("RateLimiter created with {} permits/sec and scheduler initialized", defaultPermits);
  }

  /**
   * Creates a rate limiter with the specified permits per second.
   * Uses default warmup period of 1000ms.
   *
   * @param permitsPerSecond The number of permits per second
   */
  public void createRateLimiter(double permitsPerSecond) {
    var defaultWarmupPeriod = env.getRateLimiterDefaultWarmupPeriod().getValueOrDefault();
    createRateLimiter(permitsPerSecond, defaultWarmupPeriod, DEFAULT_TIME_UNIT);
    initScheduledTasks();
    LOG.info("RateLimiter created with {} permits/sec and scheduler initialized", permitsPerSecond);
  }

  public void initScheduledTasks() {
    var schedulerInitialDelay = env.getRateLimiterSchedulerInitialDelaySeconds().getValueOrDefault();
    var schedulerPeriod = env.getRateLimiterSchedulerPeriodSeconds().getValueOrDefault();
    if (!schedulerInitialized) {
      synchronized (SpRateLimiter.class) {
        if (!schedulerInitialized) {
          scheduler = Executors.newSingleThreadScheduledExecutor();
          scheduler.scheduleAtFixedRate(this::scheduledTask, schedulerInitialDelay, schedulerPeriod, TimeUnit.SECONDS);
          schedulerInitialized = true;
        }
      }
    }
  }

  public void scheduledTask() {
    this.stats = new SpRateLimiterStats();
    stats.setAverageWaitTime(this.rateLimiterAverageWaitTime);
    stats.setQueueSize(this.currentQueueSize.get());
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
   * Acquires a permit from the rate limiter for processing data, with timeout.
   * Each request consumes exactly 1 permit regardless of data size.
   * This provides simple and fair rate limiting based on request count.
   *
   * @param bytes The number of bytes to process (for logging purposes only)
   * @return true if permit was acquired successfully, false if timeout occurred
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public boolean limit(long bytes) throws InterruptedException {
    var timeOutMs = env.getRateLimiterTimeoutMs().getValueOrDefault();
    if (this.rateLimiter == null) {
      LOG.warn("RateLimiter has not been initialized. Please call createRateLimiter() first.");
      return false;
    }

    long startTime = System.currentTimeMillis();

    currentQueueSize.incrementAndGet();
    
    try {
      int permits = (int) bytes;
      long timeoutMs = timeOutMs;
      boolean acquired = rateLimiter.tryAcquire(permits, timeoutMs, TimeUnit.MILLISECONDS);

      long waitTime = System.currentTimeMillis() - startTime;
      updateAverageWaitTime(waitTime);

      if (!acquired) {
        LOG.warn("Failed to acquire permit for {} bytes within {} ms timeout (rate: {} requests/sec)",
                 bytes, timeoutMs, rateLimiter.getRate());
      } else {
        LOG.debug("Successfully acquired permit for {} bytes in {} ms (rate: {} requests/sec)",
                 bytes, waitTime, rateLimiter.getRate()); 
      }
      return acquired;
    } finally {
      currentQueueSize.decrementAndGet();
    }
  }

    /**
     * Sets the number of permits based on a percentage of the JVM's maximum memory.
     *
     * @return The calculated number of permits
     */
  public static double setPermit() {
    var permitsSetPercentage = Environments.getEnvironment().getRateLimiterPermitsSetPercentage().getValueOrDefault();
    OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    return systemMXBean.getTotalMemorySize() * permitsSetPercentage;
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
  public int getCurrentQueueSize() {
    return currentQueueSize.get();
  }

  /**
   * Gets the rate limiter average wait time metric.
   *
   * @return The average wait time in seconds
   */
  public double getRateLimiterAverageWaitTime() {
    return rateLimiterAverageWaitTime;
  }

  /**
   * Sets the rate limiter average wait time metric.
   *
   * @param averageWaitTime The average wait time in seconds
   */
  public void setRateLimiterAverageWaitTime(double averageWaitTime) {
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
    var statsResetThreshold = env.getRateLimiterStatsResetThreshold().getValueOrDefault();
    var statsResetFactor = env.getRateLimiterStatsResetFactor().getValueOrDefault();
    var statsResetDivisor = env.getRateLimiterStatsResetDivisor().getValueOrDefault();
    totalWaitTime += waitTimeMs;
    int currentCount = waitTimeCount.incrementAndGet();
    
    rateLimiterAverageWaitTime = (double) totalWaitTime / currentCount / 1000.0;
    
    if (currentCount > statsResetThreshold) {
      totalWaitTime = totalWaitTime * statsResetFactor / statsResetDivisor;
      waitTimeCount.set(statsResetFactor);
    }
  }

  public SpRateLimiterStats getStats() {
    return stats;
  }
  
  public void resetQueueSize() {
    currentQueueSize.set(0);
    LOG.info("Queue size has been reset");
  }

  public void shutdown() {
    var shutdownTimeoutSeconds = env.getRateLimiterShutdownTimeoutSeconds().getValueOrDefault();
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdown();
      try {
        if (!scheduler.awaitTermination(shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
          scheduler.shutdownNow();
        }
      } catch (InterruptedException e) {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();
        LOG.warn("RateLimiter scheduler shutdown interrupted", e);
      }
    }
  }
}
