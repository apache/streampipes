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


import static org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil.retrieveDataTypesFromServer;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil.OpcUaLabels;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaNodeVariants;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaTypes;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * Wrapper class for all OPC UA specific stuff.
 */
public class OpcUa {

    static Logger LOG = LoggerFactory.getLogger(OpcUa.class);

    private NodeId originNodeId;
    private String opcServerURL;
    private OpcUaClient client;
    private boolean unauthenticated;
    private Integer pullIntervalMilliSeconds;
    private String user;
    private String password;
    private List<Map<String, Integer>> unitIDs = new ArrayList<>();
    private List<String> selectedNodeNames;

    private static final AtomicLong clientHandles = new AtomicLong(1L);

    /***
     *
     * @return current {@link org.eclipse.milo.opcua.sdk.client.OpcUaClient}
     */
    public OpcUaClient getClient() {
        return this.client;
    }


    /**
     * Constructor for security level {@code None}, OPC server given by url and subscription-based
     *
     * @param opcServerURL complete OPC UA server url
     * @param namespaceIndex namespace index of the given node
     * @param nodeId node identifier
     * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription mode
     * @param selectedNodeNames list of node names provided from {@link OpcUaUtil#resolveOptions(String, StaticPropertyExtractor)}
     */
    public OpcUa(String opcServerURL, int namespaceIndex, String nodeId, int pullIntervalMilliSeconds, List<String> selectedNodeNames) {

        this.opcServerURL = opcServerURL;
        this.unauthenticated = true;
        this.pullIntervalMilliSeconds = pullIntervalMilliSeconds;
        this.selectedNodeNames = selectedNodeNames;

        if (isInteger(nodeId)) {
            int integerNodeId = Integer.parseInt(nodeId);
            this.originNodeId = new NodeId(namespaceIndex, integerNodeId);
        } else {
            this.originNodeId = new NodeId(namespaceIndex, nodeId);
        }
    }

