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

package org.apache.streampipes.extensions.connectors.opcua.utils;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaNodeBrowser;
import org.apache.streampipes.extensions.connectors.opcua.client.SpOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.model.OpcNode;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
   * OPC UA specific implementation of
   * @param extractor
   * @return guess schema
   * @throws AdapterException
   * @throws ParseException
   */
  public static GuessSchema getSchema(IAdapterParameterExtractor extractor)
      throws AdapterException, ParseException {
    var builder = GuessSchemaBuilder.create();
    EventSchema eventSchema = new EventSchema();
    Map<String, Object> eventPreview = new HashMap<>();
    Map<String, FieldStatusInfo> fieldStatusInfos = new HashMap<>();
    List<EventProperty> allProperties = new ArrayList<>();

    SpOpcUaClient<OpcUaConfig> spOpcUaClient = new SpOpcUaClient<>(
        SpOpcUaConfigExtractor.extractSharedConfig(extractor.getStaticPropertyExtractor(), new OpcUaConfig())
    );

    try {
      spOpcUaClient.connect();
      OpcUaNodeBrowser nodeBrowser =
          new OpcUaNodeBrowser(spOpcUaClient.getClient(), spOpcUaClient.getSpOpcConfig());
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

      var nodeIds = selectedNodes.stream().map(OpcNode::getNodeId).collect(Collectors.toList());
      var response = spOpcUaClient.getClient().readValues(0, TimestampsToReturn.Both, nodeIds);

      var returnValues = response.get();

      makeEventPreview(selectedNodes, eventPreview, fieldStatusInfos, returnValues);


    } catch (Exception e) {
      throw new AdapterException("Could not guess schema for opc node:  " + e.getMessage(), e);
    } finally {
      spOpcUaClient.disconnect();
    }

    eventSchema.setEventProperties(allProperties);
    builder.properties(allProperties);
    builder.fieldStatusInfos(fieldStatusInfos);
    builder.preview(eventPreview);

    return builder.build();
  }

  private static void makeEventPreview(List<OpcNode> selectedNodes,
                                       Map<String, Object> eventPreview,
                                       Map<String, FieldStatusInfo> fieldStatusInfos,
                                       List<DataValue> dataValues) {

    for (int i = 0; i < dataValues.size(); i++) {
      var dv = dataValues.get(i);
      String label = selectedNodes.get(i).getLabel();
      if (StatusCode.GOOD.equals(dv.getStatusCode())) {
        var value = dv.getValue().getValue();
        eventPreview.put(label, value);
        fieldStatusInfos.put(label, FieldStatusInfo.good());
      } else {
        String additionalInfo = dv.getStatusCode() != null ? dv.getStatusCode().toString() : "Status code is null";
        fieldStatusInfos.put(label, FieldStatusInfo.bad(additionalInfo, false));
      }
    }
  }


  /***
   * OPC UA specific implementation of
   * {@link ResolvesContainerProvidedOptions resolveOptions(String, StaticPropertyExtractor)}.
   * @param internalName The internal name of the Static Property
   * @param parameterExtractor to extract parameters from the OPC UA config
   * @return {@code List<Option>} with available node names for the given OPC UA configuration
   */
  public static RuntimeResolvableTreeInputStaticProperty
      resolveConfiguration(String internalName,
                           IStaticPropertyExtractor parameterExtractor)
      throws SpConfigurationException {

    RuntimeResolvableTreeInputStaticProperty config = parameterExtractor
        .getStaticPropertyByName(internalName, RuntimeResolvableTreeInputStaticProperty.class);
    // access mode and host/url have to be selected
    try {
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.ACCESS_MODE.name());
    } catch (NullPointerException nullPointerException) {
      return config;
    }

    SpOpcUaClient spOpcUaClient = new SpOpcUaClient(
        SpOpcUaConfigExtractor.extractSharedConfig(parameterExtractor, new OpcUaConfig())
    );

    try {
      spOpcUaClient.connect();
      OpcUaNodeBrowser nodeBrowser =
          new OpcUaNodeBrowser(spOpcUaClient.getClient(), spOpcUaClient.getSpOpcConfig());

      var nodes = nodeBrowser.buildNodeTreeFromOrigin(config.getNextBaseNodeToResolve());
      if (Objects.isNull(config.getNextBaseNodeToResolve())) {
        config.setNodes(nodes);
      } else {
        config.setLatestFetchedNodes(nodes);
      }


      return config;
    } catch (UaException e) {
      throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e), e);
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      throw new SpConfigurationException("Could not connect to the OPC UA server with the provided settings", e);
    } finally {
      if (spOpcUaClient.getClient() != null) {
        spOpcUaClient.disconnect();
      }
    }
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
   *
   * @param opcNodes List of opcNodes where the data type is not determined appropriately
   */
  public static void retrieveDataTypesFromServer(OpcUaClient client, List<OpcNode> opcNodes) throws AdapterException {

    for (OpcNode opcNode : opcNodes) {
      try {
        UInteger dataTypeId =
            (UInteger) client.getAddressSpace().getVariableNode(opcNode.getNodeId()).getDataType()
                .getIdentifier();
        OpcUaTypes.getType(dataTypeId);
        opcNode.setType(OpcUaTypes.getType(dataTypeId));
      } catch (UaException e) {
        throw new AdapterException("Could not guess schema for opc node! " + e.getMessage());
      }
    }
  }


}
