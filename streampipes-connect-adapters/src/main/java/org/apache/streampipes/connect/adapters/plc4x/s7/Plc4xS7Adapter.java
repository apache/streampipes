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

import com.poiji.bind.Poiji;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.util.PollingSettings;
import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.io.File;

public class Plc4xS7Adapter extends PullAdapter {

    /**
     * A unique id to identify the Plc4xS7Adapter
     */
    public static final String ID = "org.apache.streampipes.connect.adapters.plc4x.s7";

    /**
     * Keys of user configuration parameters
     */
    private static final String PLC_IP = "plc_ip";
    private static final String PLC_NODES = "plc_nodes";
    private static final String PLC_NODE_NAME = "plc_node_name";
    private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
    private static final String PLC_NODE_TYPE = "plc_node_type";
    private static final String PLC_NODES_CSV_FILE = "plc_nodes_csv_file";
    private static final String PLC_NODES_EXCEL_FILE = "plc_nodes_excel_file";
    private static final String CONFIGURE = "configure";
    private static final String MANUALLY = "manually";
    private static final String CSV_IMPORT = "csv_import";
    private static final String EXCEL_IMPORT = "excel_import";

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

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .category(AdapterType.Manufacturing)
                .requiredTextParameter(Labels.withId(PLC_IP))
                .requiredAlternatives(Labels.withId(CONFIGURE),
                        Alternatives.from(Labels.withId(MANUALLY),
                                StaticProperties.collection(Labels.withId(PLC_NODES),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
                                        StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
                                                Options.from("Bool",  "Byte", "Int", "Word", "Real")))),
                        Alternatives.from(Labels.withId(CSV_IMPORT),
                                StaticProperties.fileProperty(Labels.withId(PLC_NODES_CSV_FILE))),
                        Alternatives.from(Labels.withId(EXCEL_IMPORT),
                                StaticProperties.fileProperty(Labels.withId(PLC_NODES_EXCEL_FILE))))
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
//            this.plcConnection= new PlcDriverManager().getConnection("s7://" + this.ip + "/1/1");
            this.plcConnection= new PlcDriverManager().getConnection("s7://" + this.ip);

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
            builder.addItem(node.get(PLC_NODE_NAME), node.get(PLC_NODE_NAME) + ":" + node.get(PLC_NODE_TYPE).toUpperCase());
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
    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

        this.ip = extractor.singleValueParameter(PLC_IP, String.class);

        String selectedAlternative = extractor.selectedAlternativeInternalId(CONFIGURE);
        if (selectedAlternative.equals(CSV_IMPORT)) {
            // CSV file
            FileStaticProperty sp = (FileStaticProperty) extractor.getStaticPropertyByName(PLC_NODES_CSV_FILE);
            this.nodes = new ArrayList<>();
            try {
                List<S7ConfigFile> configFiles = this.getCsvConfig(sp.getLocationPath());
                for (S7ConfigFile entry : configFiles) {
                    Map map = new HashMap();
                    map.put(PLC_NODE_RUNTIME_NAME, entry.getName());
                    map.put(PLC_NODE_NAME, entry.getLogicalAddress());
                    map.put(PLC_NODE_TYPE, entry.getDataType());
                    this.nodes.add(map);
                }
            } catch (FileNotFoundException e) {
                throw new AdapterException("Could not read imported file");
            }

        } else if (selectedAlternative.equals(EXCEL_IMPORT)) {
            // Excel file
            FileStaticProperty sp = (FileStaticProperty) extractor.getStaticPropertyByName(PLC_NODES_EXCEL_FILE);
            this.nodes = new ArrayList<>();
            try {
                List<S7ConfigFile> configFiles = this.getExcelConfig(sp.getLocationPath());
                for (S7ConfigFile entry : configFiles) {
                    Map map = new HashMap();
                    map.put(PLC_NODE_RUNTIME_NAME, entry.getName());
                    map.put(PLC_NODE_NAME, entry.getLogicalAddress());
                    map.put(PLC_NODE_TYPE, entry.getDataType());
                    this.nodes.add(map);
                }
            } catch (FileNotFoundException e) {
                throw new AdapterException("Could not read imported file");
            }

        } else {
            // Manually
            this.nodes = new ArrayList<>();
            CollectionStaticProperty sp = (CollectionStaticProperty) extractor.getStaticPropertyByName(PLC_NODES);

            for (StaticProperty member : sp.getMembers()) {
                StaticPropertyExtractor memberExtractor =
                        StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());
                Map map = new HashMap();
                map.put(PLC_NODE_RUNTIME_NAME, memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME));
                map.put(PLC_NODE_NAME, memberExtractor.textParameter(PLC_NODE_NAME));
                map.put(PLC_NODE_TYPE, memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class));
                this.nodes.add(map);
            }
        }
    }


    private List<S7ConfigFile> getExcelConfig(String path) throws FileNotFoundException {
        List<S7ConfigFile> configFiles = Poiji.fromExcel(new File(path), S7ConfigFile.class);
        return configFiles;
    }

    private List<S7ConfigFile> getCsvConfig(String path) throws FileNotFoundException {

        FileReader fr = new FileReader(path);
        CSVReader reader = new CSVReader(fr, ';');

        Map<String, String> mapping = new
                HashMap<String, String>();
        mapping.put("Name", "name");
        mapping.put("Logical Address", "logicalAddress");
        mapping.put("Data Type", "dataType");

        HeaderColumnNameTranslateMappingStrategy strategy =
                new HeaderColumnNameTranslateMappingStrategy();
        strategy.setType(S7ConfigFile.class);
        strategy.setColumnMapping(mapping);

        CsvToBean<S7ConfigFile> csvToBean = new CsvToBeanBuilder(reader)
                .withType(S7ConfigFile.class)
                .withMappingStrategy(strategy)
                .build();

        List<S7ConfigFile> configFiles = csvToBean.parse();
        return configFiles;
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
