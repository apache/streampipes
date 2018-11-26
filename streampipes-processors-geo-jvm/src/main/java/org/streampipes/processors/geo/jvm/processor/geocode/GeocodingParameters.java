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

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class GeocodingParameters extends EventProcessorBindingParams {

  private String city;
  private String street;
  private String number;

  public GeocodingParameters(DataProcessorInvocation graph, String city, String street, String number) {
    super(graph);
    this.city = city;
    this.street = street;
    this.number = number;
  }

  public String getCity() {
    return city;
  }

  public String getStreet() {
    return street;
  }

  public String getNumber() {
    return number;
  }
}
