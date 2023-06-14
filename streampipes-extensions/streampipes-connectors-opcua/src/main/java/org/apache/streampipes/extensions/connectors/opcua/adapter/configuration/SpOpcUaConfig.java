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

package org.apache.streampipes.extensions.connectors.opcua.adapter.configuration;

import java.util.List;

public class SpOpcUaConfig {

  private final String opcServerURL;

  private boolean unauthenticated;

  private String username;
  private String password;

  private final List<String> selectedNodeNames;
  private final Integer pullIntervalMilliSeconds;

  /**
   * Constructor for security level {@code None}, OPC server given by url and subscription-based
   *
   * @param opcServerURL             complete OPC UA server url
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in subscription
   *                                 mode
   * @param selectedNodeNames        list of node names provided
   */
  public SpOpcUaConfig(String opcServerURL,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {

    this.opcServerURL = opcServerURL;
    this.unauthenticated = true;
    this.pullIntervalMilliSeconds = pullIntervalMilliSeconds;
    this.selectedNodeNames = selectedNodeNames;
  }

  /**
   * Constructor for security level {@code None} and OPC server given by hostname and port number
   *
   * @param opcServer                OPC UA hostname
   * @param opcServerPort            OPC UA port number
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds, {@code null} if in
   *                                 subscription mode
   * @param selectedNodeNames        list of node names provided
   */
  public SpOpcUaConfig(String opcServer,
                       int opcServerPort,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServer + ":" + opcServerPort, pullIntervalMilliSeconds, selectedNodeNames);
  }

  /**
   * Constructor for security level {@code Sign} and OPC server given by url
   *
   * @param opcServerURL             complete OPC UA server url
   * @param username                 username to authenticate at the OPC UA server
   * @param password                 corresponding password to given user name
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds,
   *                                 {@code null} if in subscription mode
   * @param selectedNodeNames        list of node names provided
   */
  public SpOpcUaConfig(String opcServerURL,
                       String username,
                       String password,
                       Integer pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServerURL, pullIntervalMilliSeconds, selectedNodeNames);
    this.unauthenticated = false;
    this.username = username;
    this.password = password;
  }

  /**
   * Constructor for OPC UA security level {@code Sign} and OPC server given by hostname and port number
   *
   * @param opcServer                OPC UA hostname
   * @param opcServerPort            OPC UA port number
   * @param username                 username to authenticate at the OPC UA server
   * @param password                 corresponding password to given user name
   * @param pullIntervalMilliSeconds duration of pull interval in milliseconds,
   *                                 {@code null} if in subscription mode
   * @param selectedNodeNames        list of node names provided
   */
  public SpOpcUaConfig(String opcServer,
                       int opcServerPort,
                       String username,
                       String password,
                       int pullIntervalMilliSeconds,
                       List<String> selectedNodeNames) {
    this(opcServer, opcServerPort, pullIntervalMilliSeconds, selectedNodeNames);
    this.unauthenticated = false;
    this.username = username;
    this.password = password;
  }

  public String getOpcServerURL() {
    return opcServerURL;
  }

  public boolean isUnauthenticated() {
    return unauthenticated;
  }

  public void setUnauthenticated(boolean unauthenticated) {
    this.unauthenticated = unauthenticated;
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

  public Integer getPullIntervalMilliSeconds() {
    return pullIntervalMilliSeconds;
  }
}
