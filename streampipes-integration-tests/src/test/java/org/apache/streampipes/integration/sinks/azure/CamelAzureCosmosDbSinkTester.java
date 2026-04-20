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

package org.apache.streampipes.integration.sinks.azure;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.connectors.camel.kamelet.config.KameletSinkStaticPropertyProvider;
import org.apache.streampipes.integration.containers.AzureCosmosDbEmulatorContainer;
import org.apache.streampipes.integration.sinks.SinkTesterBase;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CamelAzureCosmosDbSinkTester extends SinkTesterBase {

  private static final String TEMPLATE_NAME = "azure-cosmosdb-sink";
  private static final int COSMOS_STARTUP_ATTEMPTS = 12;
  private static final long COSMOS_STARTUP_RETRY_DELAY_MS = 5000;

  private final String databaseName = "streampipes-test-" + UUID.randomUUID();
  private final String containerName = "items";
  private final List<Map<String, Object>> expectedDocuments = List.of(
      Map.of("id", "item-1", "value", 42),
      Map.of("id", "item-2", "value", 84)
  );

  private AzureCosmosDbEmulatorContainer emulatorContainer;
  private CosmosDbEmulatorTrustStore trustStore;
  private CosmosClient cosmosClient;
  private CosmosContainer verificationContainer;
  private EventSchema eventSchema;
  private String emulatorKey;

  @Override
  protected void startSinkService() throws Exception {
    emulatorContainer = new AzureCosmosDbEmulatorContainer();
    emulatorContainer.start();

    trustStore = new CosmosDbEmulatorTrustStore(emulatorContainer.getCertificateEndpoint());
    trustStore.install();
    emulatorKey = resolveEmulatorKey();

    cosmosClient = awaitCosmosClientReady();

    cosmosClient.createDatabaseIfNotExists(databaseName);
    CosmosDatabase database = cosmosClient.getDatabase(databaseName);
    database.createContainerIfNotExists(new CosmosContainerProperties(containerName, "/id"));

    awaitContainerReady();
    verificationContainer = cosmosClient.getDatabase(databaseName).getContainer(containerName);
  }

  @Override
  protected IStreamPipesDataSink createSink() {
    var configuration = new TestCamelAzureConnectorsModuleExport()
        .pipelineElements()
        .stream()
        .filter(pipelineElement -> pipelineElement.declareConfig()
            .getDescription()
            .getAppId()
            .endsWith("." + TEMPLATE_NAME))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find Azure Cosmos DB sink"))
        .declareConfig();

    return (IStreamPipesDataSink) configuration.getSupplier().get();
  }

  @Override
  protected List<SpDataStream> createInputStreams() {
    this.eventSchema = new EventSchema();
    this.eventSchema.addEventProperty(
        new EventPropertyPrimitive("http://www.w3.org/2001/XMLSchema#string", "id", null, null)
    );
    this.eventSchema.addEventProperty(
        new EventPropertyPrimitive("http://www.w3.org/2001/XMLSchema#integer", "value", null, null)
    );

    SpDataStream inputStream = new SpDataStream();
    inputStream.setEventSchema(eventSchema);
    return List.of(inputStream);
  }

  @Override
  protected List<StaticProperty> configureStaticProperties(IStreamPipesDataSink sink,
                                                           List<SpDataStream> inputStreams) throws Exception {
    List<StaticProperty> staticProperties = configurator().prepareStaticProperties(sink, inputStreams);

    RuntimeResolvableGroupStaticProperty parameterGroup = configurator().requireProperty(
        staticProperties,
        KameletSinkStaticPropertyProvider.KAMELET_PARAMETERS_GROUP_KEY,
        RuntimeResolvableGroupStaticProperty.class
    );
    setParameterValue(parameterGroup, "databasename", databaseName);
    setParameterValue(parameterGroup, "containername", containerName);
    setParameterValue(parameterGroup, "databaseendpoint", emulatorContainer.getEmulatorEndpoint());
    setParameterValue(parameterGroup, "accountkey", emulatorKey);

    StaticPropertyGroup messageMappingGroup = configurator().requireProperty(
        staticProperties,
        KameletSinkStaticPropertyProvider.MESSAGE_MAPPING_GROUP_KEY,
        StaticPropertyGroup.class
    );
    StaticPropertyAlternatives payloadAlternatives = configurator().requireProperty(
        messageMappingGroup,
        KameletSinkStaticPropertyProvider.PAYLOAD_ALTERNATIVES_KEY,
        StaticPropertyAlternatives.class
    );
    configurator().selectAlternative(payloadAlternatives, KameletSinkStaticPropertyProvider.PAYLOAD_EVENT_JSON_KEY);

    CollectionStaticProperty headerMappings = configurator().requireProperty(
        messageMappingGroup,
        KameletSinkStaticPropertyProvider.HEADER_MAPPINGS_KEY,
        CollectionStaticProperty.class
    );
    StaticPropertyGroup headerMapping = configurator().addCollectionMember(headerMappings);
    configurator().requireProperty(
        headerMapping,
        KameletSinkStaticPropertyProvider.HEADER_NAME_KEY,
        FreeTextStaticProperty.class
    ).setValue("itemPartitionKey");
    configurator().requireProperty(
        headerMapping,
        KameletSinkStaticPropertyProvider.HEADER_FIELD_MAPPING_KEY,
        MappingPropertyUnary.class
    ).setSelectedProperty("s0::id");

    return staticProperties;
  }

  @Override
  protected List<Event> createEvents() {
    SourceInfo sourceInfo = new SourceInfo("source-0", "s0");
    SchemaInfo schemaInfo = new SchemaInfo(eventSchema, List.of());

    return expectedDocuments.stream()
        .map(document -> EventFactory.fromMap(document, sourceInfo, schemaInfo))
        .toList();
  }

  @Override
  protected void validate(List<Event> events) throws Exception {
    for (Map<String, Object> expectedDocument : expectedDocuments) {
      JsonNode item = awaitItem(expectedDocument.get("id").toString());
      assertNotNull(item);
      assertEquals(expectedDocument.get("id"), item.get("id").asText());
      assertEquals(((Number) expectedDocument.get("value")).intValue(), item.get("value").asInt());
    }
  }

  @Override
  protected void stopSinkService() throws Exception {
    Exception failure = null;

    if (cosmosClient != null) {
      try {
        cosmosClient.close();
      } catch (Exception e) {
        failure = e;
      } finally {
        cosmosClient = null;
      }
    }

    if (trustStore != null) {
      try {
        trustStore.close();
      } catch (Exception e) {
        if (failure != null) {
          failure.addSuppressed(e);
        } else {
          failure = e;
        }
      } finally {
        trustStore = null;
      }
    }

    if (emulatorContainer != null) {
      try {
        emulatorContainer.stop();
      } catch (Exception e) {
        if (failure != null) {
          failure.addSuppressed(e);
        } else {
          failure = e;
        }
      } finally {
        emulatorContainer = null;
      }
    }

    if (failure != null) {
      throw failure;
    }
  }

  private void setParameterValue(RuntimeResolvableGroupStaticProperty parameterGroup,
                                 String suffix,
                                 String value) {
    StaticProperty property = configurator().requirePropertyMatching(
        parameterGroup,
        candidate -> candidate.getInternalName() != null && candidate.getInternalName().endsWith(suffix),
        StaticProperty.class
    );

    if (property instanceof FreeTextStaticProperty freeTextStaticProperty) {
      freeTextStaticProperty.setValue(value);
    } else if (property instanceof SecretStaticProperty secretStaticProperty) {
      secretStaticProperty.setValue(value);
    } else {
      throw new IllegalArgumentException("Unsupported Kamelet parameter property type: " + property.getClass());
    }
  }

  private JsonNode awaitItem(String id) throws Exception {
    Exception lastException = null;

    for (int attempt = 0; attempt < 20; attempt++) {
      try {
        return verificationContainer.readItem(id, new PartitionKey(id), JsonNode.class).getItem();
      } catch (Exception e) {
        lastException = e;
        Thread.sleep(1000);
      }
    }

    throw lastException;
  }

  private void awaitContainerReady() throws Exception {
    Exception lastException = null;

    for (int attempt = 0; attempt < COSMOS_STARTUP_ATTEMPTS; attempt++) {
      try (CosmosClient probeClient = createCosmosClient()) {
        CosmosDatabase probeDatabase = probeClient.getDatabase(databaseName);
        CosmosContainer probeContainer = probeDatabase.getContainer(containerName);
        probeDatabase.read();
        probeContainer.read();
        return;
      } catch (Exception e) {
        lastException = e;
        Thread.sleep(COSMOS_STARTUP_RETRY_DELAY_MS);
      }
    }

    throw lastException;
  }

  private CosmosClient awaitCosmosClientReady() throws Exception {
    Exception lastException = null;

    for (int attempt = 0; attempt < COSMOS_STARTUP_ATTEMPTS; attempt++) {
      try {
        return createCosmosClient();
      } catch (Exception e) {
        lastException = e;
        Thread.sleep(COSMOS_STARTUP_RETRY_DELAY_MS);
      }
    }

    throw lastException;
  }

  private CosmosClient createCosmosClient() {
    return new CosmosClientBuilder()
        .endpoint(emulatorContainer.getEmulatorEndpoint())
        .key(emulatorKey)
        .gatewayMode()
        .contentResponseOnWriteEnabled(true)
        .buildClient();
  }

  private String resolveEmulatorKey() throws Exception {
    String configuredKey = emulatorContainer.getConfiguredEmulatorKey();
    if (configuredKey != null) {
      return configuredKey;
    }

    throw new IllegalStateException(
        "Could not determine the Cosmos emulator key. "
            + "Set -Dstreampipes.it.cosmosdb.key or STREAMPIPES_IT_COSMOSDB_KEY."
    );
  }
}
