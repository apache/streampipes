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
package org.apache.streampipes.sinks.databases.jvm.ditto;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.eclipse.ditto.client.DittoClient;
import org.eclipse.ditto.client.DittoClients;
import org.eclipse.ditto.client.configuration.BasicAuthenticationConfiguration;
import org.eclipse.ditto.client.configuration.MessagingConfiguration;
import org.eclipse.ditto.client.configuration.WebSocketMessagingConfiguration;
import org.eclipse.ditto.client.messaging.AuthenticationProviders;
import org.eclipse.ditto.client.messaging.internal.WebSocketMessagingProvider;
import org.eclipse.ditto.client.twin.TwinThingHandle;
import org.eclipse.ditto.model.things.Feature;
import org.eclipse.ditto.model.things.Features;
import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.model.things.ThingId;
import org.eclipse.ditto.signals.commands.things.exceptions.ThingNotAccessibleException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Ditto implements EventSink<DittoParameters> {

  private DittoClient client;
  private String thingId;
  private String featureId;

  private List<String> selectedFields;

  @Override
  public void onInvocation(DittoParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {

    this.thingId = parameters.getThingId();
    this.featureId = parameters.getFeatureId();
    this.selectedFields = parameters.getSelectedFields();

    MessagingConfiguration configuration = WebSocketMessagingConfiguration.newBuilder()
        .endpoint(parameters.getDittoApiEndpoint())
        .build();
    WebSocketMessagingProvider provider = WebSocketMessagingProvider.newInstance(configuration,
        AuthenticationProviders
            .basic(BasicAuthenticationConfiguration.newBuilder()
                .username(parameters.getDittoUser())
                .password(parameters.getDittoPassword())
                .build()),
        Executors.newFixedThreadPool(4));
    this.client = DittoClients.newInstance(provider);

    TwinThingHandle twinHandle = client.twin().forId(ThingId.of(thingId));

    try {
      twinHandle.retrieve().get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof ThingNotAccessibleException) {
        try {
          client.twin().create(Thing.newBuilder().setId(ThingId.of(this.thingId)).setFeature(featureId).build()).get();
        } catch (InterruptedException | ExecutionException ex) {
          throw new SpRuntimeException(ex);
        }
      }
    } catch (InterruptedException e) {
      throw new SpRuntimeException(e);
    }

    boolean present;
    try {
      present = twinHandle.retrieve().get().getFeatures().map(features -> features.getFeature(
          featureId)).isPresent();
    } catch (InterruptedException | ExecutionException e) {
      throw new SpRuntimeException(e);
    }
    if (!present) {
      System.out.println("feature not present, adding feature...");
      twinHandle.setFeatures(Features.newBuilder().set(Feature.newBuilder().withId(featureId).build()).build());
    }
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    Event reducedEvent = event.getSubset(selectedFields);

    Map<String, Object> raw = reducedEvent.getRaw();
    for (String key : raw.keySet()) {
      try {
        client.twin().forId(ThingId.of(this.thingId)).forFeature(this.featureId).putProperty(key,
            String.valueOf(raw.get(key))).get();
      } catch (InterruptedException | ExecutionException e) {
        throw new SpRuntimeException(e);
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.client.destroy();
  }
}
