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

package org.apache.streampipes.model.loadbalancer;

/**
 * Load Balancer Related Constants
 * Centralized management of load balancing related configuration parameters
 */
public final class LoadBalancerConstants {

  // Private constructor to prevent instantiation
  private LoadBalancerConstants() {
    throw new UnsupportedOperationException("Utility class");
  }

  // CPU related constants
  public static final int DEFAULT_CPU_STANDARD = 1;

  // Memory related constants
  public static final int DEFAULT_MEMORY_STANDARD = 1;

  // Weight related constants
  public static final int MIN_WEIGHT = 0;

  // Usage rate related constants
  public static final double MIN_USAGE_PERCENT = 0.0;
  public static final double MAX_USAGE_PERCENT = 100.0;
  public static final double NEAR_LIMIT_THRESHOLD = 90.0;
}
