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

import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.manager.setup.tasks.AddAssetManagementViewTask;
import org.apache.streampipes.manager.setup.tasks.AddDataLakeMeasureViewTask;
import org.apache.streampipes.manager.setup.tasks.AddDefaultPipelineTemplatesTask;
import org.apache.streampipes.manager.setup.tasks.AddFunctionStateViewTask;
import org.apache.streampipes.manager.setup.tasks.AddScriptTemplateViewTask;
import org.apache.streampipes.manager.setup.tasks.CreateAssetLinkTypeTask;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;
import org.lightcouch.Response;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.manager.setup.design.DesignDocumentUtils.prepareDocument;

public class CouchDbInstallationStep extends InstallationStep {

  private static final String PREPARING_USERS_TEXT = "Preparing database 'users'...";

  public CouchDbInstallationStep() {

  }

  @Override
  public void install() {
    createDatabases();
    createViews();
    new CreateAssetLinkTypeTask().execute();
    new AddDefaultPipelineTemplatesTask(
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineTemplateStorage()
    ).execute();
  }

  @Override
  public String getTitle() {
    return "Creating databases...";
  }

  private void createDatabases() {
    try {
      // Set up streampipes internal databases
      Utils.getCouchDbUserClient();
      Utils.getCouchDbPipelineClient();

      logSuccess(getTitle());
    } catch (Exception e) {
      logFailure(getTitle(), e);
    }
  }

  private void createViews() {
    addUserView();
    addPipelineView();
    new AddDataLakeMeasureViewTask().execute();
    new AddAssetManagementViewTask().execute();
    new AddScriptTemplateViewTask().execute();
    new AddFunctionStateViewTask().execute();
  }

  private void addPipelineView() {
    DesignDocument pipelineDocument = prepareDocument("_design/adapters");
    DesignDocument allPipelinesDocument = prepareDocument("_design/pipelines");
    Map<String, MapReduce> adapterViews = new HashMap<>();
    Map<String, MapReduce> pipelineViews = new HashMap<>();

    MapReduce adapterFunction = new MapReduce();
    adapterFunction.setMap("function (doc) {\n"
        + "  for(var i = 0; i < doc.streams.length; i++) {\n"
        + "    var stream = doc.streams[i];\n"
        + "    if (stream.correspondingAdapterId) {\n"
        + "      emit(stream.correspondingAdapterId, doc._id);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    adapterViews.put("used-adapters", adapterFunction);
    pipelineDocument.setViews(adapterViews);
    Utils.getCouchDbPipelineClient().design().synchronizeWithDb(pipelineDocument);


    MapReduce allPipelinesFunction = new MapReduce();
    allPipelinesFunction.setMap("function (doc) {\n"
        + "  emit(doc._id, doc);\n"
        + "}");
    pipelineViews.put("all", allPipelinesFunction);
    allPipelinesDocument.setViews(pipelineViews);
    Utils.getCouchDbPipelineClient().design().synchronizeWithDb(allPipelinesDocument);
  }

  private void addUserView() {
    try {
      var userDocument = new UserDesignDocument().make();
      Response resp = Utils.getCouchDbUserClient().design().synchronizeWithDb(userDocument);

      if (resp.getError() != null) {
        logFailure(PREPARING_USERS_TEXT);
      } else {
        logSuccess(PREPARING_USERS_TEXT);
      }
    } catch (Exception e) {
      logFailure(PREPARING_USERS_TEXT, e);
    }
  }
}
