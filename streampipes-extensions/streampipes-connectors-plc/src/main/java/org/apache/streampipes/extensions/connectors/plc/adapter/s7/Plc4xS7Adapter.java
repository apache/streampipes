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

package org.apache.streampipes.extensions.connectors.plc.adapter.s7;


import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.EventSchemaProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.PlcEventGenerator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.PlcRequestProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.s7.config.ConfigurationParser;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Plc4xS7Adapter implements StreamPipesAdapter, IPullAdapter, PlcReadResponseHandler {

  /**
   * A unique id to identify the Plc4xS7Adapter
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";

  private static final Logger LOG = LoggerFactory.getLogger(Plc4xS7Adapter.class);

  private static final String S7_URL = "s7://";

  /**
   * Keys of user configuration parameters
   */
  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  public static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";

  public static final String PLC_NODE_INPUT_CODE_BLOCK_ALTIVE = "plc_node_input_code_block_altive";
  public static final String PLC_CODE_BLOCK = "plc_code_block";
  public static final String PLC_NODE_INPUT_ALTERNATIVES = "plc_node_input_alternatives";
  public static final String PLC_NODE_INPUT_COLLECTION_ALTERNATIVE = "plc_node_input_collection_alternative";

  public static final String CODE_TEMPLATE = """
      // This code block can be used to manually specify the addresses of the PLC registers.
      // The syntax is based on the PLC4X syntax, see [1].
      // Address Pattern:
      // propertyName=%{Memory-Area}{start-address}:{Data-Type}[{array-size}]

      temperature=%I0.0:INT

      // [1] https://plc4x.apache.org/users/protocols/s7.html
      """;

  /**
   * Values of user configuration parameters
   */
  private String ip;
  private int pollingInterval;
  private Map<String, String> nodes;

  private PlcDriverManager driverManager;

  private PullAdapterScheduler pullAdapterScheduler;

  private IEventCollector collector;

  private PlcEventGenerator eventGenerator;

  private final PlcRequestProvider requestProvider;

  public Plc4xS7Adapter() {
    this.requestProvider = new PlcRequestProvider();
  }

  /**
   * This method is executed when the adapter is started. A connection to the PLC is initialized
   */
  private void before(IStaticPropertyExtractor extractor) {
    // Extract user input
    getConfigurations(extractor);

    this.driverManager = PlcDriverManager.getDefault();
    try (PlcConnection plcConnection = this.driverManager.getConnectionManager().getConnection(S7_URL + this.ip)) {
      if (!plcConnection.getMetadata().isReadSupported()) {
        LOG.error("The S7 on IP: " + this.ip + " does not support reading data");
      }
    } catch (PlcConnectionException e) {
      LOG.error("Could not establish connection to S7 with ip " + this.ip, e);
    } catch (Exception e) {
      LOG.error("Could not close connection to S7 with ip " + this.ip, e);
    }
  }


  /**
   * pullData is called iteratively according to the polling interval defined in getPollInterval.
   */
  @Override
  public void pullData() {
    // Create PLC read request
    try (PlcConnection plcConnection = this.driverManager.getConnectionManager().getConnection(S7_URL + this.ip)) {
      readPlcData(plcConnection, this);
    } catch (Exception e) {
      LOG.error("Error while reading from PLC with IP {} ", this.ip, e);
    }
  }

  private void readPlcData(PlcConnection plcConnection, PlcReadResponseHandler handler) {
    var readRequest = requestProvider.makeReadRequest(plcConnection, this.nodes);
    // Execute the request
    CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
    asyncResponse.whenComplete(handler::onReadResult);
  }

  private Map<String, Object> readPlcDataSynchronized() throws Exception {
    try (PlcConnection plcConnection = this.driverManager.getConnectionManager().getConnection(S7_URL + this.ip)) {
      var readRequest = requestProvider.makeReadRequest(plcConnection, this.nodes);
      // Execute the request
      var readResponse = readRequest.execute().get(5000, TimeUnit.MILLISECONDS);
      return eventGenerator.makeEvent(readResponse);
    }
  }

  /**
   * Define the polling interval of this adapter. Default is to poll every second
   *
   * @return PollingSettings
   */
  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, this.pollingInterval);
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
   *
   * @param extractor StaticPropertyExtractor
   */
  private void getConfigurations(IStaticPropertyExtractor extractor) {

    this.ip = extractor.singleValueParameter(PLC_IP, String.class);
    this.pollingInterval = extractor.singleValueParameter(PLC_POLLING_INTERVAL, Integer.class);

    this.nodes = new HashMap<>();

    var selectedAlternative = extractor.selectedAlternativeInternalId(PLC_NODE_INPUT_ALTERNATIVES);

    if (selectedAlternative.equals(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE)) {
      // Alternative Simple
      var csp = (CollectionStaticProperty) extractor.getStaticPropertyByName(PLC_NODES);
      this.nodes = getNodeInformationFromCollectionStaticProperty(csp);

    } else {
      // Alternative Advanced
      var codePropertyInput = extractor.codeblockValue(PLC_CODE_BLOCK);
      this.nodes = new ConfigurationParser().getNodeInformationFromCodeProperty(codePropertyInput);
    }
    this.eventGenerator = new PlcEventGenerator(this.nodes);
  }


  @Override
  public void onReadResult(PlcReadResponse response, Throwable throwable) {
    if (throwable != null) {
      throwable.printStackTrace();
      LOG.error(throwable.getMessage());
    } else {
      var event = eventGenerator.makeEvent(response);
      // publish the final event
      collector.collect(event);
    }
  }

  /**
   * Describe the adapter and define what user inputs are required.
   * Currently, users can just select one node, this will be extended in the future
   *
   * @return AdapterConfiguration
   */
  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 1, Plc4xS7Adapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(PLC_IP))
        .requiredIntegerParameter(Labels.withId(PLC_POLLING_INTERVAL), 1000)
        .requiredAlternatives(
            Labels.withId(PLC_NODE_INPUT_ALTERNATIVES),
            Alternatives.from(Labels.withId(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE),
                StaticProperties.collection(Labels.withId(PLC_NODES),
                    StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
                    StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
                    StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
                        Options.from("Bool", "Byte", "Int", "Word", "Real", "Char", "String", "Date", "Time of day",
                            "Date and Time"))),
                true),
            Alternatives.from(Labels.withId(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE),
                StaticProperties.codeStaticProperty(Labels.withId(PLC_CODE_BLOCK), CodeLanguage.None, CODE_TEMPLATE)))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.before(extractor.getStaticPropertyExtractor());
    this.collector = collector;
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    // Extract user input
    try {
      getConfigurations(extractor.getStaticPropertyExtractor());

      if (this.pollingInterval < 10) {
        throw new AdapterException("Polling interval must be higher than 10. Current value: " + this.pollingInterval);
      }

      GuessSchemaBuilder builder = GuessSchemaBuilder.create();
      List<EventProperty> allProperties = new EventSchemaProvider().makeSchema(this.nodes);

      this.before(extractor.getStaticPropertyExtractor());
      var event = readPlcDataSynchronized();

      builder.properties(allProperties);
      builder.preview(event);

      return builder.build();
    } catch (Exception e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }


  private Map<String, String> getNodeInformationFromCollectionStaticProperty(CollectionStaticProperty csp) {
    var result = new HashMap<String, String>();

    for (StaticProperty member : csp.getMembers()) {
      StaticPropertyExtractor memberExtractor =
          StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());

      result.put(
          memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME),
          getNodeAddress(memberExtractor));

    }

    return result;
  }

  /**
   * Takes the members of the static property collection from the UI and creates the PLC4X node address
   *
   * @param memberExtractor member of the static property node collection
   * @return string in the format of PLC4X node address
   */
  private String getNodeAddress(StaticPropertyExtractor memberExtractor) {
    return "%s:%s".formatted(
        memberExtractor.textParameter(PLC_NODE_NAME),
        memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class)
            .toUpperCase()
            .replaceAll(" ", "_"));
  }

}
