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

package org.apache.streampipes.connect.iiot.adapters.plc4x.modbus;


import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Plc4xModbusAdapter implements StreamPipesAdapter, IPullAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(Plc4xModbusAdapter.class);
  /**
   * A unique id to identify the Plc4xModbusAdapter
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.modbus";

  /**
   * Keys of user configuration parameters
   */
  private static final String PLC_IP = "plc_ip";
  private static final String PLC_PORT = "plc_port";

  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_ID = "plc_node_id";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_ADDRESS = "plc_node_address";
  private static final String PLC_NODE_TYPE = "plc_node_type";
  private static final String CONFIGURE = "configure";

  /**
   * Values of user configuration parameters
   */
  private String ip;
  private int port;
  private int slaveID;
  private List<Map<String, String>> nodes;

  private IEventCollector collector;
  private PullAdapterScheduler pullAdapterScheduler;

  /**
   * Connection to the PLC
   */
  private PlcConnection plcConnection;

  /**
   * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
   */
  public Plc4xModbusAdapter() {
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variables
   *
   * @param extractor static property extractor
   * @throws AdapterException
   */
  private void getConfigurations(IStaticPropertyExtractor extractor) throws AdapterException {

    this.ip = extractor.singleValueParameter(PLC_IP, String.class);
    this.port = extractor.singleValueParameter(PLC_PORT, Integer.class);
    this.slaveID = extractor.singleValueParameter(PLC_NODE_ID, Integer.class);

    this.nodes = new ArrayList<>();
    CollectionStaticProperty sp = (CollectionStaticProperty) extractor.getStaticPropertyByName(PLC_NODES);
    Set<Integer> ids = new HashSet<>();
    Set<String> names = new HashSet<>();
    for (StaticProperty member : sp.getMembers()) {
      StaticPropertyExtractor memberExtractor =
          StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());

      // ensure that NODE_ADDRESS and NODE_RUNTIME_TIME appear only once to prevent duplicates
      if (!ids.add(memberExtractor.singleValueParameter(PLC_NODE_ADDRESS, Integer.class))
          || !names.add(memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME))) {

        throw new AdapterException("NodeID or RuntimeName is specified twice." + "Please prevent duplicate names.");
      }

      Map map = new HashMap();
      map.put(PLC_NODE_RUNTIME_NAME, memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME));
      map.put(PLC_NODE_ADDRESS, memberExtractor.singleValueParameter(PLC_NODE_ADDRESS, Integer.class));
      map.put(PLC_NODE_TYPE, memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class));

      this.nodes.add(map);
    }

  }


  /**
   * Transforms chosen data type to StreamPipes supported data type
   *
   * @param plcType
   * @return
   */
  private Datatypes getStreamPipesDataType(String plcType) throws AdapterException {

    String type = plcType.substring(plcType.lastIndexOf(":") + 1);

    switch (type) {
      case "DISCRETEINPUT":
      case "COIL":
        return Datatypes.Boolean;
      case "INPUTREGISTER":
      case "HOLDINGREGISTER":
        return Datatypes.Integer;
      default:
        throw new AdapterException("Datatype " + plcType + " is not supported");
    }
  }

  /**
   * This method is executed when the adapter is started. A connection to the PLC is initialized
   *
   * @throws AdapterException
   */
  private void before(IStaticPropertyExtractor extractor) throws AdapterException {

    // Extract user input
    getConfigurations(extractor);

    try {
      this.plcConnection = new PlcDriverManager().getConnection(
          "modbus-tcp:tcp://" + this.ip + ":" + this.port + "?unit-identifier=" + this.slaveID);

      if (!this.plcConnection.getMetadata().canRead()) {
        throw new AdapterException("The Modbus device on IP: " + this.ip + " does not support reading data");
      }
    } catch (PlcConnectionException pce) {
      throw new AdapterException("Could not establish a connection to Modbus device on IP: " + this.ip);
    }
  }

  /**
   * is called iteratively according to the polling interval defined in getPollInterval.
   */
  @Override
  public void pullData() {

    // create PLC read request
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    for (Map<String, String> node : this.nodes) {

      switch (node.get(PLC_NODE_TYPE)) {
        case "Coil" ->
            builder.addItem(node.get(PLC_NODE_RUNTIME_NAME), "coil:" + String.valueOf(node.get(PLC_NODE_ADDRESS)));
        case "HoldingRegister" -> builder.addItem(node.get(PLC_NODE_RUNTIME_NAME),
            "holding-register:" + String.valueOf(node.get(PLC_NODE_ADDRESS)));
        case "DiscreteInput" -> builder.addItem(node.get(PLC_NODE_RUNTIME_NAME),
            "discrete-input:" + String.valueOf(node.get(PLC_NODE_ADDRESS)));
        case "InputRegister" -> builder.addItem(node.get(PLC_NODE_RUNTIME_NAME),
            "input-register:" + String.valueOf(node.get(PLC_NODE_ADDRESS)));
      }
    }
    PlcReadRequest readRequest = builder.build();


    // Execute the request
    PlcReadResponse response = null;

    try {
      response = readRequest.execute().get();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    } catch (ExecutionException ee) {
      ee.printStackTrace();
    }

    // Create an event containing the value of the PLC
    Map<String, Object> event = new HashMap<>();
    for (Map<String, String> node : this.nodes) {

      if (response.getResponseCode(node.get(PLC_NODE_RUNTIME_NAME)) == PlcResponseCode.OK) {

        switch (node.get(PLC_NODE_TYPE)) {
          case "Coil":
            event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getBoolean(node.get(PLC_NODE_RUNTIME_NAME)));
            break;
          case "DiscreteInput":
            event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getBoolean(node.get(PLC_NODE_RUNTIME_NAME)));
            break;
          case "InputRegister":
            event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getInteger(node.get(PLC_NODE_RUNTIME_NAME)));
            break;
          case "HoldingRegister":
            event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getInteger(node.get(PLC_NODE_RUNTIME_NAME)));
            break;
        }
      } else {
        LOG.error("Error[" + node.get(PLC_NODE_RUNTIME_NAME) + "]: "
                  + response.getResponseCode(node.get(PLC_NODE_RUNTIME_NAME)));
      }
    }

    // publish the final event
    collector.collect(event);
  }

  /**
   * return polling interval for this adapter, default is set to one second
   *
   * @return
   */
  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, 1);
  }

  /**
   * Describe the adapter adapter and define what user inputs are required. Currently users can just select one node,
   * this will be extended in the future
   *
   * @return description of adapter
   */
  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, Plc4xModbusAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(PLC_IP)).requiredTextParameter(Labels.withId(PLC_PORT))
        .requiredTextParameter(Labels.withId(PLC_NODE_ID)).requiredCollection(Labels.withId(PLC_NODES),
            StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
            StaticProperties.integerFreeTextProperty(Labels.withId(PLC_NODE_ADDRESS)),
            StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
                Options.from("DiscreteInput", "Coil", "InputRegister", "HoldingRegister")))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    before(extractor.getStaticPropertyExtractor());
    this.collector = collector;
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());

  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext)
      throws AdapterException {
    this.pullAdapterScheduler.shutdown();
  }

  /**
   * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
   */
  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    getConfigurations(extractor.getStaticPropertyExtractor());

    GuessSchema guessSchema = new GuessSchema();

    EventSchema eventSchema = new EventSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    for (Map<String, String> node : this.nodes) {
      Datatypes datatype = getStreamPipesDataType(node.get(PLC_NODE_TYPE).toUpperCase());

      allProperties.add(PrimitivePropertyBuilder.create(datatype, node.get(PLC_NODE_RUNTIME_NAME))
          .label(node.get(PLC_NODE_RUNTIME_NAME))
          .description("FieldAddress: " + node.get(PLC_NODE_TYPE) + " " + String.valueOf(node.get(PLC_NODE_ADDRESS)))
          .build());
    }

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);
    return guessSchema;
  }
}
