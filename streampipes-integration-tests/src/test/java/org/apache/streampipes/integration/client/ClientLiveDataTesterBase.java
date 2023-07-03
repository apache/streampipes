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

package org.apache.streampipes.integration.client;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.client.api.live.IConfiguredEventProducer;
import org.apache.streampipes.client.credentials.StreamPipesApiKeyCredentials;
import org.apache.streampipes.integration.utils.Utils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.vocabulary.MessageFormat;

import org.testcontainers.shaded.com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class ClientLiveDataTesterBase<T extends TransportProtocol> implements AutoCloseable {

  private List<Map<String, Object>> expectedEvents;
  private int counter;

  public IStreamPipesClient makeStreamPipesClient() {
    var client = StreamPipesClient.create(
        "localhost",
        new StreamPipesApiKeyCredentials("", ""),
        true
    );
    prepareClient(client);

    return client;
  }

  public void run() throws InterruptedException {
    startContainer();

    var dataStream = makeDataStream();
    var client = makeStreamPipesClient();

    var consumer = client.streams().subscribe(dataStream, event -> {
      assertTrue(Maps.difference(event.getRaw(), expectedEvents.get(counter)).areEqual());
      counter++;
    });

    var producer = client.streams().getProducer(dataStream);

    expectedEvents = Utils.getSimpleTestEvents();

    publishEvents(producer, expectedEvents);

    // validate that events where send correctly
    validate(expectedEvents);

    producer.close();
    consumer.unsubscribe();

  }

  private SpDataStream makeDataStream() {
    var dataStream = new SpDataStream();
    dataStream.setEventGrounding(makeEventGrounding());

    return dataStream;
  }

  private EventGrounding makeEventGrounding() {
    var grounding = new EventGrounding();
    grounding.setTransportProtocol(makeProtocol());
    grounding.setTransportFormats(List.of(new TransportFormat(MessageFormat.JSON)));

    return grounding;
  }

  public void publishEvents(IConfiguredEventProducer producer,
                            List<Map<String, Object>> expectedEvents) {
    expectedEvents.forEach(producer::publish);
  }

  public void validate(List<Map<String, Object>> expectedEvents) throws InterruptedException {
    int retry = 0;
    while (counter != expectedEvents.size() && retry < 5) {
      TimeUnit.MILLISECONDS.sleep(1000);
      retry++;
    }

    assertEquals(expectedEvents.size(), counter);
  }

  public abstract void startContainer();

  public abstract T makeProtocol();

  public abstract void prepareClient(IStreamPipesClient client);
}
