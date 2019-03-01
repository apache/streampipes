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

package org.streampipes.sinks.brokers.jvm.jms;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class JmsPublisher implements EventSink<JmsParameters> {

  private ActiveMQPublisher publisher;
  private JsonDataFormatDefinition jsonDataFormatDefinition;

  public JmsPublisher() {
    this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(JmsParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.publisher = new ActiveMQPublisher(params.getJmsHost() + ":" + params.getJmsPort(), params.getTopic());
  }

  @Override
  public void onEvent(Event inputEvent) {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      this.publisher.publish(jsonDataFormatDefinition.fromMap(event));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.publisher.disconnect();
  }
}
