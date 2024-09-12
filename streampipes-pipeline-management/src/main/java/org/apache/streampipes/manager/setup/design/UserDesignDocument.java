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

package org.apache.streampipes.manager.setup.design;

import org.lightcouch.DesignDocument;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.manager.setup.design.DesignDocumentUtils.prepareDocument;

public class UserDesignDocument {

  public static final String USERNAME_KEY = "username";
  public static final String USERNAME_MAP_FUNCTION =
      "function(doc) { if(doc.properties.username) { emit(doc.properties.username.toLowerCase(), doc); } }";

  public static final String ROLE_KEY = "role";
  public static final String ROLE_MAP_FUNCTION = "function(doc) { if(doc.$type === 'role') { emit(doc._id, doc); } }";

  public static final String PRIVILEGE_MAP_FUNCTION =
      "function(doc) { if(doc.$type === 'privilege') { emit(doc._id, doc); } }";

  public DesignDocument make() {
    DesignDocument userDocument = prepareDocument("_design/users");
    Map<String, DesignDocument.MapReduce> views = new HashMap<>();

    DesignDocument.MapReduce passwordFunction = new DesignDocument.MapReduce();
    passwordFunction.setMap(
        "function(doc) { if(doc.properties.username && doc.properties.principalType === 'USER_ACCOUNT' && "
            + "doc.properties.password) { emit(doc.properties.username.toLowerCase(), doc.properties.password); } }");

    DesignDocument.MapReduce usernameFunction = new DesignDocument.MapReduce();
    usernameFunction.setMap(USERNAME_MAP_FUNCTION);

    DesignDocument.MapReduce permissionFunction = new DesignDocument.MapReduce();
    permissionFunction.setMap("function(doc) { if(doc.$type === 'permission') { emit(doc._id, doc); } }");

    DesignDocument.MapReduce groupFunction = new DesignDocument.MapReduce();
    groupFunction.setMap("function(doc) { if(doc.$type === 'group') { emit(doc._id, doc); } }");

    DesignDocument.MapReduce tokenFunction = new DesignDocument.MapReduce();
    tokenFunction.setMap(
        "function(doc) { if (doc.properties.userApiTokens) { doc.properties.userApiTokens.forEach(function(token) "
            + "{ emit(token.properties.hashedToken, doc.properties.email); });}}");

    DesignDocument.MapReduce userPermissionFunction = new DesignDocument.MapReduce();
    userPermissionFunction.setMap(
        "function(doc) { if (doc.$type === 'permission') {emit(doc.ownerSid, doc); for(var i = 0; "
            + "i < doc.grantedAuthorities.length; i++) {emit(doc.grantedAuthorities[i].sid,doc)}}}");

    DesignDocument.MapReduce objectPermissionFunction = new DesignDocument.MapReduce();
    objectPermissionFunction.setMap(
        "function(doc) { if (doc.$type === 'permission') {emit(doc.objectInstanceId, doc);}}");

    DesignDocument.MapReduce userActivationFunction = new DesignDocument.MapReduce();
    userActivationFunction.setMap("function(doc) { if (doc.$type === 'user-activation') {emit(doc._id, doc);}}");

    DesignDocument.MapReduce passwordRecoveryFunction = new DesignDocument.MapReduce();
    passwordRecoveryFunction.setMap("function(doc) { if (doc.$type === 'password-recovery') {emit(doc._id, doc);}}");

    DesignDocument.MapReduce roleFunction = new DesignDocument.MapReduce();
    roleFunction.setMap(ROLE_MAP_FUNCTION);

    DesignDocument.MapReduce privilegeFunction = new DesignDocument.MapReduce();
    privilegeFunction.setMap(PRIVILEGE_MAP_FUNCTION);

    views.put("password", passwordFunction);
    views.put(USERNAME_KEY, usernameFunction);
    views.put("groups", groupFunction);
    views.put("permissions", permissionFunction);
    views.put("token", tokenFunction);
    views.put("userpermissions", userPermissionFunction);
    views.put("objectpermissions", objectPermissionFunction);
    views.put("user-activation", userActivationFunction);
    views.put("password-recovery", passwordRecoveryFunction);
    views.put(ROLE_KEY, roleFunction);
    views.put("privilege", privilegeFunction);

    userDocument.setViews(views);

    return userDocument;
  }
}
