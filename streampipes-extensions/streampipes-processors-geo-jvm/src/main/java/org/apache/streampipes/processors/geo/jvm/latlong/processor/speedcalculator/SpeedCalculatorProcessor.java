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

package org.apache.streampipes.processors.geo.jvm.latlong.processor.speedcalculator;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.latlong.helper.HaversineDistanceUtil;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.net.URI;

public class SpeedCalculatorProcessor extends StreamPipesDataProcessor {
  private static final String TIMESTAMP_KEY = "timestamp-key";
  private static final String LATITUDE_KEY = "latitude-key";
  private static final String LONGITUDE_KEY = "longitude-key";
  private static final String COUNT_WINDOW_KEY = "count-window-key";
  private static final String SPEED_RUNTIME_NAME = "speed";
  private String latitudeFieldMapper;
  private String longitudeFieldMapper;
  private String timestampFieldMapper;
  private Integer countWindowSize;
  private CircularFifoBuffer buffer;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.latlong.processor.speedcalculator")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(TIMESTAMP_KEY), PropertyScope.HEADER_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT)
                , Labels.withId(LATITUDE_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG)
                , Labels.withId(LONGITUDE_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredIntegerParameter(Labels.withId(COUNT_WINDOW_KEY))
        .outputStrategy(
            OutputStrategies.append(PrimitivePropertyBuilder
                .create(Datatypes.Float, SPEED_RUNTIME_NAME)
                .domainProperty(SO.NUMBER)
                .measurementUnit(URI.create("http://qudt.org/vocab/unit#KilometerPerHour"))
                .build())
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.latitudeFieldMapper = parameters.extractor().mappingPropertyValue(LATITUDE_KEY);
    this.longitudeFieldMapper = parameters.extractor().mappingPropertyValue(LONGITUDE_KEY);
    this.timestampFieldMapper = parameters.extractor().mappingPropertyValue(TIMESTAMP_KEY);
    this.countWindowSize = parameters.extractor().singleValueParameter(COUNT_WINDOW_KEY, Integer.class);
    this.buffer = new CircularFifoBuffer(countWindowSize);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    if (this.buffer.isFull()) {
      Event firstEvent = (Event) buffer.get();
      double speed = calculateSpeed(firstEvent, event);
      event.addField(SPEED_RUNTIME_NAME, speed);
      collector.collect(event);
    }
    this.buffer.add(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private double calculateSpeed(Event firstEvent, Event currentEvent) {
    Float firstLatitude = getFloat(firstEvent, this.latitudeFieldMapper);
    Float firstLongitude = getFloat(firstEvent, this.longitudeFieldMapper);
    Long firstTimestamp = getLong(firstEvent, this.timestampFieldMapper);

    Float currentLatitude = getFloat(currentEvent, this.latitudeFieldMapper);
    Float currentLongitude = getFloat(currentEvent, this.longitudeFieldMapper);
    Long currentTimestamp = getLong(currentEvent, this.timestampFieldMapper);

    Float distanceInKm = HaversineDistanceUtil.dist(firstLatitude, firstLongitude, currentLatitude,
        currentLongitude);

    Double durationInSeconds = Double.valueOf((currentTimestamp - firstTimestamp) / 1000.0);

    double speedInKilometersPerSecond = distanceInKm / durationInSeconds;
    double speedInKilometerPerHour = speedInKilometersPerSecond * 3600;

    return speedInKilometerPerHour;
  }

  private Long getLong(Event event, String fieldName) {
    return event.getFieldBySelector(fieldName).getAsPrimitive().getAsLong();
  }

  private Float getFloat(Event event, String fieldName) {
    return event.getFieldBySelector(fieldName).getAsPrimitive().getAsFloat();
  }
}
