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

package org.apache.streampipes.storage.couchdb.utils;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.apache.streampipes.serializers.json.GsonSerializer;

public class Utils {

  public static CouchDbClient getCouchDbPipelineElementTemplateClient() {
    CouchDbClient dbClient = new CouchDbClient(props("pipelineelementtemplate"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbCategoryClient() {
    CouchDbClient dbClient = new CouchDbClient(props("category"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbLabelClient() {
    CouchDbClient dbClient = new CouchDbClient(props("label"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbConnectWorkerContainerClient() {
    CouchDbClient dbClient = new CouchDbClient(props("connectworkercontainer"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbFileMetadataClient() {
    CouchDbClient dbClient = new CouchDbClient(props("filemetadata"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbAdapterTemplateClient() {
    CouchDbClient dbClient = new CouchDbClient(props("adaptertemplate"));
    dbClient.setGsonBuilder(GsonSerializer.getAdapterGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbAssetDashboardClient() {
    CouchDbClient dbClient = new CouchDbClient(props("assetdashboard"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbAdapterClient() {
    CouchDbClient dbClient = new CouchDbClient(props("adapter"));
    dbClient.setGsonBuilder(GsonSerializer.getAdapterGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbPipelineClient() {
    CouchDbClient dbClient = new CouchDbClient(props("pipeline"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbSepaInvocationClient() {
    CouchDbClient dbClient = new CouchDbClient(props("invocation"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbConnectionClient() {
    CouchDbClient dbClient = new CouchDbClient(props("connection"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbVisualizationClient() {
    CouchDbClient dbClient = new CouchDbClient(props("visualizations"));
    return dbClient;
  }

  //TODO: Remove??
  public static CouchDbClient getCouchDbRdfEndpointClient() {
    CouchDbClient dbClient = new CouchDbClient(props("rdfendpoint"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbVisualizablePipelineClient() {
    CouchDbClient dbClient = new CouchDbClient(props("visualizablepipeline"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbDataExplorerDashboardClient() {
    CouchDbClient dbClient = new CouchDbClient(props("dataexplorerdashboard"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }


  public static CouchDbClient getCouchDbDataExplorerWidgetClient() {
    CouchDbClient dbClient = new CouchDbClient(props("dataexplorerwidget"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }


  public static CouchDbClient getCouchDbDashboardClient() {
    CouchDbClient dbClient = new CouchDbClient(props("dashboard"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbDashboardWidgetClient() {
    CouchDbClient dbClient = new CouchDbClient(props("dashboardwidget"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbUserClient() {
    CouchDbClient dbClient = new CouchDbClient(props("users"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbNodeClient() {
    CouchDbClient dbClient = new CouchDbClient(props("nodes"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCouchDbInternalUsersClient() {
    CouchDbClient dbClient = new CouchDbClient(props("_users"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbReplicatorClient() {
    CouchDbClient dbClient = new CouchDbClient(props("_replicator"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbGlobalChangesClient() {
    CouchDbClient dbClient = new CouchDbClient(props("_global_changes"));
    return dbClient;
  }


  public static CouchDbClient getCouchDbMonitoringClient() {
    CouchDbClient dbClient = new CouchDbClient(props("monitoring"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbNotificationClient() {
    return new CouchDbClient(props("notification"));
  }

  public static CouchDbClient getCouchDbPipelineCategoriesClient() {
    return new CouchDbClient(props("pipelinecategories"));
  }

  public static CouchDbClient getCouchDbElasticsearchFilesEndppointClient() {
    return new CouchDbClient(props("file-export-endpoints-elasticsearch"));
  }

  public static CouchDbClient getCouchDbDataLakeClient() {
    CouchDbClient dbClient = new CouchDbClient(props("data-lake"));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  public static CouchDbClient getCoucbDbClient(String table) {
    return new CouchDbClient(props(table));
  }

  private static CouchDbProperties props(String dbname) {
    return new CouchDbProperties(dbname, true, CouchDbConfig.INSTANCE.getProtocol(),
            CouchDbConfig.INSTANCE.getHost(), CouchDbConfig.INSTANCE.getPort(), null, null);
  }
}
