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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.storage.couchdb.serializer.GsonSerializer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class Utils {

  public static CouchDbClient getCouchDbDataProcessorDescriptionClient() {
    return getCouchDbGsonClient("data-processor");
  }

  public static CouchDbClient getCouchDbDataStreamDescriptionClient() {
    return getCouchDbGsonClient("data-stream");
  }

  public static CouchDbClient getCouchDbDataSinkDescriptionClient() {
    return getCouchDbGsonClient("data-sink");
  }

  public static CouchDbClient getCouchDbImageClient() {
    return getCouchDbGsonClient("images");
  }

  public static CouchDbClient getCouchDbPipelineElementTemplateClient() {
    return getCouchDbGsonClient("pipelineelementtemplate");
  }

  public static CouchDbClient getCouchDbPipelineCanvasMetadataClient() {
    return getCouchDbGsonClient("pipelinecanvasmetadata");
  }

  public static CouchDbClient getCouchDbCategoryClient() {
    return getCouchDbGsonClient("category");
  }

  public static CouchDbClient getCouchDbLabelClient() {
    return getCouchDbGsonClient("label");
  }

  public static CouchDbClient getCouchDbConnectWorkerContainerClient() {
    return getCouchDbGsonClient("connectworkercontainer");
  }

  public static CouchDbClient getCouchDbFileMetadataClient() {
    return getCouchDbGsonClient("filemetadata");
  }

  public static CouchDbClient getCouchDbAdapterTemplateClient() {
    return getCouchDbAdapterClient("adaptertemplate");
  }

  public static CouchDbClient getCouchDbAssetDashboardClient() {
    return getCouchDbGsonClient("assetdashboard");
  }

  public static CouchDbClient getCouchDbAdapterInstanceClient() {
    return getCouchDbAdapterClient("adapterinstance");
  }

  public static CouchDbClient getCouchDbAdapterDescriptionClient() {
    return getCouchDbAdapterClient("adapterdescription");
  }

  public static CouchDbClient getCouchDbPipelineClient() {
    return getCouchDbGsonClient("pipeline");
  }

  public static CouchDbClient getCouchDbUserGroupStorage() {
    return getCouchDbGsonClient("usergroup");
  }

  public static CouchDbClient getCouchDbSepaInvocationClient() {
    return getCouchDbGsonClient("invocation");
  }

  public static CouchDbClient getCouchDbConnectionClient() {
    return getCouchDbStandardSerializerClient("connection");
  }

  public static CouchDbClient getCouchDbVisualizationClient() {
    return getCouchDbStandardSerializerClient("visualizations");
  }

  //TODO: Remove??
  public static CouchDbClient getCouchDbRdfEndpointClient() {
    CouchDbClient dbClient = new CouchDbClient(props("rdfendpoint"));
    return dbClient;
  }

  public static CouchDbClient getCouchDbDataExplorerDashboardClient() {
    return getCouchDbGsonClient("dataexplorerdashboard");
  }

  public static CouchDbClient getCouchDbDataExplorerWidgetClient() {
    return getCouchDbGsonClient("dataexplorerwidget");
  }

  public static CouchDbClient getCouchDbDashboardClient() {
    return getCouchDbGsonClient("dashboard");
  }

  public static CouchDbClient getCouchDbDashboardWidgetClient() {
    return getCouchDbGsonClient("dashboardwidget");
  }

  public static CouchDbClient getCouchDbUserClient() {
    return getCouchDbPrincipalClient("users");
  }

  public static CouchDbClient getCouchDbInternalUsersClient() {
    return getCouchDbStandardSerializerClient("_users");
  }

  public static CouchDbClient getCouchDbReplicatorClient() {
    return getCouchDbStandardSerializerClient("_replicator");
  }

  public static CouchDbClient getCouchDbGlobalChangesClient() {
    return getCouchDbStandardSerializerClient("_global_changes");
  }

  public static CouchDbClient getCouchDbMonitoringClient() {
    return getCouchDbStandardSerializerClient("monitoring");
  }

  public static CouchDbClient getCouchDbNotificationClient() {
    return getCouchDbStandardSerializerClient("notification");
  }

  public static CouchDbClient getCouchDbPipelineCategoriesClient() {
    return getCouchDbStandardSerializerClient("pipelinecategories");
  }

  public static CouchDbClient getCouchDbElasticsearchFilesEndppointClient() {
    return getCouchDbStandardSerializerClient("file-export-endpoints-elasticsearch");
  }

  public static CouchDbClient getCouchDbDataLakeClient() {
    return getCouchDbGsonClient("data-lake");
  }

  private static CouchDbClient getCouchDbGsonClient(String dbname) {
    CouchDbClient dbClient = new CouchDbClient(props(dbname));
    dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return dbClient;
  }

  private static CouchDbClient getCouchDbPrincipalClient(String dbname) {
    CouchDbClient dbClient = new CouchDbClient(props(dbname));
    dbClient.setGsonBuilder(GsonSerializer.getPrincipalGsonBuilder());
    return dbClient;
  }

  private static CouchDbClient getCouchDbAdapterClient(String dbname) {
    CouchDbClient dbClient = new CouchDbClient(props(dbname));
    dbClient.setGsonBuilder(GsonSerializer.getAdapterGsonBuilder());
    return dbClient;
  }

  private static CouchDbClient getCouchDbStandardSerializerClient(String dbname) {
    return new CouchDbClient(props(dbname));
  }

  public static CouchDbClient getCouchDbClient(String database) {
    return new CouchDbClient(props(database));
  }

  private static CouchDbProperties props(String dbname) {
    return new CouchDbProperties(
      dbname,
      true,
      CouchDbConfig.INSTANCE.getProtocol(),
      CouchDbConfig.INSTANCE.getHost(),
      CouchDbConfig.INSTANCE.getPort(),
      null,
      null);
  }

  public static String getDatabaseRoute(String databaseName) {
    return toUrl() + "/" + databaseName;
  }

  private static String toUrl() {
    return CouchDbConfig.INSTANCE.getProtocol()
      + "://" + CouchDbConfig.INSTANCE.getHost()
      + ":" + CouchDbConfig.INSTANCE.getPort();
  }

  public static Request getRequest(String route) {
    return append(Request.Get(route));
  }

  public static Request postRequest(String route,
                                    String payload) {
    return append(Request.Post(route).bodyString(payload, ContentType.APPLICATION_JSON));
  }

  public static Request deleteRequest(String route) {
    return append(Request.Delete(route));
  }

  public static Request putRequest(String route,
                                   String payload) {
    return append(Request.Put(route).bodyString(payload, ContentType.APPLICATION_JSON));
  }

  private static Request append(Request req) {
    req.connectTimeout(1000)
      .socketTimeout(100000);

    return req;
  }
}
