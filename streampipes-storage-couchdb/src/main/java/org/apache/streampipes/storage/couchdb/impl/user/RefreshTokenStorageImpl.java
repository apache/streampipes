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
package org.apache.streampipes.storage.couchdb.impl.user;

import org.apache.streampipes.model.client.user.RefreshToken;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.impl.core.DefaultViewCrudStorage;

import java.util.List;

public class RefreshTokenStorageImpl extends DefaultViewCrudStorage<RefreshToken>
    implements IRefreshTokenStorage {

  private static final String REFRESH_TOKEN_BY_HASH_VIEW = "users/refresh-token-by-hash";
  private static final String REFRESH_TOKEN_BY_PRINCIPAL_ID_VIEW = "users/refresh-token-by-user";

  public RefreshTokenStorageImpl(CouchDbStores stores) {
    super(
        stores.principal(CouchDbStores.USER_DB_NAME),
        RefreshToken.class,
        REFRESH_TOKEN_BY_HASH_VIEW
    );
  }

  @Override
  public RefreshToken findByHashedToken(String hashedToken) {
    return view(REFRESH_TOKEN_BY_HASH_VIEW, RefreshToken.class)
        .key(hashedToken)
        .docs()
        .stream()
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<RefreshToken> findByPrincipalId(String principalId) {
    return view(REFRESH_TOKEN_BY_PRINCIPAL_ID_VIEW, RefreshToken.class)
        .key(principalId)
        .docs();
  }
}
