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

import java.util.List;

@TsModel
public class UserInfo {

  private String userId;
  private String displayName;
  private String email;
  private List<String> roles;
  private boolean showTutorial;
  private boolean darkMode;

  public UserInfo() {
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public boolean isShowTutorial() {
    return showTutorial;
  }

  public void setShowTutorial(boolean showTutorial) {
    this.showTutorial = showTutorial;
  }

  public boolean isDarkMode() {
    return darkMode;
  }

  public void setDarkMode(boolean darkMode) {
    this.darkMode = darkMode;
  }
}
