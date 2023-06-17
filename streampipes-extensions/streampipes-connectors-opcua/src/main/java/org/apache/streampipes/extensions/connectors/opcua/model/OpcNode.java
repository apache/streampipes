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

package org.apache.streampipes.extensions.connectors.opcua.model;

import org.apache.streampipes.sdk.utils.Datatypes;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 * OpcNode is a StreamPipes internal model of an OPC UA node.
 * It's main purpose is to ease the handling of nodes within StreamPipes.
 */

public class OpcNode {
  String label;
  Datatypes type;
  NodeId nodeId;
  int opcUnitId;
  private boolean readable;

  /**
   * Constructor for class OpcNode without an OPC UA unit identifier. <br>
   * Unit identifier is set to zero as default.
   *
   * @param label  name of the OPC UA node
   * @param type   datatype of the OPC UA node
   * @param nodeId identifier of the OPC UA node
   */
  public OpcNode(String label, Datatypes type, NodeId nodeId) {
    this.label = label;
    this.type = type;
    this.nodeId = nodeId;
    this.opcUnitId = 0;
  }

  /**
   * Constructor for class OpcNode with an OPC UA unit identifier. <br>
   * This identifier references to an OPC UA measurement unit, e.g. degree celsius. <br>
   * With {@link OpcNode#getQudtURI()} the OPC UA internal ID is mapped to the QUDT unit ontology <br>
   *
   * @param label     name of the OPC UA node
   * @param type      datatype of the OPC UA node
   * @param nodeId    identifier of the OPC UA node
   * @param opcUnitId OPC UA internal unit identifier
   */
  public OpcNode(String label, Datatypes type, NodeId nodeId, Integer opcUnitId) {
    this.label = label;
    this.type = type;
    this.nodeId = nodeId;
    this.opcUnitId = opcUnitId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Datatypes getType() {
    return type;
  }

  public void setType(Datatypes type) {
    this.type = type;
  }

  public NodeId getNodeId() {
    return nodeId;
  }

  public void setNodeId(NodeId nodeId) {
    this.nodeId = nodeId;
  }

  public int getOpcUnitId() {
    return this.opcUnitId;
  }

  public boolean hasUnitId() {
    // zero is the default case when no unit id is present
    return this.opcUnitId != 0;
  }

  public boolean isReadable() {
    return readable;
  }

  public void setReadable(boolean readable) {
    this.readable = readable;
  }

  /**
   * Returns the corresponding QUDT URI if the {@code opcUnitId} is given,
   * otherwise it returns an empty string. <br>
   * Currently, there are only two examples added. <br>
   * Other units have to be added manually, please have a look at the <a href="http://opcfoundation.org/UA/EngineeringUnits/UNECE/UNECE_to_OPCUA.csv"> OPC UA unitID mapping table</a>. <br>
   *
   * @return QUDT URI as string of the given unit
   */

  public String getQudtURI() {
    switch (this.opcUnitId) {
      case 17476:
        return "http://qudt.org/vocab/unit#DEG";
      case 4408652:
        return "http://qudt.org/vocab/unit#DegreeCelsius";
      default:
        return "";
    }
  }
}
