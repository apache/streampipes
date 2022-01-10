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

package org.apache.streampipes.connect.iiot.adapters.opcua;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.iiot.adapters.opcua.configuration.SpOpcUaConfig;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaNodeVariants;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaTypes;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.EUInformation;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.retrieveDataTypesFromServer;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

public class OpcUaNodeBrowser {

  private OpcUaClient client;
  private SpOpcUaConfig spOpcConfig;

  private List<Map<String, Integer>> unitIDs = new ArrayList<>();

  public OpcUaNodeBrowser(OpcUaClient client,
                          SpOpcUaConfig spOpcUaClientConfig) {
    this.client = client;
    this.spOpcConfig = spOpcUaClientConfig;
  }

  /***
   * Search for related nodes relative to {@link SpOpcUaConfig#getOriginNodeId()}
   * @param selectNodes indicates whether only nodes of {@link SpOpcUaConfig#getSelectedNodeNames()} should be returned
   * @return List of {@link OpcNode}s related to {@link SpOpcUaConfig#getOriginNodeId()}
   * @throws AdapterException
   */
  public List<OpcNode> browseNode(boolean selectNodes) throws AdapterException {
    NodeId originNodeId = spOpcConfig.getOriginNodeId();
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
   * Search for related nodes relative to {@link SpOpcUaConfig#getOriginNodeId()}
   * @param selectNodes indicates whether only nodes of {@link SpOpcUaConfig#getSelectedNodeNames()} should be returned
   * @return List of {@link OpcNode}s related to {@link SpOpcUaConfig#getOriginNodeId()}
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
//            result = result.stream().filter(node -> this.getSelectedNodeNames().contains(node.getLabel()))
//                    .collect(Collectors.toList());
      result = result.stream().filter(node -> this.spOpcConfig.getSelectedNodeNames().contains(node.getNodeId().getIdentifier().toString()))
              .collect(Collectors.toList());
    }

    return result;
  }
}
