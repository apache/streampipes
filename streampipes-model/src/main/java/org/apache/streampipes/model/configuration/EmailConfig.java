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

public class EmailConfig {

  private boolean emailConfigured;

  private TransportStrategy transportStrategy;
  private String smtpServerHost;
  private int smtpServerPort;

  private boolean usesAuthentication;

  private String smtpUsername;
  private String smtpPassword;

  private String senderAddress;
  private String senderName;

  private boolean usesProxy;

  private String proxyHost;
  private int proxyPort;

  private boolean usesProxyAuthentication;

  private String proxyUser;
  private String proxyPassword;

  private String testRecipientAddress;

  private boolean smtpPassEncrypted;
  private boolean proxyPassEncrypted;

  public static EmailConfig fromDefaults() {
    EmailConfig config = new EmailConfig();
    config.setEmailConfigured(false);

    return config;
  }

  public EmailConfig() {

  }

  public boolean isEmailConfigured() {
    return emailConfigured;
  }

  public void setEmailConfigured(boolean emailConfigured) {
    this.emailConfigured = emailConfigured;
  }

  public TransportStrategy getTransportStrategy() {
    return transportStrategy;
  }

  public void setTransportStrategy(TransportStrategy transportStrategy) {
    this.transportStrategy = transportStrategy;
  }

  public String getSmtpServerHost() {
    return smtpServerHost;
  }

  public void setSmtpServerHost(String smtpServerHost) {
    this.smtpServerHost = smtpServerHost;
  }

  public int getSmtpServerPort() {
    return smtpServerPort;
  }

  public void setSmtpServerPort(int smtpServerPort) {
    this.smtpServerPort = smtpServerPort;
  }

  public String getSmtpUsername() {
    return smtpUsername;
  }

  public void setSmtpUsername(String smtpUsername) {
    this.smtpUsername = smtpUsername;
  }

  public String getSmtpPassword() {
    return smtpPassword;
  }

  public void setSmtpPassword(String smtpPassword) {
    this.smtpPassword = smtpPassword;
  }

  public String getSenderAddress() {
    return senderAddress;
  }

  public void setSenderAddress(String senderAddress) {
    this.senderAddress = senderAddress;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public boolean isUsesProxy() {
    return usesProxy;
  }

  public void setUsesProxy(boolean usesProxy) {
    this.usesProxy = usesProxy;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public boolean isUsesAuthentication() {
    return usesAuthentication;
  }

  public void setUsesAuthentication(boolean usesAuthentication) {
    this.usesAuthentication = usesAuthentication;
  }

  public boolean isUsesProxyAuthentication() {
    return usesProxyAuthentication;
  }

  public void setUsesProxyAuthentication(boolean usesProxyAuthentication) {
    this.usesProxyAuthentication = usesProxyAuthentication;
  }

  public String getTestRecipientAddress() {
    return testRecipientAddress;
  }

  public void setTestRecipientAddress(String testRecipientAddress) {
    this.testRecipientAddress = testRecipientAddress;
  }

  public boolean isSmtpPassEncrypted() {
    return smtpPassEncrypted;
  }

  public void setSmtpPassEncrypted(boolean smtpPassEncrypted) {
    this.smtpPassEncrypted = smtpPassEncrypted;
  }

  public boolean isProxyPassEncrypted() {
    return proxyPassEncrypted;
  }

  public void setProxyPassEncrypted(boolean proxyPassEncrypted) {
    this.proxyPassEncrypted = proxyPassEncrypted;
  }
}
