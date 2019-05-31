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

package org.streampipes.sinks.databases.jvm.opcua;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class OpcUaParameters extends EventSinkBindingParams {

  private String hostname;
  private Integer port;
  private String nodeId;
  private Integer nameSpaceIndex;
  private String numberMapping;

  public OpcUaParameters(DataSinkInvocation graph, String hostname, Integer port, String nodeId, Integer nameSpaceIndex, String numberMapping) {
    super(graph);
    this.hostname = hostname;
    this.port = port;
    this.nodeId = nodeId;
    this.nameSpaceIndex = nameSpaceIndex;
    this.numberMapping = numberMapping;
  }

  public String getHostName() {
    return hostname;
  }

  public void setHostName(String hostname) {
    this.hostname = hostname;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public Integer getNameSpaceIndex() {
    return nameSpaceIndex;
  }

  public void setNameSpaceIndex(Integer nameSpaceIndex) {
    this.nameSpaceIndex = nameSpaceIndex;
  }

  public String getNumberMapping() {
    return numberMapping;
  }

  public void setNumberMapping(String numberMapping) {
    this.numberMapping = numberMapping;
  }
}
