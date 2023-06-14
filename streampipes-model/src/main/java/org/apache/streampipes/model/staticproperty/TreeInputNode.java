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

package org.apache.streampipes.model.staticproperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeInputNode {

  List<TreeInputNode> children;
  private boolean dataNode;
  private boolean selected;
  private String nodeName;
  private String internalNodeName;

  private Map<String, Object> nodeMetadata;

  public TreeInputNode() {
    this.children = new ArrayList<>();
    this.nodeMetadata = new HashMap<>();
  }

  public TreeInputNode(TreeInputNode other) {
    this.dataNode = other.isDataNode();
    this.selected = other.isSelected();
    this.nodeName = other.getNodeName();
    this.internalNodeName = other.getInternalNodeName();
    this.children = other.getChildren().stream().map(TreeInputNode::new).collect(Collectors.toList());
    this.nodeMetadata = other.getNodeMetadata();
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  public List<TreeInputNode> getChildren() {
    return children;
  }

  public void setChildren(List<TreeInputNode> children) {
    this.children = children;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getInternalNodeName() {
    return internalNodeName;
  }

  public void setInternalNodeName(String internalNodeName) {
    this.internalNodeName = internalNodeName;
  }

  public boolean isDataNode() {
    return dataNode;
  }

  public void setDataNode(boolean dataNode) {
    this.dataNode = dataNode;
  }

  public void addChild(TreeInputNode node) {
    this.children.add(node);
  }

  public Map<String, Object> getNodeMetadata() {
    return nodeMetadata;
  }

  public void setNodeMetadata(Map<String, Object> nodeMetadata) {
    this.nodeMetadata = nodeMetadata;
  }
}
