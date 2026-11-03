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
package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.design.ViewDefinition;
import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import java.io.IOException;
import java.util.Map;

public class AddRefreshTokenViewsMigration implements Migration {

  private static final String DOC_NAME = "_design/users";

  private final CouchDbStores stores;

  public AddRefreshTokenViewsMigration(CouchDbStores stores) {
    this.stores = stores;
  }

  @Override
  public boolean shouldExecute() {
    var views = stores.database(CouchDbStores.USER_DB_NAME).design().get(DOC_NAME)
        .map(DesignDocument::views)
        .orElse(Map.of());

    return !containsView(
        views,
        UserDesignDocument.REFRESH_TOKEN_BY_HASH_KEY,
        UserDesignDocument.REFRESH_TOKEN_BY_HASH_MAP_FUNCTION
    ) || !containsView(
        views,
        UserDesignDocument.REFRESH_TOKEN_BY_USER_KEY,
        UserDesignDocument.REFRESH_TOKEN_BY_USER_MAP_FUNCTION
    );
  }

  @Override
  public void executeMigration() throws IOException {
    stores.database(CouchDbStores.USER_DB_NAME).design().sync(new UserDesignDocument().make());
  }

  @Override
  public String getDescription() {
    return "Add refresh token views to user database";
  }

  private boolean containsView(Map<String, ViewDefinition> views,
                               String viewKey,
                               String mapFunction) {
    return views.containsKey(viewKey) && mapFunction.equals(views.get(viewKey).map());
  }
}
