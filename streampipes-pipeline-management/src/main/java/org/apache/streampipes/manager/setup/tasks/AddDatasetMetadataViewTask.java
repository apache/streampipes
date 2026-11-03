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

public class AddDatasetMetadataViewTask implements InstallationTask {

  public static final String MEASUREMENT_BY_NAME_VIEW = "_design/measurement";

  public static final String VIEW_NAME = "by-measure-name";
  public static final String MAP_FUNCTION = "function (doc) { if (doc.measureName) { emit(doc.measureName, doc); } }";

  private final CouchDbStores stores;

  public AddDatasetMetadataViewTask(CouchDbStores stores) {
    this.stores = stores;
  }

  @Override
  public void execute() {
    stores.database(CouchDbStores.DATA_LAKE_DB_NAME).design()
        .sync(DesignDocument.named(MEASUREMENT_BY_NAME_VIEW).withView(VIEW_NAME, MAP_FUNCTION));
  }
}
