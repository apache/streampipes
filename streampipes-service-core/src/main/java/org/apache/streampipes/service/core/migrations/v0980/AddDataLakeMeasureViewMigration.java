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

package org.apache.streampipes.service.core.migrations.v0980;

import org.apache.streampipes.manager.setup.tasks.AddDataLakeMeasureViewTask;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.NoDocumentException;

import java.io.IOException;

public class AddDataLakeMeasureViewMigration implements Migration {

  @Override
  public boolean shouldExecute() {
    try {
      var designDoc = Utils
          .getCouchDbGsonClient(Utils.DATA_LAKE_DB_NAME)
          .design()
          .getFromDb(AddDataLakeMeasureViewTask.MEASUREMENT_BY_NAME_VIEW);
      var viewKey = AddDataLakeMeasureViewTask.VIEW_NAME;
      var viewMapFunction = AddDataLakeMeasureViewTask.MAP_FUNCTION;
      var views = designDoc.getViews();

      if (views.containsKey(viewKey)) {
        return !(views.get(viewKey).getMap().equals(viewMapFunction));
      } else {
        return true;
      }
    } catch (NoDocumentException e) {
      return true;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    new AddDataLakeMeasureViewTask().execute();
  }

  @Override
  public String getDescription() {
    return "Creating view for data lake measure database";
  }
}
