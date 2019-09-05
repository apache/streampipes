/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapters.opcua;


import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

public class OpcUa {

    private NodeId node;
    private String opcServerHost;
    private int opcServerPort;
    private OpcUaClient client;

    private static final AtomicLong clientHandles = new AtomicLong(1L);

    public OpcUa(String opcServerURL, int opcServerPort, int namespaceIndex, String nodeId) {

        this.opcServerHost = opcServerURL;
        this.opcServerPort = opcServerPort;

        if (isInteger(nodeId)) {
            int integerNodeId = Integer.parseInt(nodeId);
            this.node  = new NodeId(namespaceIndex, integerNodeId);
        } else {
            this.node  = new NodeId(namespaceIndex, nodeId);
        }

    }

    public OpcUa(String opcServerURL, int opcServerPort, int namespaceIndex, NodeId nodeId) {

        this.opcServerHost = opcServerURL;
        this.opcServerPort = opcServerPort;
        this.node  = nodeId;
    }


    public void connect() throws Exception {

        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://" + opcServerHost + ":" + opcServerPort).get();

        EndpointDescription tmpEndpoint = endpoints[0];
        tmpEndpoint = updateEndpointUrl(tmpEndpoint, opcServerHost);
        endpoints = new EndpointDescription[]{tmpEndpoint};

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client")
                .setEndpoint(endpoint)
                .build();

        this.client = new OpcUaClient(config);
        client.connect().get();
    }

    public void disconnect() {
        client.disconnect();
    }

    private EndpointDescription updateEndpointUrl(
            EndpointDescription original, String hostname) throws URISyntaxException {

        URI uri = new URI(original.getEndpointUrl()).parseServerAuthority();

        String endpointUrl = String.format(
                "%s://%s:%s%s",
                uri.getScheme(),
                hostname,
                uri.getPort(),
                uri.getPath()
        );

        return new EndpointDescription(
                endpointUrl,
                original.getServer(),
                original.getServerCertificate(),
                original.getSecurityMode(),
                original.getSecurityPolicyUri(),
                original.getUserIdentityTokens(),
                original.getTransportProfileUri(),
                original.getSecurityLevel()
        );
    }

    public List<OpcNode> browseNode() throws AdapterException {
        List<OpcNode> referenceDescriptions = browseNode(node);

        if (referenceDescriptions.size() == 0) {
            referenceDescriptions = getRootNote(node);
        }

        return referenceDescriptions;
    }

    private List<OpcNode> getRootNote(NodeId browseRoot) {
        List<OpcNode> result = new ArrayList<>();

        try {
//            VariableNode resultNode = client.getAddressSpace().getVariableNode(browseRoot).get();
            String label = client.getAddressSpace().getVariableNode(browseRoot).get().getDisplayName().get().getText();
            Datatypes type = OpcUaTypes.getType((UInteger)client.getAddressSpace().getVariableNode(browseRoot).get().getDataType().get().getIdentifier());
            NodeId nodeId = client.getAddressSpace().getVariableNode(browseRoot).get().getNodeId().get();
            result.add(new OpcNode(label, type, nodeId));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<OpcNode> browseNode(NodeId browseRoot) throws AdapterException {
        List<OpcNode> result = new ArrayList<>();


        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue())
        );

        try {
            BrowseResult browseResult = client.browse(browse).get();

            if (browseResult.getStatusCode().isBad()) {
                throw new AdapterException(browseResult.getStatusCode().toString());
            }

            List<ReferenceDescription> references = toList(browseResult.getReferences());

            for (ReferenceDescription rd : references) {
                if (rd.getNodeClass() == NodeClass.Variable) {

                    OpcNode opcNode = new OpcNode( rd.getBrowseName().getName(), OpcUaTypes.getType((UInteger) rd.getTypeDefinition().getIdentifier()), rd.getNodeId().local().get());
                    rd.getNodeId();

                    result.add(opcNode);
                    rd.getNodeId().local().ifPresent(nodeId -> {
                        try {
                            browseNode(nodeId);
                        } catch (AdapterException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new AdapterException("Browsing nodeId=" + browse + " failed: " + e.getMessage());
        }

        return result;

    }


    public void createListSubscription(List<NodeId> nodes, OpcUaAdapter opcUaAdapter) throws Exception {
        /*
         * create a subscription @ 1000ms
         */
        UaSubscription subscription = this.client.getSubscriptionManager().createSubscription(1000.0).get();


        List<CompletableFuture<DataValue>> values = new ArrayList<>();

        for (NodeId node : nodes) {
            values.add(this.client.readValue(0, TimestampsToReturn.Both, node));
        }

        for (CompletableFuture<DataValue> value : values) {
            if (value.get().getValue().toString().contains("null")) {
                System.out.println("Node has no value");
            }
        }


        List<ReadValueId> readValues = new ArrayList<>();
        // Read a specific value attribute
        for (NodeId node : nodes) {
            readValues.add(new ReadValueId(node, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE));
        }

        List<MonitoredItemCreateRequest> requests = new ArrayList<>();

        for (ReadValueId readValue : readValues) {
            // important: client handle must be unique per item
            UInteger clientHandle = uint(clientHandles.getAndIncrement());

            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    1000.0,     // sampling interval
                    null,      // filter, null means use default
                    uint(10),   // queue size
                    true         // discard oldest
            );

            requests.add(new MonitoredItemCreateRequest(readValue, MonitoringMode.Reporting, parameters));
        }

        BiConsumer<UaMonitoredItem, Integer> onItemCreated =
                (item, id) -> {
                    item.setValueConsumer(opcUaAdapter::onSubscriptionValue);
                };

        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                requests,
                onItemCreated
        ).get();

        for (UaMonitoredItem item : items) {
            NodeId tagId = item.getReadValueId().getNodeId();
            if (item.getStatusCode().isGood()) {
                System.out.println("item created for nodeId="+ tagId);
            } else {
                System.out.println("failed to create item for " + item.getReadValueId().getNodeId() + item.getStatusCode());
            }
        }

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


}
