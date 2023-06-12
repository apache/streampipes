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

package org.apache.streampipes.extensions.connectors.opcua.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.vocabulary.XSD;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OpcUa {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUa.class);

  private OpcUaClient opcUaClient;
  private OpcUaParameters params;
  private String serverUrl;
  private NodeId node;
  private Class targetDataType;
  private Class sourceDataType;

  // define a mapping of StreamPipes data types to Java classes
  private static final HashMap<String, Class> XSDMatchings = new HashMap<>();

  static {
    XSDMatchings.put(XSD.DOUBLE.toString(), Double.class);
    XSDMatchings.put(XSD.INTEGER.toString(), Integer.class);
    XSDMatchings.put(XSD.INT.toString(), Integer.class);
    XSDMatchings.put(XSD.BOOLEAN.toString(), Boolean.class);
    XSDMatchings.put(XSD.STRING.toString(), String.class);
    XSDMatchings.put(XSD.FLOAT.toString(), Float.class);
  }

  // define potential mappings, left can be mapped to right
  private static HashMap<Class, Class[]> compatibleDataTypes = new HashMap<>();

  static {
    compatibleDataTypes.put(Double.class, new Class[]{Float.class, String.class});
    compatibleDataTypes.put(Float.class, new Class[]{Double.class, String.class});
    compatibleDataTypes.put(Integer.class, new Class[]{Double.class, Float.class, String.class});
    compatibleDataTypes.put(Boolean.class, new Class[]{String.class});
    compatibleDataTypes.put(String.class, new Class[]{String.class});
  }

  public void onInvocation(OpcUaParameters parameters) throws
      SpRuntimeException {

    if (!parameters.hostname().startsWith("opc.tcp://")) {
      serverUrl = "opc.tcp://" + parameters.hostname() + ":" + parameters.port();
    } else {
      serverUrl = parameters.hostname() + ":" + parameters.port();
    }
    if (isInteger(parameters.nodeId())) {
      int integerNodeId = Integer.parseInt(parameters.nodeId());
      node = new NodeId(parameters.nameSpaceIndex(), integerNodeId);
    } else {
      node = new NodeId(parameters.nameSpaceIndex(), parameters.nodeId());
    }


    this.params = parameters;

    List<EndpointDescription> endpoints;

    try {

      endpoints = DiscoveryClient.getEndpoints(serverUrl).get();

      EndpointDescription endpoint = endpoints
          .stream()
          .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
          .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

      OpcUaClientConfig config = OpcUaClientConfig.builder()
          .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
          .setApplicationUri("urn:eclipse:milo:examples:client")
          .setEndpoint(endpoint)
          .build();

      opcUaClient = OpcUaClient.create(config);
      opcUaClient.connect().get();

    } catch (Exception e) {
      throw new SpRuntimeException("Could not connect to OPC-UA server: " + serverUrl);
    }

    // check whether input data type and target data type are compatible
    try {
      Variant value = opcUaClient.getAddressSpace().getVariableNode(node).readValue().getValue();
      targetDataType = value.getValue().getClass();
      sourceDataType = XSDMatchings.get(params.mappingPropertyType());
      if (!sourceDataType.equals(targetDataType)) {
        if (!Arrays.stream(compatibleDataTypes.get(sourceDataType)).anyMatch(dt -> dt.equals(targetDataType))) {
          throw new SpRuntimeException("Data Type of event of target node are not compatible");
        }
      }
    } catch (UaException e) {
      throw new SpRuntimeException("DataType of target node could not be determined: " + node.getIdentifier());
    }

  }

  public void onEvent(Event inputEvent) {

    Variant v = getValue(inputEvent);

    if (v == null) {
      LOG.error("Mapping property type: " + this.params.mappingPropertyType() + " is not supported");
    } else {

      DataValue value = new DataValue(v);
      CompletableFuture<StatusCode> f = opcUaClient.writeValue(node, value);

      try {
        StatusCode status = f.get();
        if (status.isBad()) {
          if (status.getValue() == 0x80740000L) {
            LOG.error("Type missmatch! Tried to write value of type: " + this.params.mappingPropertyType()
                + " but server did not accept this");
          } else if (status.getValue() == 0x803B0000L) {
            LOG.error("Wrong access level. Not allowed to write to nodes");
          }
          LOG.error(
              "Value: " + value.getValue().toString() + " could not be written to node Id: "
                  + this.params.nodeId() + " on " + "OPC-UA server: " + this.serverUrl);
        }
      } catch (InterruptedException | ExecutionException e) {
        LOG.error("Exception: Value: " + value.getValue().toString() + " could not be written to node Id: "
            + this.params.nodeId() + " on " + "OPC-UA server: " + this.serverUrl);
      }
    }
  }

  public void onDetach() throws SpRuntimeException {
    opcUaClient.disconnect();
  }

  private Variant getValue(Event inputEvent) {
    Variant result = null;
    PrimitiveField propertyPrimitive =
        inputEvent.getFieldBySelector(this.params.mappingPropertySelector()).getAsPrimitive();

    if (targetDataType.equals(Integer.class)) {
      result = new Variant(propertyPrimitive.getAsInt());
    } else if (targetDataType.equals(Double.class)) {
      result = new Variant(propertyPrimitive.getAsDouble());
    } else if (targetDataType.equals(Boolean.class)) {
      result = new Variant(propertyPrimitive.getAsBoolean());
    } else if (targetDataType.equals(Float.class)) {
      result = new Variant(propertyPrimitive.getAsFloat());
    } else if (targetDataType.equals(String.class)) {
      result = new Variant(propertyPrimitive.getAsString());
    }

    return result;
  }

  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
    // only got here if we didn't return false
    return true;
  }
}
