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

package org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversinestatic;

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

public class HaversineStaticDistanceCalculatorProcessor extends StreamPipesDataProcessor {

  private static final String LATITUDE_KEY = "latitude-key";
  private static final String LONGITUDE_KEY = "longitude-key";
  private static final String SELECTED_LATITUDE_KEY = "selected-latitude-key";
  private static final String SELECTED_LONGITUDE_KEY = "selected-longitude-key";
  private static final String DISTANCE_RUNTIME_NAME = "distance";

  String latitudeFieldMapper;
  String longitudeFieldMapper;
  Float selectedLatitude;
  Float selectedLongitude;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversinestatic")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq(Geo.LAT),
                Labels.withId(LATITUDE_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq(Geo.LNG),
                Labels.withId(LONGITUDE_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build()
        )
        .requiredFloatParameter(Labels.withId(SELECTED_LATITUDE_KEY))
        .requiredFloatParameter(Labels.withId(SELECTED_LONGITUDE_KEY))
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
    this.latitudeFieldMapper = parameters.extractor().mappingPropertyValue(LATITUDE_KEY);
    this.longitudeFieldMapper = parameters.extractor().mappingPropertyValue(LONGITUDE_KEY);
    this.selectedLatitude = parameters.extractor().singleValueParameter(SELECTED_LATITUDE_KEY, Float.class);
    this.selectedLongitude = parameters.extractor().singleValueParameter(SELECTED_LONGITUDE_KEY, Float.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    Float latitude = event.getFieldBySelector(latitudeFieldMapper).getAsPrimitive().getAsFloat();
    Float longitude = event.getFieldBySelector(longitudeFieldMapper).getAsPrimitive().getAsFloat();

    Float distance = HaversineDistanceUtil.dist(latitude, longitude, selectedLatitude, selectedLongitude);

    event.addField(DISTANCE_RUNTIME_NAME, distance);

    collector.collect(event);

  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
