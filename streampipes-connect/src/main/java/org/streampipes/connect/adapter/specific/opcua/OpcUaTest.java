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

package org.streampipes.connect.adapter.specific.opcua;


import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
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

public class OpcUaTest {

    //    private OpcUaClient myClient;
    private static String opcServerURL = "opc.tcp://141.21.43.39:4840";
    //    private static String opcServerURL = "opc.tcp://192.168.0.144:4840";
    private static final AtomicLong clientHandles = new AtomicLong(1L);


    public static void main(String... args) throws Exception {

        OpcUaClient client = init();
//        client.connect().get();
        client.connect().get();

        NodeId node1 = new NodeId(4, "|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG.auto_gruen");
        NodeId node2 = new NodeId(4, "|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG.auto_rot");
        NodeId node3 = new NodeId(4, "|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG.fuss_rot");
        NodeId node4 = new NodeId(4, "|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG");

//        browseNodeTest("", client, node4);

//        CompletableFuture<DataValue> va1 = client.readValue(0, TimestampsToReturn.Both, node1);
//        CompletableFuture<DataValue> va2 = client.readValue(0, TimestampsToReturn.Both, node2);
//        CompletableFuture<DataValue> va3 = client.readValue(0, TimestampsToReturn.Both, node3);
//
//
//        System.out.println("Auto grün: " + va1.get().getValue());
//        System.out.println("Auto rot: " + va2.get().getValue());
//        System.out.println("Fußgänger rot: " + va3.get().getValue());

	 /*   JSONParser parser = new JSONParser();
	    JSONObject json = (JSONObject) parser.parse(exchange.getIn().getBody().toString());*/

	 createListSubscription(client, Arrays.asList(node1, node2));
//        createSubscription(client, node1);
//        createSubscription(client, node2);

        // let the example run for 10 seconds then terminate
        Thread.sleep(100000000);

//	 client.disconnect();

    }



    private static OpcUaClient init() throws Exception{
        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(opcServerURL).get();

        EndpointDescription tmpEndpoint = endpoints[0];
        tmpEndpoint = updateEndpointUrl(tmpEndpoint, "141.21.43.39");
        endpoints = new EndpointDescription[]{tmpEndpoint};

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client")
                .setEndpoint(endpoint)
                .build();

        return new OpcUaClient(config);
    }

    private static EndpointDescription updateEndpointUrl(
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


    private static void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        System.out.println(
                "subscription value received: " + item.getReadValueId().toString() + " " + value.getValue().toString());

    }

    private static void createListSubscription(OpcUaClient client, List<NodeId> nodes) throws Exception {
        /*
         * create a subscription @ 1000ms
         */
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();


        List<CompletableFuture<DataValue>> values = new ArrayList<>();

        for (NodeId node : nodes) {
            values.add(client.readValue(0, TimestampsToReturn.Both, node));
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
                        item.setValueConsumer(OpcUaTest::onSubscriptionValue);
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


    /**
     * creates a subcription for the given node
     *
     * @param client
     * @param node
     * @throws Exception
     */
    private static void createSubscription(OpcUaClient client, NodeId node) throws Exception {
        /*
         * create a subscription @ 1000ms
         */
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

        CompletableFuture<DataValue> value = client.readValue(0, TimestampsToReturn.Both, node);

        if (value.get().getValue().toString().contains("null")) {
            System.out.println("Node has no value");
        } else {
            // Read a specific value attribute
            ReadValueId readValue = new ReadValueId(node, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

            // important: client handle must be unique per item
            UInteger clientHandle = uint(clientHandles.getAndIncrement());

            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    1000.0,     // sampling interval
                    null,      // filter, null means use default
                    uint(10),   // queue size
                    true         // discard oldest
            );

            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValue, MonitoringMode.Reporting, parameters);


            BiConsumer<UaMonitoredItem, Integer> onItemCreated =
                    (item, id) -> {
                        System.out.println(id);
                        item.setValueConsumer(OpcUaTest::onSubscriptionValue);
                    };


            List<UaMonitoredItem> items = subscription.createMonitoredItems(
                    TimestampsToReturn.Both,
                    Lists.newArrayList(request),
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
    }

    private static void browseNodeTest(String indent, OpcUaClient client, NodeId browseRoot) {
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

            List<ReferenceDescription> references = toList(browseResult.getReferences());

            for (ReferenceDescription rd : references) {
                System.out.println("=====================================================================");
                System.out.println(rd.toString());
                System.out.println(rd.getNodeClass());
                System.out.println("Node={} " + indent + " " + rd.getBrowseName().getName());
                System.out.println("=====================================================================");
                // recursively browse to children
                rd.getNodeId().local().ifPresent(nodeId -> browseNodeTest(indent + "  ", client, nodeId));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Browsing nodeId=" + browseRoot + " failed: " + e.getMessage());
        }
    }


    private List<ReferenceDescription> browseNode(String indent, OpcUaClient client, NodeId browseRoot) {
        List<ReferenceDescription> result = new ArrayList<>();

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

            List<ReferenceDescription> references = toList(browseResult.getReferences());

            for (ReferenceDescription rd : references) {
                result.add(rd);
                rd.getNodeId().local().ifPresent(nodeId -> browseNode(indent + "  ", client, nodeId));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Browsing nodeId=" + browseRoot + " failed: " + e.getMessage());
        }

        return result;

    }

}
