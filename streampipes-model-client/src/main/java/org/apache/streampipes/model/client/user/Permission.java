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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class Permission {

  protected @SerializedName("_id") String permissionId;
  protected @SerializedName("_rev") String rev;

  // document type should be persisted to CouchDB with Gson serialization, but not via Jackson to the UI
  @JsonIgnore
  @SerializedName("$type")
  private String type = "permission";

  private String objectInstanceId;
  private String objectClassName;
  private boolean publicElement;

  private String ownerSid;

  private List<PermissionEntry> grantedAuthorities;

  public Permission() {
    this.grantedAuthorities = new ArrayList<>();
  }

  public String getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(String permissionId) {
    this.permissionId = permissionId;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getObjectInstanceId() {
    return objectInstanceId;
  }

  public void setObjectInstanceId(String objectInstanceId) {
    this.objectInstanceId = objectInstanceId;
  }

  public String getObjectClassName() {
    return objectClassName;
  }

  public void setObjectClassName(String objectClassName) {
    this.objectClassName = objectClassName;
  }

  public String getOwnerSid() {
    return ownerSid;
  }

  public void setOwnerSid(String ownerSid) {
    this.ownerSid = ownerSid;
  }

  public void addPermissionEntry(PermissionEntry permissionEntry) {
    this.grantedAuthorities.add(permissionEntry);
  }

  public List<PermissionEntry> getGrantedAuthorities() {
    return grantedAuthorities;
  }

  public void setGrantedAuthorities(List<PermissionEntry> grantedAuthorities) {
    this.grantedAuthorities = grantedAuthorities;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isPublicElement() {
    return publicElement;
  }

  public void setPublicElement(boolean publicElement) {
    this.publicElement = publicElement;
  }
}
