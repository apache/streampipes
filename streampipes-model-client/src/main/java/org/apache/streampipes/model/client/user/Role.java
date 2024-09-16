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
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@TsModel
public class Role implements Storable {

  protected @SerializedName("_id") String elementId;
  protected @SerializedName("_rev") String rev;

  private String label;
  private boolean defaultRole;
  private List<String> privilegeIds;

  // document type should be persisted to CouchDB with Gson serialization, but not via Jackson to the UI
  @JsonIgnore
  @SerializedName("$type")
  private String type = "role";

  public static Role createDefaultRole(String id,
                                       String label,
                                       List<String> privilegeIds) {
    Role role = new Role();
    role.elementId = id;
    role.label = label;
    role.defaultRole = true;
    role.privilegeIds = privilegeIds;
    return role;
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return elementId;
  }

  @Override
  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isDefaultRole() {
    return defaultRole;
  }

  public void setDefaultRole(boolean defaultRole) {
    this.defaultRole = defaultRole;
  }

  public List<String> getPrivilegeIds() {
    return privilegeIds;
  }

  public void setPrivilegeIds(List<String> privilegeIds) {
    this.privilegeIds = privilegeIds;
  }
}
