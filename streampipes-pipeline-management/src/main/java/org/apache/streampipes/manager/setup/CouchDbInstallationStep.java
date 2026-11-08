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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.manager.setup.tasks.AddAssetManagementViewTask;
import org.apache.streampipes.manager.setup.tasks.AddDatasetMetadataViewTask;
import org.apache.streampipes.manager.setup.tasks.AddDefaultPipelineTemplatesTask;
import org.apache.streampipes.manager.setup.tasks.AddFunctionStateViewTask;
import org.apache.streampipes.manager.setup.tasks.AddScriptTemplateViewTask;
import org.apache.streampipes.manager.setup.tasks.CreateAssetLinkTypeTask;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

public class CouchDbInstallationStep extends InstallationStep {

  private static final String PREPARING_USERS_TEXT = "Preparing database 'users'...";
  private static final String PIPELINE_DB_NAME = "pipeline";

  private final CouchDbStores stores;
  private final IGenericStorage genericStorage;
  private final ICompactPipelineTemplateStorage pipelineTemplateStorage;

  public CouchDbInstallationStep(CouchDbStores stores,
                                  IGenericStorage genericStorage,
                                  ICompactPipelineTemplateStorage pipelineTemplateStorage) {
    this.stores = stores;
    this.genericStorage = genericStorage;
    this.pipelineTemplateStorage = pipelineTemplateStorage;
  }

  @Override
  public void install() {
    createDatabases();
    createViews();
    new CreateAssetLinkTypeTask(genericStorage).execute();
    new AddDefaultPipelineTemplatesTask(
        pipelineTemplateStorage
    ).execute();
  }

  @Override
  public String getTitle() {
    return "Creating databases...";
  }

  private void createDatabases() {
    try {
      // Set up streampipes internal databases
      stores.database(CouchDbStores.USER_DB_NAME).create();
      stores.database(PIPELINE_DB_NAME).create();

      logSuccess(getTitle());
    } catch (Exception e) {
      logFailure(getTitle(), e);
    }
  }

  private void createViews() {
    addUserView();
    addPipelineView();
    new AddDatasetMetadataViewTask(stores).execute();
    new AddAssetManagementViewTask(stores).execute();
    new AddScriptTemplateViewTask(stores).execute();
    new AddFunctionStateViewTask(stores).execute();
  }

  private void addPipelineView() {
    var designDocuments = stores.database(PIPELINE_DB_NAME).design();
    designDocuments.sync(DesignDocument.named("_design/adapters").withView("used-adapters",
        "function (doc) {\n"
            + "  for(var i = 0; i < doc.streams.length; i++) {\n"
            + "    var stream = doc.streams[i];\n"
            + "    if (stream.correspondingAdapterId) {\n"
            + "      emit(stream.correspondingAdapterId, doc._id);\n"
            + "    }\n"
            + "  }\n"
            + "}"));
    designDocuments.sync(DesignDocument.named("_design/pipelines").withView("all",
        "function (doc) {\n"
            + "  emit(doc._id, doc);\n"
            + "}"));
  }

  private void addUserView() {
    try {
      stores.database(CouchDbStores.USER_DB_NAME).design().sync(new UserDesignDocument().make());
      logSuccess(PREPARING_USERS_TEXT);
    } catch (Exception e) {
      logFailure(PREPARING_USERS_TEXT, e);
    }
  }
}
