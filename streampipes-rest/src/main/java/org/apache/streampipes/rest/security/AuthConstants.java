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
package org.apache.streampipes.rest.security;

import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_ADAPTER_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_ASSETS_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_FILES_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_GENERIC_STORAGE_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_ASSETS_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_DASHBOARD_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_GENERIC_STORAGE_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE;
import static org.apache.streampipes.model.client.user.DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_ADMIN_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_DASHBOARD_ADMIN_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_DASHBOARD_USER_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_PIPELINE_ADMIN_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_PIPELINE_USER_VALUE;
import static org.apache.streampipes.model.client.user.DefaultRole.Constants.ROLE_SERVICE_ADMIN_VALUE;

public class AuthConstants {

  private static final String HAS_ANY_AUTHORITY = "hasAnyAuthority('";
  private static final String HAS_ANY_ROLE = "hasAnyRole('";
  private static final String Q = "'";
  private static final String BS = "(";
  private static final String BE = ")";
  private static final String BE2 = "))";
  private static final String OR = " or ";

  public static final String IS_ADMIN_ROLE =
      HAS_ANY_AUTHORITY + ROLE_ADMIN_VALUE + Q + ", '" + ROLE_SERVICE_ADMIN_VALUE + Q + BE;

  public static final String IS_PIPELINE_ADMIN_ROLE = HAS_ANY_ROLE + ROLE_PIPELINE_ADMIN_VALUE + Q + BE;
  public static final String IS_PIPELINE_USER_ROLE = HAS_ANY_ROLE + ROLE_PIPELINE_USER_VALUE + Q + BE;

  public static final String IS_DASHBOARD_ADMIN_ROLE = HAS_ANY_ROLE + ROLE_DASHBOARD_ADMIN_VALUE + Q + BE;
  public static final String IS_DASHBOARD_USER_ROLE = HAS_ANY_ROLE + ROLE_DASHBOARD_USER_VALUE + Q + BE;

  public static final String HAS_READ_PIPELINE_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_PIPELINE_VALUE + Q + BE2;
  public static final String HAS_WRITE_PIPELINE_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_PIPELINE_VALUE + Q + BE2;

  public static final String HAS_READ_PIPELINE_ELEMENT_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE + Q + BE2;

  public static final String HAS_WRITE_PIPELINE_ELEMENT_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE + Q + BE2;

  public static final String HAS_WRITE_ADAPTER_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_ADAPTER_VALUE + Q + BE2;
  public static final String HAS_READ_ADAPTER_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_ADAPTER_VALUE + Q + BE2;

  public static final String HAS_WRITE_DATA_EXPLORER_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE + Q + BE2;
  public static final String HAS_READ_DATA_EXPLORER_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE + Q + BE2;

  public static final String HAS_WRITE_DASHBOARD_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_DASHBOARD_VALUE + Q + BE2;
  public static final String HAS_READ_DASHBOARD_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_DASHBOARD_VALUE + Q + BE2;

  public static final String HAS_READ_FILE_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_FILES_VALUE + Q + BE2;

  public static final String HAS_READ_ASSETS_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_ASSETS_VALUE + Q + BE2;
  public static final String HAS_WRITE_ASSETS_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_ASSETS_VALUE + Q + BE2;

  public static final String HAS_READ_GENERIC_STORAGE_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_READ_GENERIC_STORAGE_VALUE + Q + BE2;
  public static final String HAS_WRITE_GENERIC_STORAGE_PRIVILEGE =
      BS + IS_ADMIN_ROLE + OR + HAS_ANY_AUTHORITY + PRIVILEGE_WRITE_GENERIC_STORAGE_VALUE + Q + BE2;

  public static final String IS_AUTHENTICATED = "isAuthenticated()";

}
