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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.storage.api.user.IRoleStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleManagerTest {

  @Test
  void getPrivilegesReturnsRoleIdAndConfiguredPrivileges() {
    var role = Role.createDefaultRole("ROLE_DASHBOARD_USER", "Dashboard User",
        List.of("PRIVILEGE_READ_DASHBOARD", "PRIVILEGE_READ_PIPELINE"));
    var manager = new RoleManager(singleRoleStorage(role));

    var authorities = manager.getPrivileges("ROLE_DASHBOARD_USER");

    assertTrue(authorities.contains("ROLE_DASHBOARD_USER"));
    assertTrue(authorities.contains("PRIVILEGE_READ_DASHBOARD"));
    assertTrue(authorities.contains("PRIVILEGE_READ_PIPELINE"));
  }

  @Test
  void getPrivilegesDropsPrivilegesInTheRoleNamespace() {
    // A privilege whose id is a role authority must never be granted: this is the
    // escalation primitive from the RoleResource/PrivilegeResource authorization gap.
    var role = Role.createDefaultRole("ROLE_DASHBOARD_USER", "Dashboard User",
        List.of("PRIVILEGE_READ_DASHBOARD", DefaultRole.Constants.ROLE_ADMIN_VALUE));
    var manager = new RoleManager(singleRoleStorage(role));

    var authorities = manager.getPrivileges("ROLE_DASHBOARD_USER");

    assertFalse(authorities.contains(DefaultRole.Constants.ROLE_ADMIN_VALUE),
        "a privilege in the ROLE_ namespace must not become a role authority");
    assertTrue(authorities.contains("PRIVILEGE_READ_DASHBOARD"));
    assertTrue(authorities.contains("ROLE_DASHBOARD_USER"));
    assertEquals(2, authorities.size());
  }

  @Test
  void getPrivilegesReturnsEmptyForUnknownRole() {
    var manager = new RoleManager(singleRoleStorage(null));
    assertTrue(manager.getPrivileges("ROLE_DOES_NOT_EXIST").isEmpty());
  }

  private static IRoleStorage singleRoleStorage(Role role) {
    return new IRoleStorage() {
      @Override
      public Role getElementById(String id) {
        return role != null && id.equals(role.getElementId()) ? role : null;
      }

      @Override
      public List<Role> findAll() {
        return role == null ? List.of() : List.of(role);
      }

      @Override
      public Tuple2<Boolean, String> persist(Role element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Role updateElement(Role element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void deleteElement(Role element) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
