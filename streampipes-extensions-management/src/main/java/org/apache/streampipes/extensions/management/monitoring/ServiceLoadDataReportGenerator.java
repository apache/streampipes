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
package org.apache.streampipes.extensions.management.monitoring;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.model.loadbalancer.Usage;

import com.google.common.util.concurrent.AtomicDouble;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service Load Data Report Generator Responsible for collecting and calculating service CPU and
 * memory usage, and generating load reports
 */
public class ServiceLoadDataReportGenerator {

  private static final Logger log = LoggerFactory.getLogger(ServiceLoadDataReportGenerator.class);

  // Configuration constants
  private static final int CPU_CHECK_INTERVAL_MILLIS = 100;
  private static final int USAGE_CALCULATION_INTERVAL_MINUTES = 1;
  private static final int BYTES_TO_MB = 1024 * 1024;

  private final ElementServiceStats serviceStats;

  // System resources
  private final OperatingSystemMXBean systemBean;
  private final double totalCPULimit;
  private final ScheduledExecutorService executorService;

  // Thread-safe CPU usage statistics
  private final AtomicDouble cpuUsageSum = new AtomicDouble(0.0);
  private final AtomicLong cpuUsageCount = new AtomicLong(0);

  // Initialization state
  private volatile boolean initialized = false;

  private volatile ServiceLoadDataReport currentReport = new ServiceLoadDataReport();
  private volatile ServiceLoadDataReport previousReport = new ServiceLoadDataReport();

  // Singleton instance
  private static volatile ServiceLoadDataReportGenerator instance;

