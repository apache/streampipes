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

import org.apache.streampipes.extensions.connectors.opcua.config.identity.IdentityConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.security.SecurityConfig;

import java.util.List;

public class OpcUaConfig {

  private String opcServerURL;
  private List<String> selectedNodeNames;
  private IdentityConfig identityConfig;
  private SecurityConfig securityPolicyConfig;

  public OpcUaConfig() {

  }

  public String getOpcServerURL() {
    return opcServerURL;
  }

  public void setOpcServerURL(String opcServerURL) {
    this.opcServerURL = opcServerURL;
  }

  public List<String> getSelectedNodeNames() {
    return selectedNodeNames;
  }

  public void setSelectedNodeNames(List<String> selectedNodeNames) {
    this.selectedNodeNames = selectedNodeNames;
  }

  public IdentityConfig getIdentityConfig() {
    return identityConfig;
  }

  public void setIdentityConfig(IdentityConfig identityConfig) {
    this.identityConfig = identityConfig;
  }

  public SecurityConfig getSecurityConfig() {
    return securityPolicyConfig;
  }

  public void setSecurityConfig(SecurityConfig securityPolicyConfig) {
    this.securityPolicyConfig = securityPolicyConfig;
  }

  public String getUniqueServerId() {
    return String.format("%s-%s-%s", opcServerURL, securityPolicyConfig, identityConfig);
  }
}
