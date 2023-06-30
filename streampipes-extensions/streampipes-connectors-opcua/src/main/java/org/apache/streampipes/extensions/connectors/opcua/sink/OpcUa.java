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
import org.apache.streampipes.extensions.connectors.opcua.client.SpOpcUaClient;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.vocabulary.XSD;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OpcUa {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUa.class);

  private OpcUaClient opcUaClient;
  private OpcUaParameters params;

  private NodeId node;

  private Class<?> targetDataType;
  private Class<?> sourceDataType;

  // define a mapping of StreamPipes data types to Java classes
  private static final HashMap<String, Class<?>> XSDMatchings = new HashMap<>();

  static {
    XSDMatchings.put(XSD.DOUBLE.toString(), Double.class);
    XSDMatchings.put(XSD.INTEGER.toString(), Integer.class);
    XSDMatchings.put(XSD.INT.toString(), Integer.class);
    XSDMatchings.put(XSD.BOOLEAN.toString(), Boolean.class);
    XSDMatchings.put(XSD.STRING.toString(), String.class);
    XSDMatchings.put(XSD.FLOAT.toString(), Float.class);
  }

  // define potential mappings, left can be mapped to right
  private static final HashMap<Class<?>, Class<?>[]> compatibleDataTypes = new HashMap<>();

  static {
    compatibleDataTypes.put(Double.class, new Class[]{Float.class, String.class});
    compatibleDataTypes.put(Float.class, new Class[]{Double.class, String.class});
    compatibleDataTypes.put(Integer.class, new Class[]{Double.class, Float.class, String.class});
    compatibleDataTypes.put(Boolean.class, new Class[]{String.class});
    compatibleDataTypes.put(String.class, new Class[]{String.class});
  }

  public void onInvocation(OpcUaParameters params) throws
      SpRuntimeException {

    try {
      this.params = params;
      this.node = NodeId.parse(params.getSelectedNode());
      opcUaClient = new SpOpcUaClient<>(params.getConfig()).getClient();
      opcUaClient.connect().get();

    } catch (Exception e) {
      throw new SpRuntimeException("Could not connect to OPC-UA server: " + params.getConfig().getOpcServerURL());
    }

    // check whether input data type and target data type are compatible
    try {
      Variant value = opcUaClient.getAddressSpace().getVariableNode(node).readValue().getValue();
      targetDataType = value.getValue().getClass();
      sourceDataType = XSDMatchings.get(params.getMappingPropertyType());
      if (!sourceDataType.equals(targetDataType)) {
        if (Arrays.stream(compatibleDataTypes.get(sourceDataType)).noneMatch(dt -> dt.equals(targetDataType))) {
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
      LOG.error("Mapping property type: " + this.params.getMappingPropertyType() + " is not supported");
    } else {

      DataValue value = new DataValue(v);
      CompletableFuture<StatusCode> f = opcUaClient.writeValue(node, value);

      try {
        StatusCode status = f.get();
        if (status.isBad()) {
          if (status.getValue() == 0x80740000L) {
            LOG.error("Type missmatch! Tried to write value of type: " + this.params.getMappingPropertyType()
                + " but server did not accept this");
          } else if (status.getValue() == 0x803B0000L) {
            LOG.error("Wrong access level. Not allowed to write to nodes");
          }
          LOG.error(
              "Value: " + value.getValue().toString() + " could not be written to node Id: "
                  + node.getIdentifier() + " on " + "OPC-UA server: " + params.getConfig().getOpcServerURL());
        }
      } catch (InterruptedException | ExecutionException e) {
        LOG.error("Exception: Value: " + value.getValue().toString() + " could not be written to node Id: "
            + node.getIdentifier() + " on " + "OPC-UA server: " + params.getConfig().getOpcServerURL());
      }
    }
  }

  public void onDetach() throws SpRuntimeException {
    opcUaClient.disconnect();
  }

  private Variant getValue(Event inputEvent) {
    Variant result = null;
    PrimitiveField propertyPrimitive =
        inputEvent.getFieldBySelector(this.params.getMappingPropertySelector()).getAsPrimitive();

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
}
