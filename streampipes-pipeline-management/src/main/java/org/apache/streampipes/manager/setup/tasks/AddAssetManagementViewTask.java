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

package org.apache.streampipes.manager.setup.tasks;

import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants;

public class AddAssetManagementViewTask implements InstallationTask {
  public static final String DESIGN_DOCUMENT = "_design/assets";

  public static final String VIEW_NAME = "all-assets";
  public static final String MAP_FUNCTION = "function(doc) { if(doc.appDocType === 'asset-management') { emit(doc._id, doc); } }";

  private final CouchDbStores stores;

  public AddAssetManagementViewTask(CouchDbStores stores) {
    this.stores = stores;
  }

  @Override
  public void execute() {
    stores.database(GenericCouchDbConstants.DB_NAME).design()
        .sync(DesignDocument.named(DESIGN_DOCUMENT).withView(VIEW_NAME, MAP_FUNCTION));
  }
}
