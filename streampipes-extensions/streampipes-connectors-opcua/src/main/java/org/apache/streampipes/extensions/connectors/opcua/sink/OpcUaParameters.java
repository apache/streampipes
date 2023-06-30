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

import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

public final class OpcUaParameters {
  private final String selectedNode;
  private final String mappingPropertySelector;
  private final String mappingPropertyType;

  private final OpcUaConfig config;

  public OpcUaParameters(OpcUaConfig config,
                         String mappingPropertySelector,
                         String mappingPropertyType,
                         String selectedNode) {
    this.config = config;
    this.mappingPropertySelector = mappingPropertySelector;
    this.mappingPropertyType = mappingPropertyType;
    this.selectedNode = selectedNode;
  }

  public String getSelectedNode() {
    return selectedNode;
  }

  public String getMappingPropertySelector() {
    return mappingPropertySelector;
  }

  public String getMappingPropertyType() {
    return mappingPropertyType;
  }

  public OpcUaConfig getConfig() {
    return config;
  }
}
