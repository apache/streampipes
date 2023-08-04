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

import com.google.gson.annotations.SerializedName;

public class SpCoreConfiguration {

  public static final String ID = "core";

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String id = ID;

  private MessagingSettings messagingSettings;
  private LocalAuthConfig localAuthConfig;
  private EmailConfig emailConfig;
  private GeneralConfig generalConfig;

  private boolean isConfigured;

  private String assetDir;
  private String filesDir;

  public SpCoreConfiguration() {
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MessagingSettings getMessagingSettings() {
    return messagingSettings;
  }

  public void setMessagingSettings(MessagingSettings messagingSettings) {
    this.messagingSettings = messagingSettings;
  }

  public LocalAuthConfig getLocalAuthConfig() {
    return localAuthConfig;
  }

  public void setLocalAuthConfig(LocalAuthConfig localAuthConfig) {
    this.localAuthConfig = localAuthConfig;
  }

  public EmailConfig getEmailConfig() {
    return emailConfig;
  }

  public void setEmailConfig(EmailConfig emailConfig) {
    this.emailConfig = emailConfig;
  }

  public GeneralConfig getGeneralConfig() {
    return generalConfig;
  }

  public void setGeneralConfig(GeneralConfig generalConfig) {
    this.generalConfig = generalConfig;
  }

  public boolean isConfigured() {
    return isConfigured;
  }

  public void setConfigured(boolean configured) {
    isConfigured = configured;
  }

  public String getAssetDir() {
    return assetDir;
  }

  public void setAssetDir(String assetDir) {
    this.assetDir = assetDir;
  }

  public String getFilesDir() {
    return filesDir;
  }

  public void setFilesDir(String filesDir) {
    this.filesDir = filesDir;
  }
}
