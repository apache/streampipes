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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.model.staticproperty.TreeInputNode;

import org.apache.plc4x.java.api.PlcConnectionFactory;
import org.apache.plc4x.java.api.messages.PlcBrowseItem;
import org.apache.plc4x.java.api.types.PlcResponseCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlcBrowseService {

  private final PlcConnectionFactory connectionFactory;

  public PlcBrowseService(PlcConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public List<TreeInputNode> browse(String connectionString, String query) throws SpConfigurationException {
    try (var connection = connectionFactory.getConnection(connectionString)) {
      if (!connection.getMetadata().isBrowseSupported()) {
        throw new SpConfigurationException("This PLC connection does not support browsing. Use manual tag entry.");
      }
      var response = connection.browseRequestBuilder().addQuery("tags", query).build()
          .execute().get(10, TimeUnit.SECONDS);
      if (response.getResponseCode("tags") != PlcResponseCode.OK) {
        throw new SpConfigurationException("PLC browsing failed (" + response.getResponseCode("tags")
            + "). Check the query or use manual tag entry.");
      }
      return toNodes(response.getValues("tags"), "root");
    } catch (SpConfigurationException e) {
      throw e;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SpConfigurationException("PLC browsing was interrupted.");
    } catch (Exception e) {
      // Driver exceptions may include connection URLs containing credentials.
      throw new SpConfigurationException("Could not browse the PLC. Check the connection and browse query.");
    }
  }

  private List<TreeInputNode> toNodes(List<PlcBrowseItem> items, String parent) {
    var nodes = new ArrayList<TreeInputNode>();
    var orderedItems = items.stream().sorted(Comparator.comparing(item -> Objects.toString(item.getName(), "")))
        .toList();
    for (int i = 0; i < orderedItems.size(); i++) {
      var item = orderedItems.get(i);
      var node = new TreeInputNode();
      var address = item.getTag() == null ? null : item.getTag().getAddressString();
      var id = parent + "/" + i;
      node.setInternalNodeName(address == null || address.isBlank() ? id : address);
      node.setNodeName(item.getName() == null || item.getName().isBlank() ? node.getInternalNodeName() : item.getName());
      node.setDataNode(item.isReadable() && address != null && !address.isBlank());
      if (address != null) {
        node.getNodeMetadata().put("Address", address);
        node.getNodeMetadata().put("Event field", "tag_" + address.replaceAll("[^a-zA-Z0-9_]", "_"));
      }
      node.setChildren(toNodes(new ArrayList<>(item.getChildren().values()), id));
      nodes.add(node);
    }
    nodes.sort(Comparator.comparing(TreeInputNode::getNodeName).thenComparing(TreeInputNode::getInternalNodeName));
    return nodes;
  }
}
