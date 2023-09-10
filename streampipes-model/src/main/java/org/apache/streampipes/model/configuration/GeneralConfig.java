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
package org.apache.streampipes.model.configuration;

import java.util.List;

public class GeneralConfig {

  private String appName;
  private String protocol;
  private String hostname;
  private int port;
  private boolean configured;

  private boolean allowSelfRegistration;
  private boolean allowPasswordRecovery;

  private List<String> defaultUserRoles;

  public GeneralConfig() {
  }

  public GeneralConfig(String protocol, String hostname, int port) {
    this.protocol = protocol;
    this.hostname = hostname;
    this.port = port;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public boolean isConfigured() {
    return configured;
  }

  public void setConfigured(boolean configured) {
    this.configured = configured;
  }

  public boolean isAllowSelfRegistration() {
    return allowSelfRegistration;
  }

  public void setAllowSelfRegistration(boolean allowSelfRegistration) {
    this.allowSelfRegistration = allowSelfRegistration;
  }

  public boolean isAllowPasswordRecovery() {
    return allowPasswordRecovery;
  }

  public void setAllowPasswordRecovery(boolean allowPasswordRecovery) {
    this.allowPasswordRecovery = allowPasswordRecovery;
  }

  public List<String> getDefaultUserRoles() {
    return defaultUserRoles;
  }

  public void setDefaultUserRoles(List<String> defaultUserRoles) {
    this.defaultUserRoles = defaultUserRoles;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }
}
