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

import org.apache.streampipes.manager.setup.tasks.AddAssetManagementViewTask;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.NoDocumentException;

import java.io.IOException;

public class AddAssetManagementViewMigration implements Migration {

  @Override
  public boolean shouldExecute() {
    var client = Utils.getCouchDbClient("genericstorage", true);
    try {
      var designDoc = client.design().getFromDb(AddAssetManagementViewTask.DESIGN_DOCUMENT);

      return designDoc == null || designDoc.getViews().containsKey(AddAssetManagementViewTask.VIEW_NAME);
    } catch (NoDocumentException e) {
      return true;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    new AddAssetManagementViewTask().execute();
  }

  @Override
  public String getDescription() {
    return "Adding design document and view for managing assets";
  }
}
