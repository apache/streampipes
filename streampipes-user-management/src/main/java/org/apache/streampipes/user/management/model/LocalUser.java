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
package org.apache.streampipes.user.management.model;

import org.apache.streampipes.model.client.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class LocalUser extends User implements UserDetails {

  public LocalUser(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.ownSepas = user.getOwnSepas();
    this.ownActions = user.getOwnActions();
    this.darkMode = user.isDarkMode();
    this.hideTutorial = user.isHideTutorial();
    this.fullName = user.getFullName();
    this.ownSources = user.getOwnSources();
    this.preferredActions = user.getPreferredActions();
    this.preferredSepas = user.getPreferredSepas();
    this.preferredSources = user.getPreferredSources();
    this.password = user.getPassword();
    this.rev = user.getRev();
    this.userId = user.getUserId();
    this.roles= user.getRoles();
    this.userApiTokens = user.getUserApiTokens();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
