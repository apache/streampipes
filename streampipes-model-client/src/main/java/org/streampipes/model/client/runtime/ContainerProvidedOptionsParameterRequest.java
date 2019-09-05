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
package org.streampipes.model.client.runtime;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public class ContainerProvidedOptionsParameterRequest {

  private List<StaticProperty> staticProperties;
  private List<SpDataStream> inputStreams;

  private String belongsTo;
  private String runtimeResolvableInternalId;

  public ContainerProvidedOptionsParameterRequest(List<StaticProperty> staticProperties,
                                                  List<SpDataStream> inputStreams, String belongsTo) {
    this.staticProperties = staticProperties;
    this.inputStreams = inputStreams;
    this.belongsTo = belongsTo;
  }

  public ContainerProvidedOptionsParameterRequest() {

  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public List<SpDataStream> getInputStreams() {
    return inputStreams;
  }

  public void setInputStreams(List<SpDataStream> inputStreams) {
    this.inputStreams = inputStreams;
  }

  public String getBelongsTo() {
    return belongsTo;
  }

  public void setBelongsTo(String belongsTo) {
    this.belongsTo = belongsTo;
  }

  public String getRuntimeResolvableInternalId() {
    return runtimeResolvableInternalId;
  }

  public void setRuntimeResolvableInternalId(String runtimeResolvableInternalId) {
    this.runtimeResolvableInternalId = runtimeResolvableInternalId;
  }
}
