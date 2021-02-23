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

package org.apache.streampipes.connect.adapters.opcua;

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.net.URI;
import java.util.*;

public class OpcUaAdapter extends SpecificDataStreamAdapter implements ResolvesContainerProvidedOptions {

    public static final String ID = "org.apache.streampipes.connect.adapters.opcua";

    private static final String OPC_HOST_OR_URL = "OPC_HOST_OR_URL";
    private static final String OPC_URL = "OPC_URL";
    private static final String OPC_HOST = "OPC_HOST";
    private static final String OPC_SERVER_URL = "OPC_SERVER_URL";
    private static final String OPC_SERVER_HOST = "OPC_SERVER_HOST";
    private static final String OPC_SERVER_PORT = "OPC_SERVER_PORT";
    private static final String NAMESPACE_INDEX = "NAMESPACE_INDEX";
    private static final String NODE_ID = "NODE_ID";
    private static final String ACCESS_MODE = "ACCESS_MODE";
    private static final String USERNAME_GROUP = "USERNAME_GROUP";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    private static final String AVAILABLE_NODES = "AVAILABLE_NODES";


    private Map<String, Object> event;
    private List<OpcNode> allNodes;
    private OpcUa opcUa;

    private int numberProperties;

    public OpcUaAdapter() {
        this.event = new HashMap<>();
        this.numberProperties = 0;
    }

    public OpcUaAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        this.opcUa = OpcUa.from(this.adapterDescription);

        this.event = new HashMap<>();
        this.numberProperties = 0;
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .requiredAlternatives(Labels.withId(ACCESS_MODE),
                        Alternatives.from(Labels.withId(UNAUTHENTICATED)),
                        Alternatives.from(Labels.withId(USERNAME_GROUP),
                                StaticProperties.group(Labels.withId(USERNAME_GROUP),
                                StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME)),
                                StaticProperties.secretValue(Labels.withId(PASSWORD)))))
                .requiredAlternatives(Labels.withId(OPC_HOST_OR_URL),
                        Alternatives.from(Labels.withId(OPC_URL),
                                StaticProperties.stringFreeTextProperty(Labels.withId(OPC_SERVER_URL))
                        ),
                        Alternatives.from(Labels.withId(OPC_HOST),
                                StaticProperties.group(Labels.withId("host-port"),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OPC_SERVER_HOST)),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OPC_SERVER_PORT)))))
                .requiredTextParameter(Labels.withId(NAMESPACE_INDEX))
                .requiredTextParameter(Labels.withId(NODE_ID))
                .requiredMultiValueSelectionFromContainer(Labels.withId(AVAILABLE_NODES), Arrays.asList(NAMESPACE_INDEX, NODE_ID))
                .build();
        description.setAppId(ID);


        return  description;
    }

    public void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        String key = getRuntimeNameOfNode(item.getReadValueId().getNodeId());

        OpcNode currNode = this.allNodes.stream()
                .filter(node -> key.equals(node.getNodeId().getIdentifier().toString()))
                .findFirst()
                .orElse(null);

        event.put(currNode.getLabel(), value.getValue().getValue());

        // ensure that event is complete and all opc ua subscriptions transmitted at least one value
        if (event.keySet().size() >= this.numberProperties) {
            Map <String, Object> newEvent = new HashMap<>();
            // deep copy of event to prevent preprocessor error
            for (String k : event.keySet()) {
              newEvent.put(k, event.get(k));
            }
            adapterPipeline.process(newEvent);
        }
    }


    @Override
    public void startAdapter() throws AdapterException {

        this.opcUa = OpcUa.from(this.adapterDescription);

        try {
            this.opcUa.connect();

            this.allNodes = this.opcUa.browseNode(true);

            List<NodeId> nodeIds = new ArrayList<>();

            for (OpcNode rd : this.allNodes) {
                nodeIds.add(rd.nodeId);
            }

            this.numberProperties = nodeIds.size();
            this.opcUa.createListSubscription(nodeIds, this);
        } catch (Exception e) {
            throw new AdapterException("Could not connect to OPC-UA server! Server: " + this.opcUa.getOpcServerURL());
        }
    }

    @Override
    public void stopAdapter() throws AdapterException {
        // close connection
        this.opcUa.disconnect();
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new OpcUaAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {

        GuessSchema guessSchema = new GuessSchema();
        EventSchema eventSchema = new EventSchema();
        List<EventProperty> allProperties = new ArrayList<>();

        OpcUa opc = OpcUa.from(this.adapterDescription);

        try {
            opc.connect();
            List<OpcNode> res =  opc.browseNode(true);

            if (res.size() > 0) {
                for (OpcNode opcNode : res) {
                    if (opcNode.opcUnitId == 0) {
                        allProperties.add(PrimitivePropertyBuilder
                                .create(opcNode.getType(), opcNode.getLabel())
                                .label(opcNode.getLabel())
                                .build());
                    } else {
                        allProperties.add(PrimitivePropertyBuilder
                                .create(opcNode.getType(), opcNode.getLabel())
                                .label(opcNode.getLabel())
                                .measurementUnit(new URI(OpcUa.mapUnitIdToQudt(opcNode.opcUnitId)))
                                .build());
                    }
                }
            }

            opc.disconnect();
        } catch (Exception e) {

            throw new AdapterException("Could not guess schema for opc node! " + e.getMessage());

        }

        eventSchema.setEventProperties(allProperties);
        guessSchema.setEventSchema(eventSchema);

        return guessSchema;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {

        try {
            parameterExtractor.selectedAlternativeInternalId(OPC_HOST_OR_URL);
            parameterExtractor.selectedAlternativeInternalId(ACCESS_MODE);
        } catch (NullPointerException npe){
            return new ArrayList<>();
        }

        OpcUa opc = OpcUa.from(parameterExtractor);

        List<Option> nodes = new ArrayList<>();
        try {
            opc.connect();
            this.allNodes =  opc.browseNode(false);

            for (OpcNode opcNode: this.allNodes) {
                nodes.add(new Option(opcNode.getLabel(), opcNode.nodeId.getIdentifier().toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nodes;
    }

    private String getRuntimeNameOfNode(NodeId nodeId) {
        String[] keys = nodeId.getIdentifier().toString().split("\\.");
        String key;

        if (keys.length > 0) {
            key = keys[keys.length - 1];
        } else {
            key = nodeId.getIdentifier().toString();
        }

        return key;
    }
}
