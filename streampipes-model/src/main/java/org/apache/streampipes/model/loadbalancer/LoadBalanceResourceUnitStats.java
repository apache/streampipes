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
 * Statistics for load balance resource units.
 *
 * @param <T> the type of elements in the resource unit
 */
public class LoadBalanceResourceUnitStats<T> {

  private double eventRateIn;

  private double eventThroughputIn;

  private double eventRateOut;

  private double eventThroughputOut;

  private long lastUpdate;

  private LoadBalanceResourceUnit<T> unit;

  /**
   * Creates a new LoadBalanceResourceUnitStats instance.
   */
  public LoadBalanceResourceUnitStats() {
    this.lastUpdate = System.currentTimeMillis();
  }

  /**
   * Gets the event rate in.
   *
   * @return the event rate in
   */
  public double getEventRateIn() {
    return eventRateIn;
  }

  /**
   * Sets the event rate in.
   *
   * @param eventRateIn the event rate in
   */
  public void setEventRateIn(double eventRateIn) {
    this.eventRateIn = eventRateIn;
  }

  /**
   * Gets the event throughput in.
   *
   * @return the event throughput in
   */
  public double getEventThroughputIn() {
    return eventThroughputIn;
  }

  /**
   * Sets the event throughput in.
   *
   * @param eventThroughputIn the event throughput in
   */
  public void setEventThroughputIn(double eventThroughputIn) {
    this.eventThroughputIn = eventThroughputIn;
  }

  /**
   * Gets the event rate out.
   *
   * @return the event rate out
   */
  public double getEventRateOut() {
    return eventRateOut;
  }

  /**
   * Sets the event rate out.
   *
   * @param eventRateOut the event rate out
   */
  public void setEventRateOut(double eventRateOut) {
    this.eventRateOut = eventRateOut;
  }

  /**
   * Gets the event throughput out.
   *
   * @return the event throughput out
   */
  public double getEventThroughputOut() {
    return eventThroughputOut;
  }

  /**
   * Sets the event throughput out.
   *
   * @param eventThroughputOut the event throughput out
   */
  public void setEventThroughputOut(double eventThroughputOut) {
    this.eventThroughputOut = eventThroughputOut;
  }

  /**
   * Gets the last update time.
   *
   * @return the last update time
   */
  public long getLastUpdate() {
    return lastUpdate;
  }

  /**
   * Sets the last update time.
   *
   * @param lastUpdate the last update time
   */
  public void setLastUpdate(long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  /**
   * Gets the resource unit.
   *
   * @return the resource unit
   */
  public LoadBalanceResourceUnit<T> getUnit() {
    return unit;
  }

  /**
   * Sets the resource unit.
   *
   * @param unit the resource unit
   */
  public void setUnit(LoadBalanceResourceUnit<T> unit) {
    this.unit = unit;
  }
}