  private ServiceLoadDataReportGenerator() {
    this.systemBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    this.totalCPULimit = calculateTotalCPULimit();
    this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "ServiceLoadDataReportGenerator");
      t.setDaemon(true);
      return t;
    });
    serviceStats = new ElementServiceStats(DeclarersSingleton.getInstance().getServiceId());
  }

  /**
   * Get singleton instance
   * 
   * @return ServiceLoadDataReportGenerator instance
   */
  public static ServiceLoadDataReportGenerator getInstance() {
    if (instance == null) {
      synchronized (ServiceLoadDataReportGenerator.class) {
        if (instance == null) {
          instance = new ServiceLoadDataReportGenerator();
        }
      }
    }
    return instance;
  }

  /**
   * Initialize service load data report generator
   */
  public synchronized void initialize() {
    if (initialized) {
      log.warn("ServiceLoadDataReportGenerator already initialized");
      return;
    }

    log.info("Initializing ServiceLoadDataReportGenerator");

    try {
      // Initial data collection
      calculateInitialUsage();

      // Start scheduled tasks
      startScheduledTasks();

      initialized = true;
      log.info("ServiceLoadDataReportGenerator initialized successfully");
    } catch (Exception e) {
      log.error("Failed to initialize ServiceLoadDataReportGenerator", e);
      throw new RuntimeException("Failed to initialize ServiceLoadDataReportGenerator", e);
    }
  }

  /**
   * Shutdown service load data report generator
   */
  public synchronized void shutdown() {
    if (!initialized) {
      return;
    }

    log.info("Shutting down ServiceLoadDataReportGenerator");

    try {
      executorService.shutdown();
      if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      initialized = false;
      log.info("ServiceLoadDataReportGenerator shutdown successfully");
    } catch (InterruptedException e) {
      log.warn("Interrupted while shutting down ServiceLoadDataReportGenerator", e);
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Get current load report
   * 
   * @return Current service load data report
   */
  public ServiceLoadDataReport getCurrentReport() {
    return currentReport;
  }

  /**
   * Get previous load report (historical data)
   * 
   * @return Previous service load data report
   */
  public ServiceLoadDataReport getPreviousReport() {
    return previousReport;
  }

  /**
   * Calculate initial usage
   */
  private void calculateInitialUsage() {
    calculateCpuLoad();
    calculateUsages();
  }

  /**
   * Start scheduled tasks
   */
  private void startScheduledTasks() {
    // CPU load calculation task
    executorService.scheduleWithFixedDelay(this::calculateCpuLoad, CPU_CHECK_INTERVAL_MILLIS,
                                           CPU_CHECK_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);

    // Usage calculation task
    executorService.scheduleWithFixedDelay(this::calculateUsages,
                                           USAGE_CALCULATION_INTERVAL_MINUTES,
                                           USAGE_CALCULATION_INTERVAL_MINUTES, TimeUnit.MINUTES);
  }

  /**
   * Calculate usage and update reports with historical data
   */
  public void calculateUsages() {
    try {
      // Create current snapshot
      ServiceLoadDataReport.ResourceSnapshot currentSnapshot =
          new ServiceLoadDataReport.ResourceSnapshot(getCpuUsage(), getMemoryUsage());

      // Create historical snapshot from previous current
      ServiceLoadDataReport.ResourceSnapshot historicalSnapshot;
      if (currentReport != null && currentReport.isComplete()) {
        historicalSnapshot = currentReport.getCurrent();
      } else {
        // For first report, use current as historical
        historicalSnapshot = currentSnapshot;
      }

      // Create new report
      ServiceLoadDataReport newReport =
          new ServiceLoadDataReport(currentSnapshot, historicalSnapshot, 0);

      // Calculate and set weight
      newReport.calculateWeight();

      // Update service stats
      serviceStats.setCpuUsage(currentSnapshot.getCpu().getUsage());
      serviceStats.setMemoryUsage(currentSnapshot.getMemory().getUsage());
      serviceStats.setSystemLoad(currentSnapshot.getAverageUsagePercent());
      serviceStats.setHistoricalSystemLoad(historicalSnapshot.getAverageUsagePercent());
      serviceStats.updateAllMetrics();

      // Store previous and update current
      previousReport = currentReport;
      currentReport = newReport;

      log.debug("Successfully calculated usage - Current: CPU={}%, Memory={}%, Historical: CPU={}%, Memory={}%",
                currentSnapshot.getCpu().percentUsage(), currentSnapshot.getMemory().percentUsage(),
                historicalSnapshot.getCpu().percentUsage(),
                historicalSnapshot.getMemory().percentUsage());
    } catch (Exception e) {
      log.error("Error calculating usage", e);
    }
  }

  /**
   * Calculate CPU load
   */
  public void calculateCpuLoad() {
    try {
      double cpuLoad = getCpuLoadValue();
      if (!Double.isNaN(cpuLoad) && cpuLoad >= 0) {
        cpuUsageSum.addAndGet(Math.max(cpuLoad, 0));
        cpuUsageCount.incrementAndGet();
      }
    } catch (Exception e) {
      log.warn("Error calculating CPU load", e);
    }
  }

  /**
   * Get CPU load value
   * 
   * @return CPU load value
   */
  private double getCpuLoadValue() {
    try {
      double cpuLoad = systemBean.getCpuLoad();
      if (Double.isNaN(cpuLoad) || cpuLoad < 0) {
        cpuLoad = systemBean.getProcessCpuLoad();
      }
      return cpuLoad;
    } catch (Exception e) {
      log.warn("Error getting CPU load", e);
      return Double.NaN;
    }
  }

  /**
   * Calculate total CPU limit
   * 
   * @return Total CPU limit
   */
  private double calculateTotalCPULimit() {
    return 100.0 * Runtime.getRuntime().availableProcessors();
  }

  /**
   * Get total CPU usage
   * 
   * @return Total CPU usage
   */
  private double getTotalCpuUsage() {
    long count = cpuUsageCount.get();
    if (count == 0) {
      return 0.0;
    }

    double usage = cpuUsageSum.get() / count;

    // Reset counters
    cpuUsageSum.set(0.0);
    cpuUsageCount.set(0);

    return usage;
  }

  /**
   * Get CPU usage
   * 
   * @return CPU usage
   */
  private Usage getCpuUsage() {
    double rawUsage = getTotalCpuUsage();
    double scaledUsage = rawUsage * totalCPULimit;
    return new Usage(scaledUsage, totalCPULimit);
  }

  /**
   * Get memory usage
   * 
   * @return Memory usage
   */
  private Usage getMemoryUsage() {
    try {
      long totalMemory = systemBean.getTotalMemorySize();
      long freeMemory = systemBean.getFreeMemorySize();

      double totalMB = (double) totalMemory / BYTES_TO_MB;
      double freeMB = (double) freeMemory / BYTES_TO_MB;
      double usedMB = totalMB - freeMB;

      return new Usage(usedMB, totalMB);
    } catch (Exception e) {
      log.error("Error getting memory usage", e);
      return new Usage(0.0, 0.0);
    }
  }

  /**
   * Check if initialized
   * 
   * @return Whether initialized
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Get CPU usage statistics
   * 
   * @return CPU usage statistics
   */
  public String getCpuUsageStats() {
    return String.format("CPU Usage: sum=%.2f, count=%d", cpuUsageSum.get(), cpuUsageCount.get());
  }

  // Backward compatible static methods
  /**
   * @deprecated Use getInstance().initialize() instead
   */
  @Deprecated
  public static void init() {
    getInstance().initialize();
  }

  /**
   * @deprecated Use getInstance().calculateUsage() instead
   */
  @Deprecated
  public static void calculateUsage() {
    getInstance().calculateUsages();
  }
}
