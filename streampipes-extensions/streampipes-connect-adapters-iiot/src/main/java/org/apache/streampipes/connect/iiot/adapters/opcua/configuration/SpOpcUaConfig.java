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

package org.apache.streampipes.connect.iiot.adapters.opcua.configuration;

import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.List;

public class SpOpcUaConfig {

  private String opcServerURL;
  private int namespaceIndex;

  private boolean unauthenticated;

  private String username;
  private String password;

  private List<String> selectedNodeNames;
  private Integer pullIntervalMilliSeconds;
  private NodeId originNodeId;

  public SpOpcUaConfig() {
  }

  /**
   * Constructor for security level {@code None}, OPC server given by url and subscription-based
   *
   * @param opcServerURL             complete OPC UA server url
   * @param namespaceIndex           namespace index of the given node
   * @param nodeId                   node identifier
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription
   *                                 mode
   * @param selectedNodeNames        list of node names provided from
   *                                 {@link OpcUaUtil#resolveConfiguration(String, StaticPropertyExtractor)}
   *                                 (String, StaticPropertyExtractor)}
   */
  public SpOpcUaConfig(String opcServerURL,
                       int namespaceIndex,
                       String nodeId,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {

    this.opcServerURL = opcServerURL;
    this.namespaceIndex = namespaceIndex;
    this.unauthenticated = true;
    this.pullIntervalMilliSeconds = pullIntervalMilliSeconds;
    this.selectedNodeNames = selectedNodeNames;

    if (isInteger(nodeId)) {
      int integerNodeId = Integer.parseInt(nodeId);
      this.originNodeId = new NodeId(namespaceIndex, integerNodeId);
    } else {
      this.originNodeId = new NodeId(namespaceIndex, nodeId);
    }
  }

  /**
   * Constructor for security level {@code None} and OPC server given by hostname and port number
   *
   * @param opcServer                OPC UA hostname
   * @param opcServerPort            OPC UA port number
   * @param namespaceIndex           namespace index of the given node
   * @param nodeId                   node identifier
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in
   *                                 subscription mode
   * @param selectedNodeNames        list of node names provided from
   *                                 {@link OpcUaUtil#resolveConfiguration(String, StaticPropertyExtractor)}
   */
  public SpOpcUaConfig(String opcServer,
                       int opcServerPort,
                       int namespaceIndex,
                       String nodeId,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServer + ":" + opcServerPort, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
  }

  /**
   * Constructor for security level {@code Sign} and OPC server given by url
   *
   * @param opcServerURL             complete OPC UA server url
   * @param namespaceIndex           namespace index of the given node
   * @param nodeId                   node identifier
   * @param username                 username to authenticate at the OPC UA server
   * @param password                 corresponding password to given user name
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds,
   *                                 {@code null} if in subscription mode
   * @param selectedNodeNames        list of node names provided from
   *                                 {@link OpcUaUtil#resolveConfiguration(String, StaticPropertyExtractor)}
   */
  public SpOpcUaConfig(String opcServerURL,
                       int namespaceIndex,
                       String nodeId,
                       String username,
                       String password,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServerURL, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
    this.unauthenticated = false;
    this.username = username;
    this.password = password;
  }

  /**
   * Constructor for OPC UA security level {@code Sign} and OPC server given by hostname and port number
   *
   * @param opcServer                OPC UA hostname
   * @param opcServerPort            OPC UA port number
   * @param namespaceIndex           namespace index of the given node
   * @param nodeId                   node identifier
   * @param username                 username to authenticate at the OPC UA server
   * @param password                 corresponding password to given user name
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds,
   *                                 {@code null} if in subscription mode
   * @param selectedNodeNames        list of node names provided from
   *                                 {@link OpcUaUtil#resolveConfiguration(String, StaticPropertyExtractor)}
   */
  public SpOpcUaConfig(String opcServer,
                       int opcServerPort,
                       int namespaceIndex,
                       String nodeId,
                       String username,
                       String password,
                       int pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServer, opcServerPort, namespaceIndex, nodeId, pullIntervalMilliSeconds, selectedNodeNames);
    this.unauthenticated = false;
    this.username = username;
    this.password = password;
  }

  public String getOpcServerURL() {
    return opcServerURL;
  }

  public void setOpcServerURL(String opcServerURL) {
    this.opcServerURL = opcServerURL;
  }

  public boolean isUnauthenticated() {
    return unauthenticated;
  }

  public void setUnauthenticated(boolean unauthenticated) {
    this.unauthenticated = unauthenticated;
  }

  public int getNamespaceIndex() {
    return namespaceIndex;
  }

  public void setNamespaceIndex(int namespaceIndex) {
    this.namespaceIndex = namespaceIndex;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getSelectedNodeNames() {
    return selectedNodeNames;
  }

  public void setSelectedNodeNames(List<String> selectedNodeNames) {
    this.selectedNodeNames = selectedNodeNames;
  }

  public Integer getPullIntervalMilliSeconds() {
    return pullIntervalMilliSeconds;
  }

  public void setPullIntervalMilliSeconds(Integer pullIntervalMilliSeconds) {
    this.pullIntervalMilliSeconds = pullIntervalMilliSeconds;
  }

  public NodeId getOriginNodeId() {
    return originNodeId;
  }

  public void setOriginNodeId(NodeId originNodeId) {
    this.originNodeId = originNodeId;
  }

  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException | NullPointerException e) {
      return false;
    }
    // only got here if we didn't return false
    return true;
  }
}
