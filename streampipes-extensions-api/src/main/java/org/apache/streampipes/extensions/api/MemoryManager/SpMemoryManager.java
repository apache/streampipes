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

package org.apache.streampipes.extensions.api.MemoryManager;

import org.apache.streampipes.commons.prometheus.spMemoryManager.SpMemoryManagerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A singleton memory manager implementation for StreamPipes extensions.
 * This class provides memory allocation and deallocation functionality with
 * blocking behavior when insufficient memory is available.
 */
public enum SpMemoryManager {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(SpMemoryManager.class);

  // Configuration constants
  private static final long DEFAULT_INITIAL_MEMORY = 10L * 1024 * 1024 * 1024; // 10 GB
  private static final long WAIT_TIMEOUT_MS = 1000L;
  private static final int SCHEDULER_INITIAL_DELAY_SECONDS = 0;
  private static final int SCHEDULER_PERIOD_SECONDS = 15;
  private static final int BYTES_TO_MB = 1024 * 1024;
  private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
  
  // Memory control thresholds
  private static final double MEMORY_USAGE_THRESHOLD = 0.9; // 90% threshold for blocking
  private static final double MEMORY_WARNING_THRESHOLD = 0.8; // 80% warning threshold

  private long freeMemory;

  private double memoryUsedBytes = 0.0;
  private double memoryAllocationRate = 0.0;
  
  private long lastAllocationTime = System.currentTimeMillis();
  private long totalAllocatedBytes = 0L;
  
  // Memory control state
  private volatile boolean memoryBlocked = false;
  private volatile boolean memoryWarningActive = false;

  private SpMemoryManagerStats stats = new SpMemoryManagerStats();
  private static volatile boolean schedulerInitialized = false;
  private static ScheduledExecutorService scheduler;

  SpMemoryManager() {
    this.freeMemory = DEFAULT_INITIAL_MEMORY;
    initScheduledTask();
  }

