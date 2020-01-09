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

package org.apache.streampipes.connect.adapters.plc4x.s7;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.connect.adapter.util.PollingSettings;
import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Plc4xS7Adapter extends PullAdapter {

    /**
     * A unique id to identify the Plc4xS7Adapter
      */
    public static final String ID = "org.apache.streampipes.connect.adapters.plc4x.s7";

    /**
     * Keys of user configuration parameters
     */
    private static final String PLC_IP = "PLC_IP";
    private static final String PLC_NODES = "PLC_NODES";
    private static final String PLC_NODE_NAME = "PLC_NODE_NAME";
    private static final String PLC_NODE_RUNTIME_NAME = "PLC_NODE_RUNTIME_NAME";
    private static final String PLC_NODE_TYPE = "PLC_NODE_TYPE";

    /**
     * Values of user configuration parameters
     */
    private String ip;
    private List<Map<String, String>> nodes;

    /**
     * Connection to the PLC
     */
    private PlcConnection plcConnection;

    /**
     * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
     */
    public Plc4xS7Adapter() {
    }

    public Plc4xS7Adapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);
    }


    /**
     * Describe the adapter adapter and define what user inputs are required. Currently users can just select one node, this will be extended in the future
     * @return
     */
    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID, "PLC4X S7", "Connect directly to your PLC")
                .iconUrl("plc4x.png")
                .category(AdapterType.Manufacturing)
                .requiredTextParameter(Labels.from(PLC_IP, "PLC Address", "Example: 192.168.34.56"))
                .requiredCollection(Labels.from(PLC_NODES, "Nodes", "The PLC Nodes"),
                    StaticProperties.stringFreeTextProperty(Labels.from(PLC_NODE_RUNTIME_NAME, "Runtime Name", "example: temperatur")),
                    StaticProperties.stringFreeTextProperty(Labels.from(PLC_NODE_NAME, "Node Name", "example: %Q0.4")),
                    StaticProperties.singleValueSelection(Labels.from(PLC_NODE_TYPE, "Data Type", "example: bool"),
                            Options.from("Bool",  "Byte", "Int", "Word", "Real"))

                )
                .build();
        description.setAppId(ID);


        return description;
    }

    /**
     * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
     * @param adapterDescription
     * @return
     * @throws AdapterException
     */
    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
        // TODO add a validation to check if the user input is available in the PLC

        // Extract user input
        getConfigurations(adapterDescription);

        GuessSchema guessSchema = new GuessSchema();

        EventSchema eventSchema = new EventSchema();
        List<EventProperty> allProperties = new ArrayList<>();

        for (Map<String, String> node : this.nodes) {
            Datatypes datatype = getStreamPipesDataType(node.get(PLC_NODE_TYPE).toUpperCase());

            allProperties.add(
                    PrimitivePropertyBuilder
                            .create(datatype, node.get(PLC_NODE_RUNTIME_NAME))
                            .label(node.get(PLC_NODE_RUNTIME_NAME))
                            .description("")
                            .build());
        }

        eventSchema.setEventProperties(allProperties);
        guessSchema.setEventSchema(eventSchema);
        guessSchema.setPropertyProbabilityList(new ArrayList<>());
        return guessSchema;
    }

    /**
     * This method is executed when the adapter is started. A connection to the PLC is initialized
     * @throws AdapterException
     */
    @Override
    protected void before() throws AdapterException {
        // Extract user input
        getConfigurations(adapterDescription);

        try {
            this.plcConnection= new PlcDriverManager().getConnection("s7://" + this.ip + "/1/1");

            if (!this.plcConnection.getMetadata().canRead()) {
                throw new AdapterException("The S7 on IP: " + this.ip + " does not support reading data");
            }

        } catch (PlcConnectionException e) {
            throw new AdapterException("Could not establish connection to S7 with ip " + this.ip);
        }
    }


    /**
     * pullData is called iteratively according to the polling interval defined in getPollInterval.
     */
    @Override
    protected void pullData() {

        // Create PLC read request
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
        for (Map<String, String> node : this.nodes) {
            builder.addItem(node.get(PLC_NODE_NAME), node.get(PLC_NODE_TYPE));
        }
        PlcReadRequest readRequest = builder.build();

        // Execute the request
        PlcReadResponse response = null;
        try {
            response = readRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Create an event containing the value of the PLC
        Map<String, Object> event = new HashMap<>();
        for (Map<String, String> node : this.nodes) {
            if(response.getResponseCode(node.get(PLC_NODE_NAME)) == PlcResponseCode.OK) {
                event.put(node.get(PLC_NODE_RUNTIME_NAME), response.getObject(node.get(PLC_NODE_NAME)));
            }

            else {
                logger.error("Error[" + node.get(PLC_NODE_NAME) + "]: " +
                        response.getResponseCode(node.get(PLC_NODE_NAME)).name());
            }
        }

        // publish the final event
        adapterPipeline.process(event);
    }


    /**
     * Define the polling interval of this adapter. Default is to poll every second
     * @return
     */
    @Override
    protected PollingSettings getPollingInterval() {
        return PollingSettings.from(TimeUnit.SECONDS, 1);
    }

    /**
     * Required by StreamPipes return a new adapter instance by calling the constructor with SpecificAdapterStreamDescription
     * @param adapterDescription
     * @return
     */
    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new Plc4xS7Adapter(adapterDescription);
    }


    /**
     * Required by StreamPipes. Return the id of the adapter
     * @return
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
     * @param adapterDescription
     */
    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

        this.ip = extractor.singleValue(PLC_IP, String.class);


        this.nodes = new ArrayList<>();
        List<ParameterExtractor> collectionExtractor = extractor.collectionGroup(PLC_NODES);
        for (ParameterExtractor rowExtractor : collectionExtractor) {
            Map map = new HashMap();
            map.put(PLC_NODE_RUNTIME_NAME, rowExtractor.singleValue(PLC_NODE_RUNTIME_NAME, String.class));
            map.put(PLC_NODE_NAME, rowExtractor.singleValue(PLC_NODE_NAME, String.class));
            map.put(PLC_NODE_TYPE, rowExtractor.selectedSingleValueOption(PLC_NODE_TYPE));
            this.nodes.add(map);
        }

    }

    /**
     * Transforms PLC4X data types to datatypes supported in StreamPipes
     * @param plcType
     * @return
     * @throws AdapterException
     */
    private Datatypes getStreamPipesDataType(String plcType) throws AdapterException {

        String type = plcType.substring(plcType.lastIndexOf(":")+1);

        switch (type) {
            case "BOOL":
                return Datatypes.Boolean;
            case "BYTE":
                return Datatypes.Float;
            case "INT":
                return Datatypes.Integer;
            case "WORD":
                return Datatypes.String;
            case "REAL":
                return Datatypes.Float;
            default:
                throw new AdapterException("Datatype " + plcType + " is not supported");
        }
    }

}
