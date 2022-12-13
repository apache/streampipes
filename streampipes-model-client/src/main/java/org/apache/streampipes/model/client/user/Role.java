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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@TsModel
public enum Role {
  ROLE_ADMIN(Constants.ROLE_ADMIN_VALUE),

  ROLE_SERVICE_ADMIN(Constants.ROLE_SERVICE_ADMIN_VALUE),

  ROLE_PIPELINE_ADMIN(
      Constants.ROLE_PIPELINE_ADMIN_VALUE,
      Privilege.PRIVILEGE_READ_PIPELINE,
      Privilege.PRIVILEGE_WRITE_PIPELINE,
      Privilege.PRIVILEGE_DELETE_PIPELINE,
      Privilege.PRIVILEGE_READ_PIPELINE_ELEMENT
  ),

  ROLE_DASHBOARD_ADMIN(
      Constants.ROLE_DASHBOARD_ADMIN_VALUE,
      Privilege.PRIVILEGE_READ_DASHBOARD,
      Privilege.PRIVILEGE_WRITE_DASHBOARD,
      Privilege.PRIVILEGE_DELETE_DASHBOARD
  ),

  ROLE_DATA_EXPLORER_ADMIN(
      Constants.ROLE_DATA_EXPLORER_ADMIN_VALUE,
      Privilege.PRIVILEGE_READ_DATA_EXPLORER_VIEW,
      Privilege.PRIVILEGE_READ_DATA_EXPLORER_WIDGET,
      Privilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
      Privilege.PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET,
      Privilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW,
      Privilege.PRIVILEGE_DELETE_DATA_EXPLORER_WIDGET,
      Privilege.PRIVILEGE_READ_PIPELINE
  ),
  ROLE_CONNECT_ADMIN(
      Constants.ROLE_CONNECT_ADMIN_VALUE,
      Privilege.PRIVILEGE_WRITE_ADAPTER

  ),

  ROLE_DASHBOARD_USER(
      Constants.ROLE_DASHBOARD_USER_VALUE,
      Privilege.PRIVILEGE_READ_DASHBOARD,
      Privilege.PRIVILEGE_READ_DASHBOARD_WIDGET,
      Privilege.PRIVILEGE_READ_PIPELINE
  ),

  ROLE_DATA_EXPLORER_USER(
      Constants.ROLE_DATA_EXPLORER_USER_VALUE,
      Privilege.PRIVILEGE_READ_DATA_EXPLORER_VIEW,
      Privilege.PRIVILEGE_READ_DATA_EXPLORER_WIDGET,
      Privilege.PRIVILEGE_READ_PIPELINE
  ),

  ROLE_PIPELINE_USER(
      Constants.ROLE_PIPELINE_USER_VALUE,
      Privilege.PRIVILEGE_READ_PIPELINE,
      Privilege.PRIVILEGE_READ_PIPELINE_ELEMENT
  ),

  ROLE_APP_USER(Constants.ROLE_APP_USER_VALUE,
      Privilege.PRIVILEGE_READ_PIPELINE,
      Privilege.PRIVILEGE_READ_PIPELINE_ELEMENT),

  ROLE_ASSET_USER(Constants.ROLE_ASSET_USER_VALUE,
      Privilege.PRIVILEGE_READ_GENERIC_STORAGE,
      Privilege.PRIVILEGE_READ_ASSETS),

  ROLE_ASSET_ADMIN(Constants.ROLE_ASSET_ADMIN_VALUE,
      Privilege.PRIVILEGE_READ_ASSETS,
      Privilege.PRIVILEGE_READ_GENERIC_STORAGE,
      Privilege.PRIVILEGE_WRITE_GENERIC_STORAGE,
      Privilege.PRIVILEGE_WRITE_ASSETS);

  private List<Privilege> privileges;
  private String roleString;

  Role(String roleString,
       Privilege... privileges) {
    this.roleString = roleString;
    this.privileges = Arrays.asList(privileges);
  }

  public List<String> getPrivilegesAsString() {
    return this.privileges.stream().map(Enum::name).collect(Collectors.toList());
  }

  public List<Privilege> getPrivileges() {
    return privileges;
  }

  public static final class Constants {
    public static final String ROLE_ADMIN_VALUE = "ROLE_ADMIN";
    public static final String ROLE_SERVICE_ADMIN_VALUE = "ROLE_SERVICE_ADMIN";
    public static final String ROLE_PIPELINE_ADMIN_VALUE = "ROLE_PIPELINE_ADMIN";
    public static final String ROLE_DASHBOARD_ADMIN_VALUE = "ROLE_DASHBOARD_ADMIN";
    public static final String ROLE_DATA_EXPLORER_ADMIN_VALUE = "ROLE_DATA_EXPLORER_ADMIN";
    public static final String ROLE_CONNECT_ADMIN_VALUE = "ROLE_CONNECT_ADMIN";
    public static final String ROLE_DASHBOARD_USER_VALUE = "ROLE_DASHBOARD_USER";
    public static final String ROLE_DATA_EXPLORER_USER_VALUE = "ROLE_DATA_EXPLORER_USER";
    public static final String ROLE_PIPELINE_USER_VALUE = "ROLE_PIPELINE_USER";
    public static final String ROLE_APP_USER_VALUE = "ROLE_APP_USER";
    public static final String ROLE_ASSET_USER_VALUE = "ROLE_ASSET_USER";
    public static final String ROLE_ASSET_ADMIN_VALUE = "ROLE_ASSET_ADMIN";
  }
}
