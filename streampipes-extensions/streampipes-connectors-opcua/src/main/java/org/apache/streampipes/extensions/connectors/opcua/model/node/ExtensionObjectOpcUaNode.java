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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.connect.guess.FieldStatus;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;

import org.eclipse.milo.opcua.binaryschema.Struct;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.util.Map;

public class ExtensionObjectOpcUaNode implements OpcUaNode {

  private final BasicVariableNodeInfo nodeInfo;

  public ExtensionObjectOpcUaNode(BasicVariableNodeInfo nodeInfo) {
    this.nodeInfo = nodeInfo;
  }

  @Override
  public BasicVariableNodeInfo nodeInfo() {
    return nodeInfo;
  }

  @Override
  public int getNumberOfEventProperties(OpcUaClient client) {
    var struct = extractStruct(client, nodeInfo.getNode().getValue().getValue());
    return struct.getMembers().size();
  }

  @Override
  public void addToEvent(OpcUaClient client,
                         Map<String, Object> event,
                         Variant variant) {
    var struct = extractStruct(client, variant);

    struct.getMembers().forEach((key, member) -> {
      var nodeName = nodeInfo.getDesiredName(key);
      event.put(nodeName, member.getValue());
    });
  }

  @Override
  public void addToEventPreview(OpcUaClient client,
                                Map<String, Object> eventPreview,
                                Map<String, FieldStatusInfo> fieldStatusInfos,
                                Variant variant,
                                FieldStatusInfo fieldStatusInfo) {
    if (fieldStatusInfo.getFieldStatus() == FieldStatus.GOOD) {
      var struct = extractStruct(client, variant);
      struct.getMembers().forEach((key, member) -> {
        var nodeName = nodeInfo.getDesiredName(key);
        eventPreview.put(nodeName, member.getValue());
        fieldStatusInfos.put(nodeName, fieldStatusInfo);
      });
    } else {
      throw new SpRuntimeException("Could not read value for " + nodeInfo.getBaseNodeName());
    }
  }

  private Struct extractStruct(OpcUaClient client,
                               Variant variant) {
    if (variant.getValue() instanceof ExtensionObject extensionObject) {
      var decoded = extensionObject.decode(client.getDynamicSerializationContext());
      if (decoded instanceof Struct struct) {
        return struct;
      }
    }
    throw new SpRuntimeException("Decoded value is not a Struct");
  }
}
