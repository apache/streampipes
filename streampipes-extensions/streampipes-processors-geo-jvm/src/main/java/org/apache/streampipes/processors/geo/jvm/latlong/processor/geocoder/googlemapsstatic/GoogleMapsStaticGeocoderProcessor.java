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

package org.apache.streampipes.processors.geo.jvm.latlong.processor.geocoder.googlemapsstatic;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.geo.jvm.config.ConfigKeys;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

public class GoogleMapsStaticGeocoderProcessor extends StreamPipesDataProcessor {
  private static final String STATIC_GEOCODER_REQUEST_KEY = "sg-request-key";
  private static final String LAT_RUNTIME_NAME = "staticgeocoder-latitude";
  private static final String LONG_RUNTIME_NAME = "staticgeocoder-longitude";
  private String staticGeocoderRequest;
  private GeoApiContext apiContext;
  private Double responseLatitude;
  private Double responseLongitude;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.latlong.processor.geocoder.googlemapsstatic")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build()
        )
        .requiredTextParameter(Labels.withId(STATIC_GEOCODER_REQUEST_KEY))
        .outputStrategy(OutputStrategies.append(
            EpProperties.doubleEp(Labels.empty(), LAT_RUNTIME_NAME, Geo.LAT),
            EpProperties.stringEp(Labels.empty(), LONG_RUNTIME_NAME, Geo.LNG))
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.staticGeocoderRequest =
        parameters.extractor().singleValueParameter(STATIC_GEOCODER_REQUEST_KEY, String.class);
    String googleMapsApiKey = runtimeContext.getConfigStore().getString(ConfigKeys.GOOGLE_API_KEY);

    if (googleMapsApiKey == null || googleMapsApiKey.equals("")) {
      throw new SpRuntimeException("Could not start Geocoder. Did you forget to add a Google Maps" + " API key?");
    }

    this.apiContext = new GeoApiContext.Builder().apiKey(googleMapsApiKey).build();

    try {
      GeocodingResult[] results = GeocodingApi.geocode(apiContext, staticGeocoderRequest).await();
      this.responseLatitude = results[0].geometry.location.lat;
      this.responseLongitude = results[0].geometry.location.lng;
    } catch (ApiException | IOException | InterruptedException e) {
      e.printStackTrace();
      throw new SpRuntimeException("Could not fetch geocoding result");
    }

  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    event.addField(LAT_RUNTIME_NAME, responseLatitude);
    event.addField(LONG_RUNTIME_NAME, responseLongitude);

    collector.collect(event);

  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
