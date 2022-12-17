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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.user.management.util.GrantedAuthoritiesBuilder;
import org.apache.streampipes.user.management.util.GrantedPermissionsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PrincipalUserDetails<T extends Principal> implements UserDetails {

  protected T details;
  private Set<String> allAuthorities;
  private Set<String> allObjectPermissions;

  public PrincipalUserDetails(T details) {
    this.details = details;
    this.allAuthorities = new GrantedAuthoritiesBuilder(details).buildAllAuthorities();
    this.allObjectPermissions = new GrantedPermissionsBuilder(details).buildAllPermissions();
  }

  public T getDetails() {
    return details;
  }

  public void setDetails(T details) {
    this.details = details;
  }

  @Override
  public boolean isAccountNonExpired() {
    return !this.details.isAccountExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !this.details.isAccountLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.details.isAccountEnabled();
  }

  @Override
  public String getUsername() {
    return this.details.getUsername();
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return allAuthorities.stream().map(r -> (GrantedAuthority) () -> r).collect(Collectors.toList());
  }

  @JsonIgnore
  public Set<String> getAllObjectPermissions() {
    return allObjectPermissions;
  }
}
