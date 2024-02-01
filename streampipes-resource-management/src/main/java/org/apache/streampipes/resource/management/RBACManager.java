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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.storage.couchdb.utils.Utils;

import io.github.java_casbin.couchdb.CouchDBAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class RBACManager {
  private static final Logger log = LoggerFactory.getLogger(RBACManager.class);
  public static RBACManager INSTANCE = new RBACManager();
  private static final String DEFAULT_DOMAIN = "default";
  public static final String USER_PREFIX = "user_";
  public static final String GROUP_PREFIX = "group_";
  public static final String SERVICE_COUNT_PREFIX = "service-account_";
  private static final String OBJECT_PREFIX = "obj_";
  public static final String ALL_PERMISSION = "*";
  public static final String READ_PERMISSION = "READ";
  public static final String WRITE_PERMISSION = "WRITE";
  public static final String DELETE_PERMISSION = "DELETE";
  public static final String OWNER_PERMISSION = "OWNER";
  public static final String PUBLIC_USER = "public";
  private static final int INDEX_OF_USER = 0;
  private static final int INDEX_OF_OBJECT = 2;
  final String modelDefinition = """
      [request_definition]
      r = sub, dom, obj, act

      [policy_definition]
      p = sub, dom, obj, act

      [role_definition]
      g = _, _, _

      [policy_effect]
      e = some(where (p.eft == allow))

      [matchers]
      m = g(r.sub, p.sub, r.dom) && r.dom == p.dom && r.obj == p.obj && (r.act == p.act || p.act == "*" || p.act == "OWNER")""";
  private final Enforcer enforcer;

  public RBACManager() {
    Model model = new Model();
    model.loadModelFromText(modelDefinition);
    enforcer = new Enforcer(model, new CouchDBAdapter(Utils.getCouchDbUserClient(), "policies"));
  }

  public void addPermissionForUser(String userId, String objectInstanceId, String permission) {
    log.info("Adding permission for user {} on object {} with permission {}", userId, objectInstanceId, permission);
    enforcer.addPermissionForUser(getUserId(userId), DEFAULT_DOMAIN,
        getObjectId(objectInstanceId), permission);
  }

  public void addPermissionForUserGroup(String groupId, String objectInstanceId, String permission) {
    log.info("Adding permission for group {} on object {} with permission {}", groupId, objectInstanceId, permission);
    enforcer.addPermissionForUser(getUserGroupID(groupId), DEFAULT_DOMAIN,
        getObjectId(objectInstanceId), permission);
  }

  public void addPermissionForServiceAccount(String saID, String objectInstanceId, String permission) {
    log.info("Adding permission for service account {} on object {} with permission {}", saID, objectInstanceId, permission);
    enforcer.addPermissionForUser(getServiceAccountID(saID), DEFAULT_DOMAIN,
        getObjectId(objectInstanceId), permission);
  }

  public void addUserToUserGroup(String userID, String groupID) {
    log.info("Adding user {} to group {}", userID, groupID);
    enforcer.addRoleForUserInDomain(getUserId(userID), getUserGroupID(groupID), DEFAULT_DOMAIN);
  }

  public void removeUserFromAllUserGroup(String userID) {
    log.info("Removing user {} from all groups", userID);
    enforcer.getRolesForUserInDomain(userID, DEFAULT_DOMAIN).forEach(g -> {
      if (g.startsWith(GROUP_PREFIX)) {
        enforcer.deleteRoleForUserInDomain(userID, g, DEFAULT_DOMAIN);
      }
    });
  }

  public boolean hasPermissionForUser(String userId, String objectInstanceId, String permission) {
    boolean result = enforcer.enforce(getUserId(userId), DEFAULT_DOMAIN, getObjectId(objectInstanceId), permission) ||
        enforcer.enforce(getUserId(PUBLIC_USER), DEFAULT_DOMAIN, getObjectId(objectInstanceId), permission);
    log.info("Checking permission for user {} on object {} with permission {}: {}", userId, objectInstanceId,
        permission, result);
    return result;
  }

  public void deleteObject(String objectInstanceId) {
    log.info("Deleting object {}", objectInstanceId);
    enforcer.removeFilteredPolicy(INDEX_OF_OBJECT, getObjectId(objectInstanceId));
  }

  /**
   * Due to the bug in jcasbin, we shouldn't modify the content in the result
   * @param objectInstanceId
   * @return
   */
  public List<List<String>> getRawPoliciesForObject(String objectInstanceId) {
    log.info("Getting permissions for object {}", objectInstanceId);
    return enforcer.getFilteredPolicy(0, "", DEFAULT_DOMAIN, getObjectId(objectInstanceId));
  }

  public void setOwner(String userId, String objectInstanceId) {
    enforcer.removeFilteredPolicy(INDEX_OF_USER, "", DEFAULT_DOMAIN, getObjectId(objectInstanceId), OWNER_PERMISSION);
    enforcer.addPermissionForUser(getUserId(userId), DEFAULT_DOMAIN, getObjectId(objectInstanceId), OWNER_PERMISSION);
  }

  public String getOwner(String objectInstanceId) {
    List<List<String>> p =
        enforcer.getFilteredPolicy(INDEX_OF_USER, "", DEFAULT_DOMAIN, getObjectId(objectInstanceId), OWNER_PERMISSION);
    if (p.isEmpty()) {
      return null;
    }
    if (p.size() > 1) {
      throw new IllegalStateException("More than one owner for object " + objectInstanceId);
    }
    return p.get(0).get(0).replaceFirst(USER_PREFIX, "");
  }

  private static String getUserId(String sid) {
    return USER_PREFIX + sid;
  }

  private static String getUserGroupID(String sid) {
    return GROUP_PREFIX + sid;
  }

  private static String getServiceAccountID(String sid) {
    return SERVICE_COUNT_PREFIX + sid;
  }

  private static String getObjectId(String oid) {
    return OBJECT_PREFIX + oid;
  }
}
