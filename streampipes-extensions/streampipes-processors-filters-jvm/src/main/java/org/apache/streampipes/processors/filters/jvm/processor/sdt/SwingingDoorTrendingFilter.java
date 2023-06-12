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

package org.apache.streampipes.processors.filters.jvm.processor.sdt;

import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;

public class SwingingDoorTrendingFilter {

  /**
   * the maximum absolute difference the user set if the data's value is within compressionDeviation, it
   * will be compressed and discarded after compression, it will only store out of range (time, data) to form the trend
   */
  private final double compressionDeviation;

  /**
   * the minimum time distance between two stored data points if current point time to the last
   * stored point time distance <= compressionMinTimeInterval, current point will NOT be stored regardless of
   * compression deviation
   */
  private final long compressionMinTimeInterval;

  /**
   * the maximum time distance between two stored data points if current point time to the last
   * stored point time distance >= compressionMaxTimeInterval, current point will be stored regardless of
   * compression deviation
   */
  private final long compressionMaxTimeInterval;

  /**
   * isFirstValue is true when the encoder takes the first point or reset() when cur point's
   * distance to the last stored point's distance exceeds compressionMaxTimeInterval
   */
  private boolean isFirstValue = true;

  /**
   * the maximum curUpperSlope between the lastStoredPoint to the current point upperDoor can only
   * open up
   */
  private double upperDoor = Integer.MIN_VALUE;

  /**
   * the minimum curLowerSlope between the lastStoredPoint to the current point lowerDoor can only
   * open downward
   */
  private double lowerDoor = Integer.MAX_VALUE;

  /**
   * the last read time and value if upperDoor >= lowerDoor meaning out of compressionDeviation range, will
   * store lastReadPair
   */
  private long lastReadTimestamp;
  private double lastReadDouble;
  private Event lastReadEvent;

  /**
   * the last stored time and value we compare current point against lastStoredPair
   */
  private long lastStoredTimestamp;
  private double lastStoredDouble;
  private Event lastStoredEvent;

  public SwingingDoorTrendingFilter(double compressionDeviation, long compressionMinTimeInterval,
                                    long compressionMaxTimeInterval) {
    this.compressionDeviation = compressionDeviation;
    this.compressionMinTimeInterval = compressionMinTimeInterval;
    this.compressionMaxTimeInterval = compressionMaxTimeInterval;
  }

  /**
   * input a newly arrived event and output whether a new characteristic event is filtered
   *
   * @param time  the timestamp extracted from the newly arrived event
   * @param value the value extracted from the newly arrived event
   * @param event the newly arrived event
   * @return true if a new characteristic event is filtered
   */
  public boolean filter(long time, double value, Event event) {
    // store the first time and value pair
    if (isFirstValue) {
      isFirstValue = false;

      lastReadTimestamp = time;
      lastReadDouble = value;
      lastReadEvent = event;

      lastStoredTimestamp = time;
      lastStoredDouble = value;
      lastStoredEvent = event;

      return true;
    }

    // if current point to the last stored point's time distance is within compressionMinTimeInterval,
    // will not check two doors nor store any point within the compressionMinTimeInterval time range
    if (time - lastStoredTimestamp <= compressionMinTimeInterval) {
      return false;
    }

    // if current point to the last stored point's time distance is larger than compressionMaxTimeInterval,
    // will reset two doors, and store current point;
    if (time - lastStoredTimestamp >= compressionMaxTimeInterval) {
      reset(time, value, event);
      return true;
    }

    final double currentUpperSlope = (value - lastStoredDouble - compressionDeviation) / (time - lastStoredTimestamp);
    if (currentUpperSlope > upperDoor) {
      upperDoor = currentUpperSlope;
    }

    final double currentLowerSlope = (value - lastStoredDouble + compressionDeviation) / (time - lastStoredTimestamp);
    if (currentLowerSlope < lowerDoor) {
      lowerDoor = currentLowerSlope;
    }

    // current point to the lastStoredPair's value exceeds compDev, will store lastReadPair and
    // update two doors
    if (upperDoor >= lowerDoor) {
      lastStoredTimestamp = lastReadTimestamp;
      lastStoredDouble = lastReadDouble;
      lastStoredEvent = lastReadEvent;

      upperDoor = (value - lastStoredDouble - compressionDeviation) / (time - lastStoredTimestamp);
      lowerDoor = (value - lastStoredDouble + compressionDeviation) / (time - lastStoredTimestamp);

      lastReadDouble = value;
      lastReadTimestamp = time;
      lastReadEvent = event;

      return true;
    }

    lastReadDouble = value;
    lastReadTimestamp = time;
    lastReadEvent = event;

    return false;
  }

  /**
   * output the recently filtered characteristic event to the collector
   *
   * @param collector the event collector
   */
  public void forward(SpOutputCollector collector) {
    collector.collect(lastStoredEvent);
  }

  /**
   * if current point to the last stored point's time distance >= compressionMaxTimeInterval, will store current
   * point and reset upperDoor and lowerDoor
   *
   * @param time  current time
   * @param value current value
   * @param event current event
   */
  private void reset(long time, double value, Event event) {
    lastStoredTimestamp = time;
    lastStoredDouble = value;
    lastStoredEvent = event;

    upperDoor = Integer.MIN_VALUE;
    lowerDoor = Integer.MAX_VALUE;
  }
}
