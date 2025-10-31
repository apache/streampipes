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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

public class MqttConfig {

  private Boolean authenticated;

  private String url;
  private String topic;
  private String username;
  private String password;

  //private String caCertificatePath;   
  //private String clientCertificatePath; 
  //private String clientKeyPath;       
  private Boolean tls = false;          
  //private String tlsVersion;    


  public MqttConfig(String url, String topic) {
    this.authenticated = false;
    this.url = url;
    this.topic = topic;

  }

  public MqttConfig(String url, String topic, String username, String password) {
    this(url, topic);
    this.authenticated = true;
    this.username = username;
    this.password = password;
  }

  public MqttConfig(String url, String topic, Boolean tlsEnabled, String caCertificatePath, String clientCertificatePath, String clientKeyPath, String tlsVersion) {
    this.authenticated = false;
    this.url = url;
    this.topic = topic;
    this.tls = tlsEnabled;
    //this.caCertificatePath = caCertificatePath;
    //this.clientCertificatePath = clientCertificatePath;
    //this.clientKeyPath = clientKeyPath;
    //this.tlsVersion = tlsVersion;
}

public MqttConfig(String url, String topic, String username, String password, Boolean tlsEnabled) {
    this(url, topic, username, password); 
    this.tls = tlsEnabled;
}

public MqttConfig(String url, String topic, Boolean tlsEnabled) {
    this(url, topic); 
    this.tls = tlsEnabled;
}




   public Boolean getTlsEnabled() {
    return tls;
  }

  public void setTlsEnabled(Boolean tlsEnabled) {
    this.tls = tlsEnabled;
  }

  /**public String getCaCertificatePath() {
    return caCertificatePath;
  }

  public void setCaCertificatePath(String caCertificatePath) {
    this.caCertificatePath = caCertificatePath;
  }

  public String getClientCertificatePath() {
    return clientCertificatePath;
  }

  public void setClientCertificatePath(String clientCertificatePath) {
    this.clientCertificatePath = clientCertificatePath;
  }

  public String getClientKeyPath() {
    return clientKeyPath;
  }

  public void setClientKeyPath(String clientKeyPath) {
    this.clientKeyPath = clientKeyPath;
  }

  public String getTlsVersion() {
    return tlsVersion;
  }

  public void setTlsVersion(String tlsVersion) {
    this.tlsVersion = tlsVersion;
  }*/

  public Boolean getAuthenticated() {
    return authenticated;
  }

  public String getUrl() {
    return url;
  }

  public String getTopic() {
    return topic;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