  public void initScheduledTask() {
    if (!schedulerInitialized) {
      synchronized (SpMemoryManager.class) {
        if (!schedulerInitialized) {
          scheduler = Executors.newScheduledThreadPool(1);
          scheduler.scheduleAtFixedRate(this::ScheduledTask, SCHEDULER_INITIAL_DELAY_SECONDS, SCHEDULER_PERIOD_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
          schedulerInitialized = true;
        }
      }
    }
  }

  public void ScheduledTask() {
    this.stats = new SpMemoryManagerStats();
    stats.setAllocationRate(this.getMEMORY_ALLOCATION_RATE());
    stats.setMemoryUsedBytes(this.getMEMORY_USED_BYTES());
    stats.updateAllMetrics(DEFAULT_INITIAL_MEMORY);
    
    // Check memory usage thresholds
    checkMemoryThresholds();
  }
  
  /**
   * Checks memory usage against configured thresholds and updates blocking state.
   */
  private void checkMemoryThresholds() {
    double memoryUsageRatio = (double) getAllocatedMemory() / DEFAULT_INITIAL_MEMORY;
    
    if (memoryUsageRatio >= MEMORY_USAGE_THRESHOLD) {
      if (!memoryBlocked) {
        memoryBlocked = true;
        LOG.warn("Memory usage reached {}% threshold. Blocking data consumption.", 
            (int)(MEMORY_USAGE_THRESHOLD * 100));
      }
    } else if (memoryUsageRatio <= MEMORY_WARNING_THRESHOLD) {
      if (memoryBlocked) {
        memoryBlocked = false;
        LOG.info("Memory usage dropped below {}% threshold. Resuming data consumption.", 
            (int)(MEMORY_WARNING_THRESHOLD * 100));
      }
    }
    
    // Update warning state
    memoryWarningActive = memoryUsageRatio >= MEMORY_WARNING_THRESHOLD && 
                         memoryUsageRatio < MEMORY_USAGE_THRESHOLD;
  }

  /**
   * Allocates the specified amount of memory.
   * If insufficient memory is available, this method will block until memory becomes available.
   * If memory usage is above threshold, allocation will be blocked.
   *
   * @param bytes The number of bytes to allocate
   * @throws IllegalArgumentException if bytes is non-positive
   */
  public void allocate(long bytes) {
    if (bytes <= 0) {
      LOG.warn("Attempted to allocate non-positive memory: {} bytes", bytes);
      return;
    }
    
    // Check if memory is blocked due to threshold
    if (memoryBlocked) {
      LOG.warn("Memory allocation blocked due to high memory usage threshold. " +
               "Current usage: {}%", getMemoryUsagePercentage());
      return;
    }

    // Loop until enough memory is available
    while (true) {
      long currentFree = freeMemory;
      long newFreeMemory = currentFree - bytes;

      if (newFreeMemory >= 0) {
        // Sufficient memory available, perform allocation
        freeMemory = newFreeMemory;
        
        long allocatedMemory = DEFAULT_INITIAL_MEMORY - freeMemory;
        memoryUsedBytes = (double) allocatedMemory;
        
        updateAllocationRate(bytes);

        return;
      } else {
        // Insufficient memory, block and wait
        LOG.warn("Not enough free memory to allocate {} bytes. Current free memory: {} bytes. "
            + "Blocking allocation.", bytes, currentFree);

        synchronized (this) {
          try {
            // Block and wait for memory to be freed
            wait(WAIT_TIMEOUT_MS);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Memory allocation blocking was interrupted");
            return;
          }
        }
      }
    }
  }

  /**
   * Frees the specified amount of memory.
   * This will notify any threads waiting for memory allocation.
   *
   * @param bytes The number of bytes to free
   * @throws IllegalArgumentException if bytes is non-positive
   */
  public void free(long bytes) {
    if (bytes <= 0) {
      LOG.warn("Attempted to free non-positive memory: {} bytes", bytes);
      return;
    }

    synchronized (this) {
      freeMemory += bytes;
      
      long allocatedMemory = DEFAULT_INITIAL_MEMORY - freeMemory;
      memoryUsedBytes = (double) allocatedMemory;

      // Notify all waiting threads
      notifyAll();
    }
  }

  /**
   * Gets the current amount of free memory.
   *
   * @return The current free memory in bytes
   */
  public long getFreeMemory() {
    return freeMemory;
  }

  /**
   * Gets the total allocated memory (initial memory - free memory).
   *
   * @return The total allocated memory in bytes
   */
  public long getAllocatedMemory() {
    return DEFAULT_INITIAL_MEMORY - freeMemory;
  }

  /**
   * Gets the memory used bytes metric.
   *
   * @return The memory used in bytes
   */
  public double getMEMORY_USED_BYTES() {
    return memoryUsedBytes;
  }

  /**
   * Sets the memory used bytes metric.
   *
   * @param usedBytes The memory used in bytes
   */
  public void setMEMORY_USED_BYTES(double usedBytes) {
    this.memoryUsedBytes = usedBytes;
  }

  /**
   * Gets the memory allocation rate metric.
   *
   * @return The allocation rate in bytes per second
   */
  public double getMEMORY_ALLOCATION_RATE() {
    return memoryAllocationRate;
  }

  /**
   * Sets the memory allocation rate metric.
   *
   * @param allocationRate The allocation rate in bytes per second
   */
  public void setMEMORY_ALLOCATION_RATE(double allocationRate) {
    this.memoryAllocationRate = allocationRate;
  }

  
  private void updateAllocationRate(long allocatedBytes) {
    long currentTime = System.currentTimeMillis();
    long timeDiff = currentTime - lastAllocationTime;
    
    if (timeDiff > 0) {
      totalAllocatedBytes += allocatedBytes;
      
      double rate = (double) totalAllocatedBytes / (timeDiff / 1000.0);
      memoryAllocationRate = rate;
    }
    
    lastAllocationTime = currentTime;
  }

    public long getTotalAllocatedBytes() {
        return totalAllocatedBytes;
    }

    public SpMemoryManagerStats getStats() {
        return stats;
    }
    
    /**
     * Gets the current memory usage percentage.
     *
     * @return The memory usage percentage (0.0 to 1.0)
     */
    public double getMemoryUsagePercentage() {
        return (double) getAllocatedMemory() / DEFAULT_INITIAL_MEMORY;
    }
    
    /**
     * Checks if memory allocation is currently blocked due to high usage.
     *
     * @return true if memory allocation is blocked, false otherwise
     */
    public boolean isMemoryBlocked() {
        return memoryBlocked;
    }
    
    /**
     * Checks if memory warning is currently active.
     *
     * @return true if memory warning is active, false otherwise
     */
    public boolean isMemoryWarningActive() {
        return memoryWarningActive;
    }
    
    /**
     * Gets the current memory usage in MB.
     *
     * @return The memory usage in MB
     */
    public double getMemoryUsageMB() {
        return getAllocatedMemory() / (double) BYTES_TO_MB;
    }
    
    /**
     * Gets the total available memory in MB.
     *
     * @return The total available memory in MB
     */
    public double getTotalMemoryMB() {
        return DEFAULT_INITIAL_MEMORY / (double) BYTES_TO_MB;
    }
    
    public static void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
