/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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
package org.streampipes.processors.geo.jvm.processor.geocoder;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class GoogleMapsGeocodingParameters extends EventProcessorBindingParams {

  private String placeField;
  private String googleMapsApiKey;

  public GoogleMapsGeocodingParameters(DataProcessorInvocation graph, String placeField,
                                       String googleMapsApiKey) {
    super(graph);
    this.placeField = placeField;
    this.googleMapsApiKey = googleMapsApiKey;
  }

  public String getPlaceField() {
    return placeField;
  }

  public String getGoogleMapsApiKey() {
    return googleMapsApiKey;
  }
}
