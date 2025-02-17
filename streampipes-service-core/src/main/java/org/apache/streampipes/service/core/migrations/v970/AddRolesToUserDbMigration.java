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

package org.apache.streampipes.service.core.migrations.v970;

import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AddRolesToUserDbMigration implements Migration {

  private static final String DOC_NAME = "_design/users";
  private static final Logger LOG = LoggerFactory.getLogger(AddRolesToUserDbMigration.class);

  @Override
  public boolean shouldExecute() {
    var designDoc = Utils.getCouchDbUserClient().design().getFromDb(DOC_NAME);
    var viewKey = UserDesignDocument.ROLE_KEY;
    var viewMapFunction = UserDesignDocument.ROLE_MAP_FUNCTION;
    var views = designDoc.getViews();

    if (views.containsKey(viewKey)) {
      return !(views.get(viewKey).getMap().equals(viewMapFunction));
    } else {
      return true;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    var userDocument = new UserDesignDocument().make();
    Response resp = Utils.getCouchDbUserClient().design().synchronizeWithDb(userDocument);

    if (resp.getError() != null) {
      LOG.warn("Could not update user design document with reason {}", resp.getReason());
    }
  }

  @Override
  public String getDescription() {
    return "Add default roles and privileges";
  }
}
