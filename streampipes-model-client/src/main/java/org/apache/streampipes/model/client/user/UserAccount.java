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

  protected List<UserApiToken> userApiTokens;

  protected boolean hideTutorial;
  protected boolean darkMode = false;
  protected boolean hasAcknowledged = false;
  protected String language;

  protected long createdAtMillis;
  protected long lastLoginAtMillis;

  /**
   * The authentication provider (LOCAL or one of the configured OAuth providers
   */
  protected String provider;
  protected boolean externallyManagedRoles = false;

  public UserAccount() {
    super(PrincipalType.USER_ACCOUNT);
    this.hideTutorial = false;
    this.userApiTokens = new ArrayList<>();
    this.createdAtMillis = System.currentTimeMillis();
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

  public boolean isExternallyManagedRoles() {
    return externallyManagedRoles;
  }

  public void setExternallyManagedRoles(boolean externallyManagedRoles) {
    this.externallyManagedRoles = externallyManagedRoles;
  }

  public boolean isHasAcknowledged() {
    return hasAcknowledged;
  }

  public void setHasAcknowledged(boolean hasAcknowledged) {
    this.hasAcknowledged = hasAcknowledged;
  }

  public long getCreatedAtMillis() {
    return createdAtMillis;
  }

  public void setCreatedAtMillis(long createdAtMillis) {
    this.createdAtMillis = createdAtMillis;
  }

  public long getLastLoginAtMillis() {
    return lastLoginAtMillis;
  }

  public void setLastLoginAtMillis(long lastLoginAtMillis) {
    this.lastLoginAtMillis = lastLoginAtMillis;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
