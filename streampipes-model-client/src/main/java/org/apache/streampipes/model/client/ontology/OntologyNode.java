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

package org.apache.streampipes.model.client.ontology;

import java.util.ArrayList;
import java.util.List;

public class OntologyNode {

  private String prefix;
  private String namespace;

  private String id;
  private String title;
  private String icon;

  private List<OntologyNode> nodes;

  public OntologyNode(String id, String title, NodeType nodeType) {
    this.nodes = new ArrayList<>();
    this.id = id;
    this.title = title;
    this.icon = toIconUrl(nodeType);
  }

  public OntologyNode(String id, String title, String prefix, String namespace, NodeType nodeType) {
    this(id, title, nodeType);
    this.prefix = prefix;
    this.namespace = namespace;
  }

  private String toIconUrl(NodeType nodeType) {
    return "Test";
    // return Configuration.getInstance().WEBAPP_BASE_URL +
    // Configuration.getInstance().CONTEXT_PATH + "/img/" +nodeType.name() +".png";
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String text) {
    this.title = text;
  }

  public List<OntologyNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<OntologyNode> children) {
    this.nodes = children;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof OntologyNode)) {
      return false;
    } else {
      return this.id.equals(((OntologyNode) other).getId());
    }
  }
}
