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
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.IOException;

public class Geocoder implements EventProcessor<GeocodingParameters> {

  private GeocodingParameters geocodingParameters;
  private GeoApiContext context;

  @Override
  public void onInvocation(GeocodingParameters geocodingParameters, RuntimeContext runtimeContext) {
    this.geocodingParameters = geocodingParameters;
    context = new GeoApiContext.Builder()
            .apiKey(GeoJvmConfig.INSTANCE.getGoogleApiKey())
            .build();
  }

  @Override
  public void onEvent(Event in, SpOutputCollector spOutputCollector) {
    String city = in.getFieldBySelector(geocodingParameters.getCity()).getAsPrimitive().getAsString();
    String street = in.getFieldBySelector(geocodingParameters.getStreet()).getAsPrimitive()
            .getAsString();
    String number = in.getFieldBySelector(geocodingParameters.getNumber()).getAsPrimitive().getAsString();

    String searchQuery = street + " " +number + ", " +city;

    try {
      GeocodingResult[] result = GeocodingApi.geocode(context, searchQuery).await();
      if(result.length > 0) {
        in.addField("latitude", result[0].geometry.location.lat);
        in.addField("longitude", result[0].geometry.location.lng);
      }
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    spOutputCollector.collect(in);


  }

  @Override
  public void onDetach() {

  }
}
