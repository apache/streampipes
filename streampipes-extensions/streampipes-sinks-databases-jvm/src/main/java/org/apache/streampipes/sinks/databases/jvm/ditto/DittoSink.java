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
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.eclipse.ditto.client.DisconnectedDittoClient;
import org.eclipse.ditto.client.DittoClient;
import org.eclipse.ditto.client.DittoClients;
import org.eclipse.ditto.client.configuration.BasicAuthenticationConfiguration;
import org.eclipse.ditto.client.configuration.MessagingConfiguration;
import org.eclipse.ditto.client.configuration.WebSocketMessagingConfiguration;
import org.eclipse.ditto.client.messaging.AuthenticationProviders;
import org.eclipse.ditto.client.messaging.MessagingProvider;
import org.eclipse.ditto.client.messaging.MessagingProviders;
import org.eclipse.ditto.client.twin.TwinThingHandle;
import org.eclipse.ditto.things.model.Feature;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.exceptions.ThingNotAccessibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DittoSink implements IStreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(DittoSink.class);

  private static final long REQUEST_TIMEOUT_SECONDS = 30;

  private static final String DITTO_API_ENDPOINT_KEY = "dittoApiEndpointKey";
  private static final String DITTO_USER_KEY = "dittoUserKey";
  private static final String DITTO_PASSWORD_KEY = "dittoPasswordKey";

  private static final String DITTO_THING_ID_KEY = "dittoThingIdKey";
  private static final String DITTO_FEATURE_ID_KEY = "dittoFeatureIdKey";

  private static final String SELECTED_FIELDS_KEY = "selectedFieldsKey";

  private DisconnectedDittoClient disconnectedClient;
  private DittoClient client;
  private ThingId thingId;
  private String featureId;

  private List<String> selectedFields;


  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        DittoSink::new,
        DataSinkBuilder
            .create("org.apache.streampipes.sinks.databases.ditto", 0)
            .category(DataSinkType.FORWARD)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithNaryMapping(
                EpRequirements.anyProperty(),
                Labels.withId(SELECTED_FIELDS_KEY),
                PropertyScope.NONE).build())
            .requiredTextParameter(Labels.withId(DITTO_API_ENDPOINT_KEY))
            .requiredTextParameter(Labels.withId(DITTO_USER_KEY))
            .requiredSecret(Labels.withId(DITTO_PASSWORD_KEY))
            .requiredTextParameter(Labels.withId(DITTO_THING_ID_KEY))
            .requiredTextParameter(Labels.withId(DITTO_FEATURE_ID_KEY))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataSinkParameters parameters,
      EventSinkRuntimeContext runtimeContext) {
    var extractor = parameters.extractor();
    String dittoApiEndpoint = extractor.textParameter(DITTO_API_ENDPOINT_KEY);
    String dittoUser = extractor.textParameter(DITTO_USER_KEY);
    String dittoPassword = extractor.secretValue(DITTO_PASSWORD_KEY);

    this.thingId = ThingId.of(extractor.textParameter(DITTO_THING_ID_KEY));
    this.featureId = extractor.textParameter(DITTO_FEATURE_ID_KEY);
    this.selectedFields = extractor.mappingPropertyValues(SELECTED_FIELDS_KEY);

    MessagingConfiguration configuration = WebSocketMessagingConfiguration.newBuilder()
        .endpoint(dittoApiEndpoint)
        .build();
    MessagingProvider provider = MessagingProviders.webSocket(
        configuration,
        AuthenticationProviders.basic(BasicAuthenticationConfiguration.newBuilder()
            .username(dittoUser)
            .password(dittoPassword)
            .build())
    );
    this.disconnectedClient = DittoClients.newInstance(provider);
    this.client = await(disconnectedClient.connect());

    TwinThingHandle twinHandle = client.twin().forId(thingId);
    Thing thing = retrieveOrCreateThing(twinHandle);

    boolean featurePresent = thing.getFeatures()
        .flatMap(features -> features.getFeature(featureId))
        .isPresent();
    if (!featurePresent) {
      LOG.info("Feature {} not present on thing {}, adding feature", featureId, thingId);
      await(twinHandle.putFeature(Feature.newBuilder().withId(featureId).build()));
    }
  }

  private Thing retrieveOrCreateThing(TwinThingHandle twinHandle) {
    try {
      return await(twinHandle.retrieve());
    } catch (SpRuntimeException e) {
      if (e.getCause() instanceof ThingNotAccessibleException) {
        LOG.info("Thing {} does not exist, creating thing", thingId);
        return await(client.twin().create(Thing.newBuilder()
            .setId(thingId)
            .setFeature(featureId)
            .build()));
      }
      throw e;
    }
  }

  private <T> T await(CompletionStage<T> stage) {
    try {
      return stage.toCompletableFuture().get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SpRuntimeException(e);
    } catch (ExecutionException e) {
      throw new SpRuntimeException(e.getCause());
    } catch (TimeoutException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    Event reducedEvent = event.getSubset(selectedFields);

    Map<String, Object> raw = reducedEvent.getRaw();
    var featureHandle = client.twin().forId(thingId).forFeature(featureId);
    for (Map.Entry<String, Object> entry : raw.entrySet()) {
      await(featureHandle.putProperty(entry.getKey(), String.valueOf(entry.getValue())));
    }
  }

  @Override
  public void onPipelineStopped() {
    if (this.client != null) {
      this.client.destroy();
    } else if (this.disconnectedClient != null) {
      this.disconnectedClient.destroy();
    }
  }
}
