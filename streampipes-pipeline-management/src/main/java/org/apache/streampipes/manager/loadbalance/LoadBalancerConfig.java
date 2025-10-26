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

package org.apache.streampipes.manager.loadbalance;

/**
 * Configuration constants for load balancing operations.
 */
public class LoadBalancerConfig {

  /**
   * CPU usage weight when calculating new resource usage.
   */
  public static double CPUResourceWeight = 1.0;

  /**
   * Memory usage weight when calculating new resource usage.
   */
  public static double MemoryResourceWeight = 1.0;

  /**
   * Directory memory usage weight when calculating new resource usage.
   */
  public static double DirMemoryResourceWeight = 1.0;

  /**
   * Service resource usage threshold for migration.
   */
  public static float ThresholdMigratorPercentage = 20.0F;

  /**
   * Minimum migrator percentage.
   */
  public static float MinMigratorPercentage = 20.0F;

  /**
   * Overloaded threshold percentage.
   */
  public static float OverloadedThresholdPercentage = 85F;

  /**
   * History usage accounts for when calculating new resource usage.
   */
  public static float HistoryResourcePercentage = 0.9F;

  /**
   * Message-rate percentage threshold between highest and least loaded service for uniform load migration.
   */
  public static int MsgRateDifferenceMigratorThreshold = 85;

  /**
   * Target standard deviation range.
   */
  public static float LoadTargetStd = 25.0F;

  /**
   * Service selector strategy.
   */
  public static String selector = "WeightedRandomSelector";

  /**
   * Pipeline migrator strategy.
   */
  public static String migrator = "ThresholdMigrator";

}
