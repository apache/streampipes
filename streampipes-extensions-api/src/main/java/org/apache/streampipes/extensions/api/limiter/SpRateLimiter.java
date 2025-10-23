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

import org.apache.streampipes.commons.prometheus.spratelimiter.SpRateLimiterStats;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
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
  private static final long TIMEOUT_MS = 1000;
  private static final int PERMITS_PER_REQUEST = 1;

  private RateLimiter rateLimiter;

  private double rateLimiterAverageWaitTime = 0.0;
  
  private long totalWaitTime = 0L;
  private AtomicInteger waitTimeCount = new AtomicInteger(0);
  
  private AtomicInteger currentQueueSize = new AtomicInteger(0);

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
          scheduler.scheduleAtFixedRate(this::scheduledTask, SCHEDULER_INITIAL_DELAY_SECONDS, SCHEDULER_PERIOD_SECONDS, TimeUnit.SECONDS);
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
    if (this.rateLimiter == null) {
      LOG.warn("RateLimiter has not been initialized. Please call createRateLimiter() first.");
      return false;
    }

    long startTime = System.currentTimeMillis();
    
    synchronized (this) {
      currentQueueSize.incrementAndGet();
    }
    
    try {
      if (rateLimiter.getRate() <= 0) {
        LOG.warn("RateLimiter is set to zero or negative rate. No permits will be acquired.");
        updateAverageWaitTime(ZERO_RATE_WAIT_TIME_MS);
        try {
          Thread.sleep(ZERO_RATE_WAIT_TIME_MS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return false;
        }
        return false;
        } else {
          // Each request consumes exactly 1 permit regardless of data size
          long timeoutMs = TIMEOUT_MS;
          boolean acquired = rateLimiter.tryAcquire(PERMITS_PER_REQUEST, timeoutMs, TimeUnit.MILLISECONDS);
        
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
      }
    } finally {
      synchronized (this) {
        currentQueueSize.updateAndGet(current -> Math.max(0, current - 1));
      }
    }
  }

  public void limitForMap(Map<?, ?> map) throws InterruptedException {
    if (map == null || map.isEmpty()) {
      return;
    }
    long mapDataSize = getMapSizeInBytes(map);
    if (mapDataSize < 0) {
      LOG.warn("Could not determine map size for rate limiting.");
      return;
    }
    limit(mapDataSize);
  }

  public static long getMapSizeInBytes(Map<?, ?> map) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(map);
      oos.close();
      LOG.info("Calculated map size: {} bytes", baos.size());
      return baos.size();
    } catch (Exception e) {
      return -1;
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
    return currentQueueSize.get();
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
    int currentCount = waitTimeCount.incrementAndGet();
    
    rateLimiterAverageWaitTime = (double) totalWaitTime / currentCount / 1000.0;
    
    if (currentCount > STATS_RESET_THRESHOLD) {
      totalWaitTime = totalWaitTime * STATS_RESET_FACTOR / STATS_RESET_DIVISOR;
      waitTimeCount.set(STATS_RESET_FACTOR);
    }
  }

  public SpRateLimiterStats getStats() {
    return stats;
  }
  
  public int getCurrentQueueSize() {
    return currentQueueSize.get();
  }
  
  public void resetQueueSize() {
    currentQueueSize.set(0);
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
