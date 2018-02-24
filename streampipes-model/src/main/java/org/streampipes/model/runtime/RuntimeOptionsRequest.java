/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime;

import org.streampipes.model.schema.EventProperty;

public class RuntimeOptionsRequest {

  private String requestId;

  private EventProperty mappedEventPropety;

  public RuntimeOptionsRequest() {

  }

  public RuntimeOptionsRequest(String requestId, EventProperty mappedEventPropety) {
    this.requestId = requestId;
    this.mappedEventPropety = mappedEventPropety;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public EventProperty getMappedEventPropety() {
    return mappedEventPropety;
  }

  public void setMappedEventPropety(EventProperty mappedEventPropety) {
    this.mappedEventPropety = mappedEventPropety;
  }
}