    /**
     * Constructor for security level {@code None} and OPC server given by hostname and port number
     *
     * @param opcServer OPC UA hostname
     * @param opcServerPort OPC UA port number
     * @param namespaceIndex namespace index of the given node
     * @param nodeId node identifier
     * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription mode
     * @param selectedNodeNames list of node names provided from {@link OpcUaUtil#resolveOptions(String, StaticPropertyExtractor)}
     */
    public OpcUa(String opcServer, int opcServerPort, int namespaceIndex, String nodeId, int pullIntervalMilliSeconds, List<String> selectedNodeNames) {
        this( opcServer + ":" + opcServerPort, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
    }

    /**
     * Constructor for security level {@code Sign} and OPC server given by url
     *
     * @param opcServerURL complete OPC UA server url
     * @param namespaceIndex namespace index of the given node
     * @param nodeId node identifier
     * @param username username to authenticate at the OPC UA server
     * @param password corresponding password to given user name
     * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription mode
     * @param selectedNodeNames list of node names provided from {@link OpcUaUtil#resolveOptions(String, StaticPropertyExtractor)}
     */
    public OpcUa(String opcServerURL, int namespaceIndex, String nodeId, String username, String password, int pullIntervalMilliSeconds, List<String> selectedNodeNames) {
        this(opcServerURL, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
        this.unauthenticated = false;
        this.user = username;
        this.password = password;
    }

    /**
     * Constructor for OPC UA security level {@code Sign} and OPC server given by hostname and port number
     *
     * @param opcServer OPC UA hostname
     * @param opcServerPort OPC UA port number
     * @param namespaceIndex namespace index of the given node
     * @param nodeId node identifier
     * @param username username to authenticate at the OPC UA server
     * @param password corresponding password to given user name
     * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription mode
     * @param selectedNodeNames list of node names provided from {@link OpcUaUtil#resolveOptions(String, StaticPropertyExtractor)}
     */
    public OpcUa(String opcServer, int opcServerPort, int namespaceIndex, String nodeId, String username, String password, int pullIntervalMilliSeconds, List<String> selectedNodeNames) {
        this (opcServer, opcServerPort, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
        this.unauthenticated = false;
        this.user = username;
        this.password = password;
    }

    /**
     * Creates {@link OpcUa}  instance in accordance with the given {@link org.apache.streampipes.sdk.extractor.StaticPropertyExtractor}.
     * @param extractor extractor for user inputs
     * @return {@link OpcUa}  instance based on information from {@code extractor}
     */
    public static OpcUa from(StaticPropertyExtractor extractor) {

        String selectedAlternativeConnection = extractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
        String selectedAlternativeAuthentication = extractor.selectedAlternativeInternalId(OpcUaLabels.ACCESS_MODE.name());

        int namespaceIndex = extractor.singleValueParameter(OpcUaLabels.NAMESPACE_INDEX.name(), int.class);
        String nodeId = extractor.singleValueParameter(OpcUaLabels.NODE_ID.name(), String.class);

        boolean usePullMode = extractor.selectedAlternativeInternalId(OpcUaLabels.ADAPTER_TYPE.name()).equals(OpcUaLabels.PULL_MODE.name());
        boolean useURL = selectedAlternativeConnection.equals(OpcUaLabels.OPC_URL.name());
        boolean unauthenticated =  selectedAlternativeAuthentication.equals(OpcUaLabels.UNAUTHENTICATED.name());

        Integer pullIntervalSeconds = null;
        if (usePullMode) {
            pullIntervalSeconds = extractor.singleValueParameter(OpcUaLabels.PULLING_INTERVAL.name(), Integer.class);
        }

        List<String> selectedNodeNames = extractor.selectedMultiValues(OpcUaLabels.AVAILABLE_NODES.name(), String.class);

        if (useURL && unauthenticated){

            String serverAddress = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_URL.name(), String.class);
            serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

            return new OpcUa(serverAddress, namespaceIndex, nodeId, pullIntervalSeconds, selectedNodeNames);

        } else if(!useURL && unauthenticated){
            String serverAddress = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_HOST.name(), String.class);
            serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
            int port = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_PORT.name(), int.class);

            return new OpcUa(serverAddress, port, namespaceIndex, nodeId, pullIntervalSeconds, selectedNodeNames);
        } else {

            String username = extractor.singleValueParameter(OpcUaLabels.USERNAME.name(), String.class);
            String password = extractor.secretValue(OpcUaLabels.PASSWORD.name());

            if (useURL) {
                String serverAddress = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_URL.name(), String.class);
                serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

                return new OpcUa(serverAddress, namespaceIndex, nodeId, username, password, pullIntervalSeconds, selectedNodeNames);
            } else {
                String serverAddress = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_HOST.name(), String.class);
                serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
                int port = extractor.singleValueParameter(OpcUaLabels.OPC_SERVER_PORT.name(), int.class);

                return new OpcUa(serverAddress, port, namespaceIndex, nodeId, username, password, pullIntervalSeconds, selectedNodeNames);
            }
        }

    }

    /***
     * Creates {@link OpcUa}  instance in accordance with the given {@link org.apache.streampipes.model.connect.adapter.AdapterDescription}
     * @param adapterDescription description of current adapter
     * @return {@link OpcUa}  instance based on information from {@code adapterDescription}
     */
    public static OpcUa from(AdapterDescription adapterDescription){

        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

        return from(extractor);
    }

    /***
     * Establishes appropriate connection to OPC UA endpoint depending on the {@link OpcUa} instance
     *
     * @throws Exception
     */
    public void connect() throws Exception {

        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(this.opcServerURL).get();
        String host = this.opcServerURL.split("://")[1].split(":")[0];

        EndpointDescription tmpEndpoint = endpoints
                .stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst()
                .orElseThrow(() -> new Exception("No endpoint with security policy none"));

        tmpEndpoint = updateEndpointUrl(tmpEndpoint, host);
        endpoints = Collections.singletonList(tmpEndpoint);

        EndpointDescription endpoint = endpoints
                .stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        OpcUaClientConfig config;
        if (this.unauthenticated) {
            config = OpcUaClientConfig.builder()
                    .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                    .setApplicationUri("urn:eclipse:milo:examples:client")
                    .setEndpoint(endpoint)
                    .build();
        } else {
            config = OpcUaClientConfig.builder()
                    .setIdentityProvider(new UsernameProvider(this.user, this.password))
                    .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                    .setApplicationUri("urn:eclipse:milo:examples:client")
                    .setEndpoint(endpoint)
                    .build();
        }
        this.client = OpcUaClient.create(config);
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

    /***
     * Search for related nodes relative to {@link OpcUa#originNodeId}
     * @param selectNodes indicates whether only nodes of {@link OpcUa#selectedNodeNames} should be returned
     * @return List of {@link OpcNode}s related to {@link OpcUa#originNodeId}
     * @throws AdapterException
     */
    public List<OpcNode> browseNode(boolean selectNodes) throws AdapterException {
        List<OpcNode> discoveredNodes = browseNode(originNodeId, selectNodes);

        if (discoveredNodes.size() == 0) {
            discoveredNodes =  getRootNote(originNodeId);
        } else if (selectNodes) { // only required for getSchema where the selectedNodes are already set.
            /* In case a node with sub-nodes is queried, the data types are not detected appropriately.
               This has to be performed separately.
             */
            retrieveDataTypesFromServer(client, discoveredNodes);
        }

        return discoveredNodes;
    }

    private List<OpcNode> getRootNote(NodeId browseRoot) {
        List<OpcNode> result = new ArrayList<>();

        try {
//            VariableNode resultNode = client.getAddressSpace().getVariableNode(browseRoot).get();
            String label = client.getAddressSpace().getVariableNode(browseRoot).getDisplayName().getText();
            int opcDataTypeId = ((UInteger) client.getAddressSpace().getVariableNode(browseRoot).getDataType().getIdentifier()).intValue();
            Datatypes type = OpcUaTypes.getType((UInteger)client.getAddressSpace().getVariableNode(browseRoot).getDataType().getIdentifier());
            NodeId nodeId = client.getAddressSpace().getVariableNode(browseRoot).getNodeId();

            // if rootNote is of type Property or EUInformation it does not deliver any data value,
            // therefore return an empty list
            if (opcDataTypeId == OpcUaNodeVariants.Property.getId() || opcDataTypeId == OpcUaNodeVariants.EUInformation.getId()){
                return result;
            }

            // check whether a unitID is detected for this node
            Integer unitID = null;
            for (Map<String, Integer> unit: this.unitIDs) {
                if (unit.get(nodeId) != null) {
                    unitID = unit.get(nodeId);
                }
            }

            if (unitID != null){
                result.add(new OpcNode(label,type, nodeId, unitID));
            } else {
                result.add(new OpcNode(label, type, nodeId));
            }

        } catch (UaException e) {
            e.printStackTrace();
        }

        return result;
    }

    /***
     * Search for related nodes relative to {@link OpcUa#originNodeId}
     * @param selectNodes indicates whether only nodes of {@link OpcUa#selectedNodeNames} should be returned
     * @return List of {@link OpcNode}s related to {@link OpcUa#originNodeId}
     * @throws AdapterException
     */
    private List<OpcNode> browseNode(NodeId browseRoot, boolean selectNodes) throws AdapterException {
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

            for (ReferenceDescription ref: references){
                if (ref.getNodeClass().name().equals("Object")) {
                    NodeId brwRoot =  ref.getNodeId().toNodeId(client.getNamespaceTable()).orElseThrow(AdapterException::new);

                    BrowseDescription brw = new BrowseDescription(
                            brwRoot,
                            BrowseDirection.Both,
                            Identifiers.HasComponent,
                            true,
                            uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                            uint(BrowseResultMask.All.getValue())
                    );
                    List<ReferenceDescription> subReferences = toList(client.browse(brw).get().getReferences());
                    references = Stream.concat(references.stream(), subReferences.stream()).collect(Collectors.toList());
                }

            }

            for (ReferenceDescription rd : references) {
                if (rd.getNodeClass() == NodeClass.Variable) {

                    EUInformation eu;

                    // check for whether the Node is of type Property
                    if (OpcUaNodeVariants.Property.getId() == ((UInteger) rd.getTypeDefinition().getIdentifier()).intValue()) {

                        ExpandedNodeId property = rd.getNodeId();
                        NodeId propertyNode = property.toNodeId(client.getNamespaceTable()).orElseThrow(AdapterException::new);

                        // check node for EU Information

                       if (OpcUaNodeVariants.EUInformation.getId() == ((UInteger) client.getAddressSpace().getVariableNode(propertyNode).getDataType().getIdentifier()).intValue()){

                           ExtensionObject euExtension = (ExtensionObject) client.readValue(0, TimestampsToReturn.Both, propertyNode).get().getValue().getValue();

                           // save information about EngineeringUnit in list
                           eu = (EUInformation) euExtension.decode(client.getSerializationContext());
                           Map map = new HashMap();
                           map.put(browseRoot, eu.getUnitId());
                           this.unitIDs.add(map);

                       }

                    } else {

                        // check whether there exists an unitID for this node
                        Integer unitID = null;
                        for (Map<String, Integer> unit: this.unitIDs){
                            if (unit.get(browseRoot) != null){
                                unitID = unit.get(browseRoot);
                            }
                        }
                        OpcNode opcNode;
                        if (unitID != null){
                            opcNode = new OpcNode(rd.getBrowseName().getName(), OpcUaTypes.getType((UInteger) rd.getTypeDefinition().getIdentifier()), rd.getNodeId().toNodeId(client.getNamespaceTable()).orElseThrow(AdapterException::new), unitID);
                        } else {
                            opcNode = new OpcNode(rd.getBrowseName().getName(), OpcUaTypes.getType((UInteger) rd.getTypeDefinition().getIdentifier()), rd.getNodeId().toNodeId(client.getNamespaceTable()).orElseThrow(AdapterException::new));
                        }
                        rd.getNodeId();

                        result.add(opcNode);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException | UaException e) {
            throw new AdapterException("Browsing nodeId=" + browse + " failed: " + e.getMessage());
        }

        if (selectNodes) {
            // filter for nodes that were selected by the user during configuration
            result = result.stream().filter(node -> this.getSelectedNodeNames().contains(node.getLabel()))
                    .collect(Collectors.toList());
        }

        return result;

    }


    /***
     * Register subscriptions for given OPC UA nodes
     * @param nodes List of {@link org.eclipse.milo.opcua.stack.core.types.builtin.NodeId}
     * @param opcUaAdapter current instance of {@link OpcUaAdapter}
     * @throws Exception
     */
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
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


    public List<String> getSelectedNodeNames() {
        return selectedNodeNames;
    }

    public String getOpcServerURL() {
        return opcServerURL;
    }

    public boolean inPullMode() {
        return !(this.pullIntervalMilliSeconds == null);
    }

    public int getPullIntervalMilliSeconds() {
        return this.pullIntervalMilliSeconds;
    }
}
