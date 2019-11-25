/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapters.plc4x.s7;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.connect.adapter.util.PollingSettings;
import org.streampipes.connect.adapters.PullAdapter;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.utils.Datatypes;

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
    public static final String ID = "http://streampipes.org/adapter/specific/plc4xs7";

    /**
     * Keys of user configuration parameters
     */
    private static final String PLC_IP = "PLC_IP";
    private static final String PLC_NODE_NAME = "PLC_NODE_NAME";
    private static final String PLC_NODE_TYPE = "PLC_NODE_TYPE";

    /**
     * Values of user configuration parameters
     */
    private String ip;
    private String nodeName;
    private String nodeType;

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
                .requiredTextParameter(Labels.from(PLC_NODE_NAME, "Node Name", "temperature"))
                .requiredTextParameter(Labels.from(PLC_NODE_TYPE, "Node Type", "%Q0.4:BOOL"))
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

        Datatypes datatype = getStreamPipesDataType(this.nodeType);

        allProperties.add(
                PrimitivePropertyBuilder
                        .create(datatype, this.nodeName)
                        .label(this.nodeName)
                        .description("")
                        .build());

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
        builder.addItem(this.nodeName, this.nodeType);
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
        for (String fieldName : response.getFieldNames()) {
            if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                event.put(fieldName, response.getObject(fieldName));
            }

            else {
                logger.error("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
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
        this.nodeName = extractor.singleValue(PLC_NODE_NAME, String.class);
        this.nodeType = extractor.singleValue(PLC_NODE_TYPE, String.class);
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
