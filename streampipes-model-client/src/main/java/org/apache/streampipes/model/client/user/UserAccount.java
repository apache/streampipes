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

package org.apache.streampipes.model.client.user;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@TsModel
public class UserAccount extends Principal {

  public static final String LOCAL = "local";

  protected String fullName;
  protected String password;

  protected List<String> preferredDataStreams;
  protected List<String> preferredDataProcessors;
  protected List<String> preferredDataSinks;

  protected List<UserApiToken> userApiTokens;

  protected boolean hideTutorial;
  protected boolean darkMode = false;

  /**
   * The authentication provider (LOCAL or one of the configured OAuth providers
   */
  protected String provider;

  public UserAccount() {
    super(PrincipalType.USER_ACCOUNT);
    this.hideTutorial = false;
    this.userApiTokens = new ArrayList<>();
    this.preferredDataProcessors = new ArrayList<>();
    this.preferredDataSinks = new ArrayList<>();
    this.preferredDataStreams = new ArrayList<>();
    this.provider = UserAccount.LOCAL;
  }

  public static UserAccount from(String username,
                                 String encryptedPassword,
                                 Set<String> roles) {
    UserAccount account = new UserAccount();
    account.setUsername(username);
    account.setPassword(encryptedPassword);
    account.setRoles(roles);
    account.setAccountEnabled(true);
    account.setAccountLocked(false);

    return account;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public List<String> getPreferredDataStreams() {
    return preferredDataStreams;
  }

  public void setPreferredDataStreams(List<String> preferredDataStreams) {
    this.preferredDataStreams = preferredDataStreams;
  }

  public List<String> getPreferredDataProcessors() {
    return preferredDataProcessors;
  }

  public void setPreferredDataProcessors(List<String> preferredDataProcessors) {
    this.preferredDataProcessors = preferredDataProcessors;
  }

  public List<String> getPreferredDataSinks() {
    return preferredDataSinks;
  }

  public void setPreferredDataSinks(List<String> preferredDataSinks) {
    this.preferredDataSinks = preferredDataSinks;
  }

  public void addPreferredDataStream(String elementId) {
    this.preferredDataStreams.add(elementId);
  }

  public void addPreferredDataProcessor(String elementId) {
    this.preferredDataProcessors.add(elementId);
  }

  public void addPreferredDataSink(String elementId) {
    this.preferredDataSinks.add(elementId);
  }

  public void removePreferredDataStream(String elementId) {
    this.preferredDataStreams.remove(elementId);
  }

  public void removePreferredDataProcessor(String elementId) {
    this.preferredDataProcessors.remove(elementId);
  }

  public void removePreferredDataSink(String elementId) {
    this.preferredDataSinks.remove(elementId);
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public List<UserApiToken> getUserApiTokens() {
    return userApiTokens;
  }

  public void setUserApiTokens(List<UserApiToken> userApiTokens) {
    this.userApiTokens = userApiTokens;
  }

  public boolean isHideTutorial() {
    return hideTutorial;
  }

  public void setHideTutorial(boolean hideTutorial) {
    this.hideTutorial = hideTutorial;
  }

  public boolean isDarkMode() {
    return darkMode;
  }

  public void setDarkMode(boolean darkMode) {
    this.darkMode = darkMode;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }
}
