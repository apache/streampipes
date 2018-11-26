/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.geo.jvm.processor.geocode;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.io.IOException;
import java.util.Map;

public class Geocoder extends StandaloneEventProcessorEngine<GeocodingParameters> {

  private GeocodingParameters geocodingParameters;
  private GeoApiContext context;

  public Geocoder(GeocodingParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(GeocodingParameters geocodingParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.geocodingParameters = geocodingParameters;
    context = new GeoApiContext.Builder()
            .apiKey(GeoJvmConfig.INSTANCE.getGoogleApiKey())
            .build();
  }

  @Override
  public void onEvent(Map<String, Object> in, String s, SpOutputCollector spOutputCollector) {
    String city = String.valueOf(in.get(geocodingParameters.getCity()));
    String street = String.valueOf(in.get(geocodingParameters.getStreet()));
    String number = String.valueOf(in.get(geocodingParameters.getNumber()));

    String searchQuery = street + " " +number + ", " +city;

    try {
      GeocodingResult[] result = GeocodingApi.geocode(context, searchQuery).await();
      if(result.length > 0) {
        in.put("latitude", result[0].geometry.location.lat);
        in.put("longitude", result[0].geometry.location.lng);
      }
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    spOutputCollector.onEvent(in);


  }

  @Override
  public void onDetach() {

  }
}
