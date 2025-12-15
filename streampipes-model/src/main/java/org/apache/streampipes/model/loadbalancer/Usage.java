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

import java.util.Objects;

/**
* Resource Usage
* Represents the usage and limit of a certain resource (such as CPU, memory)
*/
public class Usage {

  private final double usage;
  private final double limit;

  /**
   * Constructor
   * @param usage Usage amount
   * @param limit Limit amount
   * @throws IllegalArgumentException If parameters are invalid
   */
  public Usage(double usage, double limit) {
    this.usage = validateUsage(usage);
    this.limit = validateLimit(limit);
  }

  /**
   * Default constructor, creates empty usage
   */
  public Usage() {
    this(0.0, 0.0);
}

  /**
   * Get usage amount
   * @return Usage amount
   */
  public double getUsage() {
    return usage;
}

  /**
   * Get limit amount
   * @return Limit amount
   */
  public double getLimit() {
    return limit;
}

  /**
   * Calculate usage percentage
   * @return Usage percentage (0-100)
   */
  public double percentUsage() {
    if (limit <= 0) {
      return LoadBalancerConstants.MIN_USAGE_PERCENT;
    }
    return Math.min(LoadBalancerConstants.MAX_USAGE_PERCENT, (usage / limit) * 100.0);
  }

  /**
   * Get integer representation of usage amount
   * @return Integer part of usage amount
   */
  public int getUsageInt() {
    return (int) Math.round(usage);
}

  /**
   * Get integer representation of limit amount
   * @return Integer part of limit amount
   */
  public int getLimitInt() {
    return (int) Math.round(limit);
}

  /**
   * Check if exceeds limit
   * @return Whether exceeds limit
   */
  public boolean isOverLimit() {
    return usage > limit;
}

  /**
   * Check if near limit (usage rate exceeds 90%)
   * @return Whether near limit
   */
  public boolean isNearLimit() {
    return percentUsage() > LoadBalancerConstants.NEAR_LIMIT_THRESHOLD;
}

  /**
   * Get remaining capacity
   * @return Remaining capacity
   */
  public double getRemainingCapacity() {
    return Math.max(0.0, limit - usage);
}

  /**
   * Get usage ratio (0.0-1.0)
   * @return Usage ratio
   */
  public double getUsageRatio() {
    if (limit <= 0) {
      return 0.0;
    }
    return Math.min(1.0, usage / limit);
  }

  /**
   * Create a copy of usage
   * @return New Usage instance
   */
  public Usage copy() {
    return new Usage(usage, limit);
  }

  /**
   * Validate usage amount
   * @param usage Usage amount
   * @return Validated usage amount
   */
  private double validateUsage(double usage) {
    if (Double.isNaN(usage)) {
      throw new IllegalArgumentException("Usage cannot be NaN");
    }
    if (Double.isInfinite(usage)) {
      throw new IllegalArgumentException("Usage cannot be infinite");
    }
    if (usage < 0) {
      throw new IllegalArgumentException("Usage cannot be negative");
    }
    return usage;
  }

  /**
   * Validate limit amount
   * @param limit Limit amount
   * @return Validated limit amount
   */
  private double validateLimit(double limit) {
    if (Double.isNaN(limit)) {
      throw new IllegalArgumentException("Limit cannot be NaN");
    }
    if (Double.isInfinite(limit)) {
      throw new IllegalArgumentException("Limit cannot be infinite");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit cannot be negative");
    }
    return limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Usage usage1 = (Usage) o;
    return Double.compare(usage1.usage, usage) == 0
            && Double.compare(usage1.limit, limit) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(usage, limit);
  }

  @Override
  public String toString() {
    return "Usage{"
            + "usage="
            + usage
            + ", limit="
            + limit + ", percentUsage="
            + String.format("%.2f", percentUsage())
            + "%"
            + '}';
  }
}
