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
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

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

public class DittoSink extends StreamPipesDataSink {

  private static final String DITTO_API_ENDPOINT_KEY = "dittoApiEndpointKey";
  private static final String DITTO_USER_KEY = "dittoUserKey";
  private static final String DITTO_PASSWORD_KEY = "dittoPasswordKey";

  private static final String DITTO_THING_ID_KEY = "dittoThingIdKey";
  private static final String DITTO_FEATURE_ID_KEY = "dittoFeatureIdKey";

  private static final String SELECTED_FIELDS_KEY = "selectedFieldsKey";

  private DittoClient client;
  private String thingId;
  private String featureId;

  private List<String> selectedFields;


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.ditto")
        .category(DataSinkType.FORWARD)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithNaryMapping(
            EpRequirements.anyProperty(),
            Labels.withId(SELECTED_FIELDS_KEY),
            PropertyScope.NONE).build())
        .requiredTextParameter(Labels.withId(DITTO_API_ENDPOINT_KEY))
        .requiredTextParameter(Labels.withId(DITTO_USER_KEY))
        .requiredSecret(Labels.withId(DITTO_PASSWORD_KEY))
        .requiredTextParameter(Labels.withId(DITTO_THING_ID_KEY))
        .requiredTextParameter(Labels.withId(DITTO_FEATURE_ID_KEY))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    String dittoApiEndpoint = extractor.textParameter(DITTO_API_ENDPOINT_KEY);
    String dittoUser = extractor.textParameter(DITTO_USER_KEY);
    String dittoPassword = extractor.secretValue(DITTO_PASSWORD_KEY);

    this.thingId = extractor.textParameter(DITTO_THING_ID_KEY);
    this.featureId = extractor.textParameter(DITTO_FEATURE_ID_KEY);
    this.selectedFields = extractor.mappingPropertyValues(SELECTED_FIELDS_KEY);

    MessagingConfiguration configuration = WebSocketMessagingConfiguration.newBuilder()
        .endpoint(dittoApiEndpoint)
        .build();
    WebSocketMessagingProvider provider = WebSocketMessagingProvider.newInstance(configuration,
        AuthenticationProviders
            .basic(BasicAuthenticationConfiguration.newBuilder()
                .username(dittoUser)
                .password(dittoPassword)
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
