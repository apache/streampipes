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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Principal {

  protected @SerializedName("_id") String principalId;
  protected @SerializedName("_rev") String rev;
  protected String username;
  protected Set<String> objectPermissions;
  protected Set<Role> roles;
  protected Set<String> groups;
  @JsonIgnore
  private String type = "principal";
  private boolean accountEnabled;
  private boolean accountLocked;
  private boolean accountExpired;
  private PrincipalType principalType;

  public Principal(PrincipalType principalType) {
    this.principalType = principalType;
    this.roles = new HashSet<>();
    this.groups = new HashSet<>();
    this.objectPermissions = new HashSet<>();
  }


  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getPrincipalId() {
    return principalId;
  }

  public void setPrincipalId(String principalId) {
    this.principalId = principalId;
  }

  private Element find(String elementId, List<Element> source) {
    return source.stream().filter(f -> f.getElementId().equals(elementId)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  public boolean isAccountEnabled() {
    return accountEnabled;
  }

  public void setAccountEnabled(boolean accountEnabled) {
    this.accountEnabled = accountEnabled;
  }

  public boolean isAccountLocked() {
    return accountLocked;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  public boolean isAccountExpired() {
    return accountExpired;
  }

  public void setAccountExpired(boolean accountExpired) {
    this.accountExpired = accountExpired;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public PrincipalType getPrincipalType() {
    return principalType;
  }

  public void setPrincipalType(PrincipalType principalType) {
    this.principalType = principalType;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Set<String> getObjectPermissions() {
    return objectPermissions;
  }

  public void setObjectPermissions(Set<String> objectPermissions) {
    this.objectPermissions = objectPermissions;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
