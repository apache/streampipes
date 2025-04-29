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

package org.apache.streampipes.extensions.connectors.opcua.model.node;

import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaTypes;
import org.apache.streampipes.model.connect.guess.FieldStatus;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class PrimitiveOpcUaNode implements OpcUaNode {

  private final BasicVariableNodeInfo nodeInfo;
  private final List<String> runtimeNamesToDelete;

  public PrimitiveOpcUaNode(BasicVariableNodeInfo nodeInfo,
                            List<String> runtimeNamesToDelete) {
    this.nodeInfo = nodeInfo;
    this.runtimeNamesToDelete = runtimeNamesToDelete;
  }

  @Override
  public BasicVariableNodeInfo nodeInfo() {
    return nodeInfo;
  }

  @Override
  public int getNumberOfEventProperties(OpcUaClient client) {
    return 1;
  }

  @Override
  public void addToSchema(OpcUaClient client,
                          List<EventProperty> eventProperties) {
    var nodeName = nodeInfo().getBaseNodeName();
    eventProperties.add(PrimitivePropertyBuilder
        .create(getType(), nodeName)
        .label(nodeName)
        .build());
  }

  @Override
  public void addToEvent(OpcUaClient client,
                         Map<String, Object> event,
                         Variant variant) {
    var nodeName = nodeInfo.getDesiredName("");
    if (!runtimeNamesToDelete.contains(nodeName)) {
      event.put(nodeName, extractValue(variant));
    }
  }

  @Override
  public void addToEventPreview(OpcUaClient client,
                                Map<String, Object> eventPreview,
                                Map<String, FieldStatusInfo> fieldStatusInfos,
                                Variant variant,
                                FieldStatusInfo fieldStatusInfo) {
    if (fieldStatusInfo.getFieldStatus() == FieldStatus.GOOD) {
      eventPreview.put(nodeInfo().getBaseNodeName(), extractValue(variant));
    }
    fieldStatusInfos.put(nodeInfo().getBaseNodeName(), fieldStatusInfo);
  }

  private Datatypes getType() {
    UInteger value = (UInteger) nodeInfo.getNode().getDataType()
        .getIdentifier();
    return OpcUaTypes.getType(value);
  }

  private Object extractValue(Variant variant) {
    var rawValue = variant.getValue();
    if (rawValue instanceof ByteString) {
      // encode ByteString to base64 string
      return Base64.getEncoder().encodeToString(((ByteString) rawValue).bytes());
    } else if (rawValue instanceof DateTime) {
      // convert DateTime to UTC timestamp in ms
      return ((DateTime) rawValue).getJavaTime();
    }
    return rawValue;
  }
}
