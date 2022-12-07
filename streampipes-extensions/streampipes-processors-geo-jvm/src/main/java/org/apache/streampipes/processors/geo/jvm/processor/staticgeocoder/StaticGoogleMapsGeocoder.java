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
package org.apache.streampipes.processors.geo.jvm.processor.staticgeocoder;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.geo.jvm.config.ConfigKeys;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

public class StaticGoogleMapsGeocoder implements EventProcessor<StaticGoogleMapsGeocodingParameters> {

  private Double latitude;
  private Double longitude;

  @Override
  public void onInvocation(StaticGoogleMapsGeocodingParameters parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    String place = parameters.getPlace();
    String googleMapsApiKey = runtimeContext.getConfigStore().getConfig().getString(ConfigKeys.GOOGLE_API_KEY);

    if (googleMapsApiKey == null || googleMapsApiKey.equals("")) {
      throw new SpRuntimeException("Could not start Geocoder. Did you forget to add a Google Maps"
          + " API key?");
    }

    GeoApiContext context = new GeoApiContext.Builder()
        .apiKey(googleMapsApiKey)
        .build();

    try {
      GeocodingResult[] results = GeocodingApi.geocode(context,
          place).await();
      this.latitude = results[0].geometry.location.lat;
      this.longitude = results[0].geometry.location.lng;
    } catch (ApiException | IOException | InterruptedException e) {
      e.printStackTrace();
      throw new SpRuntimeException("Could not fetch geocoding result");
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    event.addField("latitude", latitude);
    event.addField("longitude", longitude);

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

}
