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

import org.apache.streampipes.couchdb.client.design.DesignDocument;

public class UserDesignDocument {

  public static final String USERNAME_KEY = "username";
  public static final String USERNAME_MAP_FUNCTION =
      "function(doc) { if(doc.properties.username) { emit(doc.properties.username.toLowerCase(), doc); } }";

  public static final String ROLE_KEY = "role";
  public static final String ROLE_MAP_FUNCTION = "function(doc) { if(doc.$type === 'role') { emit(doc._id, doc); } }";

  public static final String PRIVILEGE_MAP_FUNCTION =
      "function(doc) { if(doc.$type === 'privilege') { emit(doc._id, doc); } }";

  public static final String REFRESH_TOKEN_BY_HASH_KEY = "refresh-token-by-hash";
  public static final String REFRESH_TOKEN_BY_HASH_MAP_FUNCTION =
      "function(doc) { if (doc.$type === 'refresh-token' && doc.hashedToken) { emit(doc.hashedToken, doc); } }";

  public static final String REFRESH_TOKEN_BY_USER_KEY = "refresh-token-by-user";
  public static final String REFRESH_TOKEN_BY_USER_MAP_FUNCTION =
      "function(doc) { if (doc.$type === 'refresh-token' && doc.principalId) { emit(doc.principalId, doc); } }";

  public DesignDocument make() {
    return DesignDocument.named("_design/users")
        .withView("password",
            "function(doc) { if(doc.properties.username && doc.properties.principalType === 'USER_ACCOUNT' && "
                + "doc.properties.password) { emit(doc.properties.username.toLowerCase(), doc.properties.password); } }")
        .withView(USERNAME_KEY, USERNAME_MAP_FUNCTION)
        .withView("groups", "function(doc) { if(doc.$type === 'group') { emit(doc._id, doc); } }")
        .withView("permissions", "function(doc) { if(doc.$type === 'permission') { emit(doc._id, doc); } }")
        .withView("token",
            "function(doc) { if (doc.properties.userApiTokens) { doc.properties.userApiTokens.forEach(function(token) "
                + "{ emit(token.properties.hashedToken, doc.properties.email); });}}")
        .withView("userpermissions",
            "function(doc) { if (doc.$type === 'permission') {emit(doc.ownerSid, doc); for(var i = 0; "
                + "i < doc.grantedAuthorities.length; i++) {emit(doc.grantedAuthorities[i].sid,doc)}}}")
        .withView("objectpermissions",
            "function(doc) { if (doc.$type === 'permission') {emit(doc.objectInstanceId, doc);}}")
        .withView("user-activation", "function(doc) { if (doc.$type === 'user-activation') {emit(doc._id, doc);}}")
        .withView("password-recovery",
            "function(doc) { if (doc.$type === 'password-recovery') {emit(doc._id, doc);}}")
        .withView(ROLE_KEY, ROLE_MAP_FUNCTION)
        .withView("privilege", PRIVILEGE_MAP_FUNCTION)
        .withView(REFRESH_TOKEN_BY_HASH_KEY, REFRESH_TOKEN_BY_HASH_MAP_FUNCTION)
        .withView(REFRESH_TOKEN_BY_USER_KEY, REFRESH_TOKEN_BY_USER_MAP_FUNCTION);
  }
}
