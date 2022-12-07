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

package org.apache.streampipes.sinks.brokers.jvm.nats;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.messaging.nats.NatsUtils;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class NatsPublisher implements EventSink<NatsParameters> {

  private String subject;
  private Connection natsConnection;
  private JsonDataFormatDefinition dataFormatDefinition;
  private static Logger log;

  public NatsPublisher() {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(NatsParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {

    log = parameters.getGraph().getLogger(NatsPublisher.class);
    var natsConfig = parameters.getNatsConfig();
    this.subject = natsConfig.getSubject();
    Options options = NatsUtils.makeNatsOptions(natsConfig);

    try {
      this.natsConnection = Nats.connect(options);
    } catch (Exception e) {
      log.error("Error when connecting to the Nats broker on " + natsConfig.getNatsUrls() + " . " + e.toString());
    }
  }

  @Override
  public void onEvent(Event inputEvent) {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      natsConnection.publish(subject, dataFormatDefinition.fromMap(event));
    } catch (SpRuntimeException e) {
      log.error("Could not publish events to Nats broker. " + e.toString());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      natsConnection.flush(Duration.ofMillis(50));
      natsConnection.close();
    } catch (TimeoutException | InterruptedException e) {
      log.error("Error when disconnecting with Nats broker. " + e.toString());
    }
  }
}
