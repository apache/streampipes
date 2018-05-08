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
package org.streampipes.model.client.pipeline;

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.grounding.EventGrounding;

public class DataSetModificationMessage extends Message {

  private EventGrounding eventGrounding;
  private String invocationId;

  public DataSetModificationMessage(
          EventGrounding eventGrounding, String invocationId) {
    super(true);
    this.eventGrounding = eventGrounding;
    this.invocationId = invocationId;
  }

  public EventGrounding getEventGrounding() {
    return eventGrounding;
  }

  public void setEventGrounding(EventGrounding eventGrounding) {
    this.eventGrounding = eventGrounding;
  }

  public String getInvocationId() {
    return invocationId;
  }

  public void setInvocationId(String invocationId) {
    this.invocationId = invocationId;
  }
}
