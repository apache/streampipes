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

package org.apache.streampipes.user.management.authorization;

import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.client.user.Privilege;

import java.util.List;

public class PrivilegeManager {

  public List<Privilege> makeDefaultPrivileges() {
    return List.of(
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_DASHBOARD_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_WIDGET_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_DASHBOARD_WIDGET_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_WIDGET_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_NOTIFICATIONS_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_FILES_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_FILES_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_ASSETS_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ASSETS_VALUE),

        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_READ_GENERIC_STORAGE_VALUE),
        Privilege.create(DefaultPrivilege.Constants.PRIVILEGE_WRITE_GENERIC_STORAGE_VALUE)
    );
  }
}
