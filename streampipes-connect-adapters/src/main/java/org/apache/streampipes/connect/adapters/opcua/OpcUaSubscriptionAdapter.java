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
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil.OpcUaLabels;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.*;

public class OpcUaSubscriptionAdapter extends SpecificDataStreamAdapter implements ResolvesContainerProvidedOptions {

    public static final String ID = "org.apache.streampipes.connect.adapters.opcua.subscription";

    private Map<String, Object> event;
    private List<OpcNode> allNodes;
    private OpcUa opcUa;

    private int numberProperties;

    public OpcUaSubscriptionAdapter() {
        this.event = new HashMap<>();
        this.numberProperties = 0;
    }

    public OpcUaSubscriptionAdapter(SpecificAdapterStreamDescription adapterDescription) {
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
                .requiredAlternatives(Labels.withId(OpcUaLabels.ACCESS_MODE.name()),
                        Alternatives.from(Labels.withId(OpcUaLabels.UNAUTHENTICATED.name())),
                        Alternatives.from(Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                                StaticProperties.group(Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                                StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.USERNAME.name())),
                                StaticProperties.secretValue(Labels.withId(OpcUaLabels.PASSWORD.name())))))
                .requiredAlternatives(Labels.withId(OpcUaLabels.OPC_HOST_OR_URL.name()),
                        Alternatives.from(Labels.withId(OpcUaLabels.OPC_URL.name()),
                                StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_URL.name()))
                        ),
                        Alternatives.from(Labels.withId(OpcUaLabels.OPC_HOST.name()),
                                StaticProperties.group(Labels.withId("host-port"),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_HOST.name())),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_PORT.name())))))
                .requiredTextParameter(Labels.withId(OpcUaLabels.NAMESPACE_INDEX.name()))
                .requiredTextParameter(Labels.withId(OpcUaLabels.NODE_ID.name()))
                .requiredMultiValueSelectionFromContainer(
                        Labels.withId(OpcUaLabels.AVAILABLE_NODES.name()),
                        Arrays.asList(OpcUaLabels.NAMESPACE_INDEX.name(), OpcUaLabels.NODE_ID.name())
                )
                .build();
        description.setAppId(ID);


        return  description;
    }

    public void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        String key = OpcUaUtil.getRuntimeNameOfNode(item.getReadValueId().getNodeId());

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
        return new OpcUaSubscriptionAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {

        return OpcUaUtil.getSchema(adapterDescription);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {

        return OpcUaUtil.resolveOptions(requestId, parameterExtractor);
    }
}
