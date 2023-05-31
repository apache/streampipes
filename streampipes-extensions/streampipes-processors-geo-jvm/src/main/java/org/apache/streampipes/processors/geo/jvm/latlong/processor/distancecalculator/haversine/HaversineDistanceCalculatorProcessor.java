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

package org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversine;

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

import java.net.URI;

public class HaversineDistanceCalculatorProcessor extends StreamPipesDataProcessor {
  private static final String LAT_1_KEY = "lat1";
  private static final String LONG_1_KEY = "long1";
  private static final String LAT_2_KEY = "lat2";
  private static final String LONG_2_KEY = "long2";
  private static final String DISTANCE_RUNTIME_NAME = "distance";
  String lat1FieldMapper;
  String long1FieldMapper;
  String lat2FieldMapper;
  String long2FieldMapper;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversine")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT),
                Labels.withId(LAT_1_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG),
                Labels.withId(LONG_1_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT),
                Labels.withId(LAT_2_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG),
                Labels.withId(LONG_2_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build()
        )
        .outputStrategy(OutputStrategies.append(PrimitivePropertyBuilder
                .create(Datatypes.Float, DISTANCE_RUNTIME_NAME)
                .domainProperty(SO.NUMBER)
                .measurementUnit(URI.create("http://qudt.org/vocab/unit#Kilometer"))
                .build())
            )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.lat1FieldMapper = parameters.extractor().mappingPropertyValue(LAT_1_KEY);
    this.long1FieldMapper = parameters.extractor().mappingPropertyValue(LONG_1_KEY);
    this.lat2FieldMapper = parameters.extractor().mappingPropertyValue(LAT_2_KEY);
    this.long2FieldMapper = parameters.extractor().mappingPropertyValue(LONG_2_KEY);

  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    float lat1 = event.getFieldBySelector(lat1FieldMapper).getAsPrimitive().getAsFloat();
    float long1 = event.getFieldBySelector(long1FieldMapper).getAsPrimitive().getAsFloat();
    float lat2 = event.getFieldBySelector(lat2FieldMapper).getAsPrimitive().getAsFloat();
    float long2 = event.getFieldBySelector(long2FieldMapper).getAsPrimitive().getAsFloat();

    double resultDist = HaversineDistanceUtil.dist(lat1, long1, lat2, long2);

    event.addField(DISTANCE_RUNTIME_NAME, resultDist);

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
