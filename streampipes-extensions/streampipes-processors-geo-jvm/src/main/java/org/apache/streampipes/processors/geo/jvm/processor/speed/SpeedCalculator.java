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

package org.apache.streampipes.processors.geo.jvm.processor.speed;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.geo.jvm.processor.util.DistanceUtil;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class SpeedCalculator implements EventProcessor<SpeedCalculatorParameters> {

  private CircularFifoBuffer buffer;

  private String latitudeFieldName;
  private String longitudeFieldName;
  private String timestampFieldName;

  @Override
  public void onInvocation(SpeedCalculatorParameters parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.buffer = new CircularFifoBuffer(parameters.getCountWindowSize());
    this.latitudeFieldName = parameters.getLatitudeFieldName();
    this.longitudeFieldName = parameters.getLongitudeFieldName();
    this.timestampFieldName = parameters.getTimestampFieldName();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    if (this.buffer.isFull()) {
      Event firstEvent = (Event) buffer.get();
      Float speed = calculateSpeed(firstEvent, event);
      event.addField("speed", speed);
      collector.collect(event);
    }
    this.buffer.add(event);
  }

  private Float calculateSpeed(Event firstEvent, Event currentEvent) {
    Float firstLatitude = getFloat(firstEvent, latitudeFieldName);
    Float firstLongitude = getFloat(firstEvent, longitudeFieldName);
    Long firstTimestamp = getLong(firstEvent, timestampFieldName);

    Float currentLatitude = getFloat(currentEvent, latitudeFieldName);
    Float currentLongitude = getFloat(currentEvent, longitudeFieldName);
    Long currentTimestamp = getLong(currentEvent, timestampFieldName);

    Float distanceInKm = DistanceUtil.dist(firstLatitude, firstLongitude, currentLatitude,
        currentLongitude);

    Long durationInSeconds = (currentTimestamp - firstTimestamp) / 1000;

    Float speedInKilometersPerSecond = distanceInKm / durationInSeconds;
    Float speedInKilometerPerHour = speedInKilometersPerSecond * 3600;

    return speedInKilometerPerHour;
  }

  private Long getLong(Event event, String fieldName) {
    return event.getFieldBySelector(fieldName).getAsPrimitive().getAsLong();
  }

  private Float getFloat(Event event, String fieldName) {
    return event.getFieldBySelector(fieldName).getAsPrimitive().getAsFloat();
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
