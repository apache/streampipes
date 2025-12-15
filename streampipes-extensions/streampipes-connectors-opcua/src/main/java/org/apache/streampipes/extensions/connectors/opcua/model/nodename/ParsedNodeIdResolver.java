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

package org.apache.streampipes.extensions.connectors.opcua.model.nodename;

import org.eclipse.milo.opcua.sdk.core.nodes.VariableNode;

public class ParsedNodeIdResolver implements NamingStrategyResolver {

  @Override
  public String resolveName(VariableNode node,
                            String fieldAppendix) {
    var nodeIdStr = node.getNodeId().toParseableString();
    var sanitizedNodeIdStr = removeSpecialChars(nodeIdStr);
    return sanitizedNodeIdStr + (!fieldAppendix.isEmpty() ? "_" + fieldAppendix : "");
  }

  private String removeSpecialChars(String parseableNodeId) {
    return parseableNodeId.replaceAll("[^a-zA-Z0-9]", "_");
  }
}
