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
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.EventSchemaProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.ContinuousPlcRequestReader;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.OneTimePlcRequestReader;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.PlcRequestProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;
import org.apache.streampipes.extensions.connectors.plc.adapter.s7.config.ConfigurationParser;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.plc4x.java.api.PlcConnectionManager;

public class Plc4xS7Adapter implements StreamPipesAdapter {

  /**
   * A unique id to identify the Plc4xS7Adapter
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";

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

  private final PlcConnectionManager connectionManager;
  private final PlcRequestProvider requestProvider;

  private PullAdapterScheduler pullAdapterScheduler;

  public Plc4xS7Adapter(PlcConnectionManager connectionManager) {
    this.requestProvider = new PlcRequestProvider();
    this.connectionManager = connectionManager;
  }

  /**
   * Describe the adapter and define what user inputs are required. Currently, users can just select one node, this will
   * be extended in the future
   *
   * @return AdapterConfiguration
   */
  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 1, () -> new Plc4xS7Adapter(connectionManager))
            .withLocales(Locales.EN).withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withCategory(AdapterType.Manufacturing).requiredTextParameter(Labels.withId(PLC_IP))
            .requiredIntegerParameter(Labels.withId(PLC_POLLING_INTERVAL), 1000)
            .requiredAlternatives(Labels.withId(PLC_NODE_INPUT_ALTERNATIVES),
                    Alternatives.from(Labels.withId(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE),
                            StaticProperties.collection(Labels.withId(PLC_NODES),
                                    StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
                                    StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
                                    StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
                                            Options.from("Bool", "Byte", "Int", "Word", "Real", "Char", "String",
                                                    "Date", "Time of day", "Date and Time"))),
                            true),
                    Alternatives.from(Labels.withId(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE), StaticProperties
                            .codeStaticProperty(Labels.withId(PLC_CODE_BLOCK), CodeLanguage.None, CODE_TEMPLATE)))
            .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
          IAdapterRuntimeContext adapterRuntimeContext) {
    var settings = getConfigurations(extractor.getStaticPropertyExtractor());
    var plcRequestReader = new ContinuousPlcRequestReader(connectionManager, settings, requestProvider, collector);
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(plcRequestReader, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    try {
      var settings = getConfigurations(extractor.getStaticPropertyExtractor());

      if (settings.pollingInterval() < 10) {
        throw new AdapterException(String.format("Polling interval must be higher than 10. Current value: %s",
                settings.pollingInterval()));
      }

      GuessSchemaBuilder builder = GuessSchemaBuilder.create();
      List<EventProperty> allProperties = new EventSchemaProvider().makeSchema(settings.nodes());
      var event = new OneTimePlcRequestReader(connectionManager, settings, requestProvider).readPlcDataSynchronized();

      builder.properties(allProperties);
      builder.preview(event);

      return builder.build();
    } catch (Exception e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
   *
   * @param extractor
   *          StaticPropertyExtractor
   */
  private Plc4xConnectionSettings getConfigurations(IStaticPropertyExtractor extractor) {

    var ip = extractor.singleValueParameter(PLC_IP, String.class);
    var pollingInterval = extractor.singleValueParameter(PLC_POLLING_INTERVAL, Integer.class);

    Map<String, String> nodes;

    var selectedAlternative = extractor.selectedAlternativeInternalId(PLC_NODE_INPUT_ALTERNATIVES);

    if (selectedAlternative.equals(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE)) {
      // Alternative Simple
      var csp = (CollectionStaticProperty) extractor.getStaticPropertyByName(PLC_NODES);
      nodes = getNodeInformationFromCollectionStaticProperty(csp);

    } else {
      // Alternative Advanced
      var codePropertyInput = extractor.codeblockValue(PLC_CODE_BLOCK);
      nodes = new ConfigurationParser().getNodeInformationFromCodeProperty(codePropertyInput);
    }

    return new Plc4xConnectionSettings(S7_URL + ip, pollingInterval, nodes);
  }

  private Map<String, String> getNodeInformationFromCollectionStaticProperty(CollectionStaticProperty csp) {
    var result = new HashMap<String, String>();

    for (StaticProperty member : csp.getMembers()) {
      StaticPropertyExtractor memberExtractor = StaticPropertyExtractor
              .from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());

      result.put(memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME), getNodeAddress(memberExtractor));

    }

    return result;
  }

  /**
   * Takes the members of the static property collection from the UI and creates the PLC4X node address
   *
   * @param memberExtractor
   *          member of the static property node collection
   * @return string in the format of PLC4X node address
   */
  private String getNodeAddress(StaticPropertyExtractor memberExtractor) {
    return "%s:%s".formatted(memberExtractor.textParameter(PLC_NODE_NAME),
            memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class).toUpperCase().replaceAll(" ", "_"));
  }

}
