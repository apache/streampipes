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

package org.apache.streampipes.connect.iiot.adapters.opcua.utils;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.connect.iiot.adapters.opcua.OpcNode;
import org.apache.streampipes.connect.iiot.adapters.opcua.OpcUaNodeBrowser;
import org.apache.streampipes.connect.iiot.adapters.opcua.SpOpcUaClient;
import org.apache.streampipes.connect.iiot.adapters.opcua.configuration.SpOpcUaConfigBuilder;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/***
 * Collection of several utility functions in context of OPC UA
 */
public class OpcUaUtil {

    /***
     * Ensures server address starts with {@code opc.tcp://}
     * @param serverAddress server address as given by user
     * @return correctly formated server address
     */
    public static String formatServerAddress(String serverAddress) {

        if (!serverAddress.startsWith("opc.tcp://")) {
            serverAddress = "opc.tcp://" + serverAddress;
        }

        return serverAddress;
    }

    /***
     * OPC UA specific implementation of {@link org.apache.streampipes.connect.adapter.Adapter}
     * @param adapterStreamDescription
     * @return guess schema
     * @throws AdapterException
     * @throws ParseException
     */
    public static GuessSchema getSchema(SpecificAdapterStreamDescription adapterStreamDescription) throws AdapterException, ParseException {
        GuessSchema guessSchema = new GuessSchema();
        EventSchema eventSchema = new EventSchema();
        List<EventProperty> allProperties = new ArrayList<>();

        SpOpcUaClient spOpcUaClient = new SpOpcUaClient(SpOpcUaConfigBuilder.from(adapterStreamDescription));

        try {
            spOpcUaClient.connect();
            OpcUaNodeBrowser nodeBrowser = new OpcUaNodeBrowser(spOpcUaClient.getClient(), spOpcUaClient.getSpOpcConfig());
            List<OpcNode> selectedNodes = nodeBrowser.findNodes();

            if (!selectedNodes.isEmpty()) {
                for (OpcNode opcNode : selectedNodes) {
                    if (opcNode.hasUnitId()) {
                        allProperties.add(PrimitivePropertyBuilder
                            .create(opcNode.getType(), opcNode.getLabel())
                            .label(opcNode.getLabel())
                            .measurementUnit(new URI(opcNode.getQudtURI()))
                            .build());
                    } else {
                        allProperties.add(PrimitivePropertyBuilder
                            .create(opcNode.getType(), opcNode.getLabel())
                            .label(opcNode.getLabel())
                            .build());
                    }

                }
            }

            spOpcUaClient.disconnect();

        } catch (Exception e) {
            throw new AdapterException("Could not guess schema for opc node:  " + e.getMessage(), e.getCause());
        }

        eventSchema.setEventProperties(allProperties);
        guessSchema.setEventSchema(eventSchema);

        return guessSchema;
    }


    /***
     * OPC UA specific implementation of {@link org.apache.streampipes.container.api.ResolvesContainerProvidedOptions#resolveOptions(String, StaticPropertyExtractor)}.  }
     * @param internalName The internal name of the Static Property
     * @param parameterExtractor
     * @return {@code List<Option>} with available node names for the given OPC UA configuration
     */
    public static RuntimeResolvableTreeInputStaticProperty resolveConfiguration (String internalName,
                                                                                 StaticPropertyExtractor parameterExtractor) {

        RuntimeResolvableTreeInputStaticProperty config = parameterExtractor
                .getStaticPropertyByName(internalName, RuntimeResolvableTreeInputStaticProperty.class);
        // access mode and host/url have to be selected
        try {
            parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
            parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.ACCESS_MODE.name());
        } catch (NullPointerException nullPointerException) {
            return config;
        }

        SpOpcUaClient spOpcUaClient = new SpOpcUaClient(SpOpcUaConfigBuilder.from(parameterExtractor));
        try{
            spOpcUaClient.connect();
            OpcUaNodeBrowser nodeBrowser = new OpcUaNodeBrowser(spOpcUaClient.getClient(), spOpcUaClient.getSpOpcConfig());
            config.setNodes(nodeBrowser.buildNodeTreeFromOrigin());
            spOpcUaClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    public static String getRuntimeNameOfNode(NodeId nodeId) {
        String[] keys = nodeId.getIdentifier().toString().split("\\.");
        String key;

        if (keys.length > 0) {
            key = keys[keys.length - 1];
        } else {
            key = nodeId.getIdentifier().toString();
        }

        return key;
    }

    /**
     * connects to each node individually and updates the data type in accordance to the data from the server.
     * @param opcNodes List of opcNodes where the data type is not determined appropriately
     */
    public static void retrieveDataTypesFromServer(OpcUaClient client, List<OpcNode> opcNodes) throws AdapterException {

        for (OpcNode opcNode : opcNodes) {
            try {
                UInteger dataTypeId = (UInteger) client.getAddressSpace().getVariableNode(opcNode.getNodeId()).getDataType().getIdentifier();
                OpcUaTypes.getType(dataTypeId);
                opcNode.setType(OpcUaTypes.getType(dataTypeId));
            } catch (UaException e) {
               throw new AdapterException("Could not guess schema for opc node! " + e.getMessage());
            }
        }
    }

    /***
     * Enum for all possible labels in the context of OPC UA adapters
     */
    public enum OpcUaLabels {
        OPC_HOST_OR_URL,
        OPC_URL,
        OPC_HOST,
        OPC_SERVER_URL,
        OPC_SERVER_HOST,
        OPC_SERVER_PORT,
        NAMESPACE_INDEX,
        NODE_ID,
        ACCESS_MODE,
        USERNAME_GROUP,
        USERNAME,
        PASSWORD,
        UNAUTHENTICATED,
        AVAILABLE_NODES,
        PULLING_INTERVAL,
        ADAPTER_TYPE,
        PULL_MODE,
        SUBSCRIPTION_MODE;
    }
}
