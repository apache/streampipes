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
package org.apache.streampipes.connect.adapters.wikipedia;

import org.apache.streampipes.extensions.api.connect.AdapterOutputCollector;

import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

public class WikipediaSseConsumer {

  private Boolean running = true;

  public void consumeEventStream(String url, AdapterOutputCollector consumer) throws Exception {
    Client client = ClientBuilder.newBuilder().register(new SseFeature()).build();
    WebTarget target = client.target(url);
    EventInput e = null;
    while (running) {
      Thread.sleep(100);
      if (e == null || e.isClosed()) {
        // (re)connect
        e = target.request().get(EventInput.class);
        e.setChunkType("text/event-stream");
      }

      final InboundEvent inboundEvent = e.read();
      if (inboundEvent == null) {
        break;
      } else {
        String data = inboundEvent.readData();
        consumer.onEvent(data);
      }
    }
  }

  public void stop() {
    this.running = false;
  }

}
