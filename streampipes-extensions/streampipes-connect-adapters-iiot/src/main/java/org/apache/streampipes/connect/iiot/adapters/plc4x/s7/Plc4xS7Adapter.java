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

package org.apache.streampipes.connect.iiot.adapters.plc4x.s7;


import org.apache.streampipes.connect.iiot.adapters.PullAdapter;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.guess.GuessTypeInfo;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
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
import org.apache.plc4x.java.utils.connectionpool.PooledPlcDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Plc4xS7Adapter extends PullAdapter implements PlcReadResponseHandler {

  /**
   * A unique id to identify the Plc4xS7Adapter
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";

  private static final Logger LOG = LoggerFactory.getLogger(Plc4xS7Adapter.class);

  /**
   * Keys of user configuration parameters
   */
  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";

  /**
   * Values of user configuration parameters
   */
  private String ip;
  private int pollingInterval;
  private List<Map<String, String>> nodes;

  /**
   * Connection to the PLC
   */
//    private PlcConnection plcConnection;
  private PlcDriverManager driverManager;

  /**
   * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
   */
  public Plc4xS7Adapter() {
  }

  public Plc4xS7Adapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  /**
   * Describe the adapter adapter and define what user inputs are required.
   * Currently, users can just select one node, this will be extended in the future
   *
   * @return
   */
  @Override
  public SpecificAdapterStreamDescription declareModel() {

    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(PLC_IP))
        .requiredIntegerParameter(Labels.withId(PLC_POLLING_INTERVAL), 1000)
        .requiredCollection(Labels.withId(PLC_NODES),
            StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
            StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
            StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
                Options.from("Bool", "Byte", "Int", "Word", "Real", "Char", "String", "Date", "Time of day",
                    "Date and Time")))
        .build();
    description.setAppId(ID);

    return description;
  }

  /**
   * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {

    // Extract user input
    try {
      getConfigurations(adapterDescription);

      if (this.pollingInterval < 10) {
        throw new AdapterException("Polling interval must be higher than 10. Current value: " + this.pollingInterval);
      }

      GuessSchema guessSchema = new GuessSchema();

      EventSchema eventSchema = new EventSchema();
      List<EventProperty> allProperties = new ArrayList<>();

      for (Map<String, String> node : this.nodes) {
        Datatypes datatype = getStreamPipesDataType(node.get(PLC_NODE_TYPE).toUpperCase().replaceAll(" ", "_"));

        allProperties.add(
            PrimitivePropertyBuilder
                .create(datatype, node.get(PLC_NODE_RUNTIME_NAME))
                .label(node.get(PLC_NODE_RUNTIME_NAME))
                .description("")
                .build());
      }

      this.before();
      var event = readPlcDataSynchronized();
      var preview = event
          .entrySet()
          .stream()
          .collect(Collectors.toMap(Map.Entry::getKey, e ->
              new GuessTypeInfo(e.getValue().getClass().getCanonicalName(), e.getValue())));

      eventSchema.setEventProperties(allProperties);
      guessSchema.setEventSchema(eventSchema);
      guessSchema.setEventPreview(List.of(preview));

      return guessSchema;
    } catch (Exception e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }

  /**
   * This method is executed when the adapter is started. A connection to the PLC is initialized
   *
   * @throws AdapterException
   */
  @Override
  protected void before() throws AdapterException {
    // Extract user input
    getConfigurations(adapterDescription);

    this.driverManager = new PooledPlcDriverManager();
    try (PlcConnection plcConnection = this.driverManager.getConnection("s7://" + this.ip)) {
      if (!plcConnection.getMetadata().canRead()) {
        this.LOG.error("The S7 on IP: " + this.ip + " does not support reading data");
      }
    } catch (PlcConnectionException e) {
      this.LOG.error("Could not establish connection to S7 with ip " + this.ip, e);
    } catch (Exception e) {
      this.LOG.error("Could not close connection to S7 with ip " + this.ip, e);
    }
  }


  /**
   * pullData is called iteratively according to the polling interval defined in getPollInterval.
   */
  @Override
  protected void pullData() {
    // Create PLC read request
    try (PlcConnection plcConnection = this.driverManager.getConnection("s7://" + this.ip)) {
      readPlcData(plcConnection, this);
    } catch (Exception e) {
      LOG.error("Error while reading from PLC with IP {} ", this.ip, e);
    }
  }

  private PlcReadRequest makeReadRequest(PlcConnection plcConnection) throws PlcConnectionException {
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    for (Map<String, String> node : this.nodes) {
      builder.addItem(node.get(PLC_NODE_NAME),
          node.get(PLC_NODE_NAME) + ":" + node.get(PLC_NODE_TYPE).toUpperCase().replaceAll(" ", "_"));
    }
    return builder.build();
  }

  private void readPlcData(PlcConnection plcConnection, PlcReadResponseHandler handler) throws PlcConnectionException {
    var readRequest = makeReadRequest(plcConnection);
    // Execute the request
    CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
    asyncResponse.whenComplete(handler::onReadResult);
  }

  private Map<String, Object> readPlcDataSynchronized() throws Exception {
    try (PlcConnection plcConnection = this.driverManager.getConnection("s7://" + this.ip)) {
      var readRequest = makeReadRequest(plcConnection);
      // Execute the request
      var readResponse = readRequest.execute().get(5000, TimeUnit.MILLISECONDS);
      return makeEvent(readResponse);
    }
  }

  /**
   * Define the polling interval of this adapter. Default is to poll every second
   *
   * @return
   */
  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, this.pollingInterval);
  }

  /**
   * Required by StreamPipes return a new adapter instance by calling the constructor with
   * SpecificAdapterStreamDescription
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new Plc4xS7Adapter(adapterDescription);
  }


  /**
   * Required by StreamPipes. Return the id of the adapter
   *
   * @return
   */
  @Override
  public String getId() {
    return ID;
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
   *
   * @param adapterDescription
   */
  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

    this.ip = extractor.singleValueParameter(PLC_IP, String.class);
    this.pollingInterval = extractor.singleValueParameter(PLC_POLLING_INTERVAL, Integer.class);

    this.nodes = new ArrayList<>();
    CollectionStaticProperty sp = (CollectionStaticProperty) extractor.getStaticPropertyByName(PLC_NODES);

    for (StaticProperty member : sp.getMembers()) {
      StaticPropertyExtractor memberExtractor =
          StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());
      Map<String, String> map = new HashMap<>();
      map.put(PLC_NODE_RUNTIME_NAME, memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME));
      map.put(PLC_NODE_NAME, memberExtractor.textParameter(PLC_NODE_NAME));
      map.put(PLC_NODE_TYPE, memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class));
      this.nodes.add(map);
    }
  }

  /**
   * Transforms PLC4X data types to datatypes supported in StreamPipes
   *
   * @param plcType
   * @return
   */
  private Datatypes getStreamPipesDataType(String plcType) throws AdapterException {

    String type = plcType.substring(plcType.lastIndexOf(":") + 1);

    switch (type) {
      case "BOOL":
        return Datatypes.Boolean;
      case "BYTE":
      case "REAL":
        return Datatypes.Float;
      case "INT":
        return Datatypes.Integer;
      case "WORD":
      case "TIME_OF_DAY":
      case "DATE":
      case "DATE_AND_TIME":
      case "STRING":
      case "CHAR":
        return Datatypes.String;
      default:
        throw new AdapterException("Datatype " + plcType + " is not supported");
    }
  }

  @Override
  public void onReadResult(PlcReadResponse response, Throwable throwable) {
    if (throwable != null) {
      throwable.printStackTrace();
      this.LOG.error(throwable.getMessage());
    } else {
      var event = makeEvent(response);
      // publish the final event
      adapterPipeline.process(event);
    }
  }

  private Map<String, Object> makeEvent(PlcReadResponse response) {
    Map<String, Object> event = new HashMap<>();
    for (Map<String, String> node : this.nodes) {
      if (response.getResponseCode(node.get(PLC_NODE_NAME)) == PlcResponseCode.OK) {
        event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getObject(node.get(PLC_NODE_NAME)));
      } else {
        this.LOG.error("Error[" + node.get(PLC_NODE_NAME) + "]: "
            + response.getResponseCode(node.get(PLC_NODE_NAME)).name());
      }
    }
    return event;
  }
}
