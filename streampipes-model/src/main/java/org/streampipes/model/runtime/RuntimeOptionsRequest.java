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
package org.streampipes.model.runtime;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public class RuntimeOptionsRequest {

  protected String requestId;

  protected List<StaticProperty> staticProperties;
  protected List<SpDataStream> inputStreams;

  public RuntimeOptionsRequest() {

  }

  public RuntimeOptionsRequest(String requestId) {
    this.requestId = requestId;
  }

  public RuntimeOptionsRequest(String requestId, List<StaticProperty> staticProperties,
                               List<SpDataStream> inputStreams) {
    this.requestId = requestId;
    this.staticProperties = staticProperties;
    this.inputStreams = inputStreams;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
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
}
