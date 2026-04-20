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

package org.apache.streampipes.service.core.migrations.templates;

import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.NoDocumentException;

public abstract class AddGenericStorageViewMigration implements Migration {

  @Override
  public boolean shouldExecute() {
    var client = Utils.getCouchDbClient("genericstorage", true);
    try {
      var designDoc = client.design().getFromDb(getDesignDocumentName());

      return designDoc == null || !designDoc.getViews().containsKey(getViewName());
    } catch (NoDocumentException e) {
      return true;
    }
  }

  @Override
  public String getDescription() {
    return String.format(
        "Adding design document and view for design document %s, view %s",
        getDesignDocumentName(),
        getViewName()
    );
  }

  public abstract String getDesignDocumentName();

  public abstract String getViewName();
}
