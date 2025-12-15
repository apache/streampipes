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

import org.apache.streampipes.extensions.connectors.opcua.model.nodename.NamingStrategyResolver;
import org.apache.streampipes.extensions.connectors.opcua.model.nodename.ParsedNodeIdResolver;
import org.apache.streampipes.extensions.connectors.opcua.model.nodename.SimpleNameResolver;
import org.apache.streampipes.model.staticproperty.Option;

import org.eclipse.milo.opcua.sdk.core.nodes.VariableNode;

public enum OpcUaNamingStrategy {
  DISPLAY_NAME("Display Name",
      new SimpleNameResolver((VariableNode node) -> node.getDisplayName().getText())),
  BROWSE_NAME("Browse Name",
      new SimpleNameResolver((VariableNode node) -> "_" + node.getBrowseName().toParseableString())),
  PARSED_NODE_ID("Parsed Node ID",
      new ParsedNodeIdResolver());

  private final String optionLabel;
  private final NamingStrategyResolver resolver;

  OpcUaNamingStrategy(String optionLabel,
                      NamingStrategyResolver resolver) {
    this.optionLabel = optionLabel;
    this.resolver = resolver;
  }

  public Option toOption() {
    return new Option(optionLabel, this.name());
  }

  public String getDesiredName(VariableNode node,
                               String fieldAppendix) {
    return resolver.resolveName(node, fieldAppendix);
  }
}
