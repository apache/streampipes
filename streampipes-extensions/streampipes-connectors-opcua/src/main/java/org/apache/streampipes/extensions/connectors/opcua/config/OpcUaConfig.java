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

package org.apache.streampipes.extensions.connectors.opcua.config;

import java.util.List;

public class OpcUaConfig {

  private String opcServerURL;
  private boolean unauthenticated;
  private String username;
  private String password;
  private List<String> selectedNodeNames;

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
}
