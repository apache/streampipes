/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.app.file.export.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.app.file.export.ElasticsearchAppData;
import org.streampipes.app.file.export.ElasticsearchConfig;
import org.streampipes.app.file.export.api.IElasticsearch;
import org.streampipes.app.file.export.converter.JsonConverter;
import org.streampipes.app.file.export.model.IndexInfo;
import org.streampipes.storage.couchdb.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/elasticsearch")
public class Elasticsearch implements IElasticsearch {

  private static String mainFilePath = ElasticsearchConfig.INSTANCE.getDataLocation();
  private static final List<String> excludedIndices = Collections.singletonList(".kibana");

  Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/file")
  @Override
  public Response createFiles(ElasticsearchAppData data) {
    String index = data.getIndex();
    long timestampFrom = data.getTimestampFrom();
    long timeStampTo = data.getTimestampTo();
    String output = data.getOutput();
    boolean allData = data.isAllData();

    try {
      RestHighLevelClient client = getRestHighLevelClient();

      Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
      SearchRequest searchRequest = new SearchRequest(index);
      searchRequest.scroll(scroll);
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

      if (!allData) {
        searchSourceBuilder.query(QueryBuilders.rangeQuery("timestamp").from(timestampFrom).to(timeStampTo));
      }

      searchRequest.source(searchSourceBuilder);

      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      SearchHit[] searchHits = searchResponse.getHits().getHits();

      //Time created in milli sec, index, from, to
      long timestamp = System.currentTimeMillis();
      String fileName = System.currentTimeMillis() + "-" + index + "-" + timestampFrom + "-" + timeStampTo + "." + output;
      String filePath = mainFilePath + fileName;
      FileOutputStream fileStream = this.getFileStream(filePath);

      List<Map<String, Object>> result = new ArrayList<>();
      String response = null;

      if(("csv").equals(output)) {
        JsonConverter jsonConverter = new JsonConverter();
        boolean isFirstElement = true;
        for (SearchHit hit : searchHits) {
          if (isFirstElement)
            fileStream.write(jsonConverter.getCsvHeader(hit.getSourceAsString()).getBytes());
          response = jsonConverter.convertToCsv(hit.getSourceAsString());
          fileStream.write(response.getBytes());
          isFirstElement = false;
        }
      } else {
        fileStream.write("[".getBytes());
        boolean isFirstElement = true;
        for (SearchHit hit : searchHits) {
          if(!isFirstElement)
            fileStream.write(",".getBytes());
          fileStream.write(hit.getSourceAsString().getBytes());
          isFirstElement = false;
        }
        fileStream.write("]".getBytes());
      }

      fileStream.close();

      CouchDbClient couchDbClient = getCouchDbClient();
      Map<String, Object> map = new HashMap<>();
      map.put("_id", fileName);
      map.put("fileName", fileName);
      map.put("filePath", filePath);
      map.put("createAt", timestamp);
      map.put("from", timestampFrom);
      map.put("to", timeStampTo);
      couchDbClient.save(map);

      LOG.info("Created file: " + fileName);

      return Response.ok().build();

    } catch (IOException e) {
      e.printStackTrace();
      LOG.error(e.getMessage());
      return Response.status(500).entity(e).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/file/{fileName}")
  public Response getFile(@PathParam("fileName") String fileName) {
    File file = new File(mainFilePath + fileName);
    if (file.exists()) {
      LOG.info("Downloaded file: " + fileName);
      return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
              .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
              .build();
    } else {
      LOG.info("Download - File not found");
      return Response.status(404).entity("File not found").build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/indices")
  public Response getIndices() {
    String url = ElasticsearchConfig.INSTANCE.getElasticsearchURL() + "/_cat/indices?v";
    try {
      HttpResponse<JsonNode> jsonResponse = unirestGet(url);

      JsonArray response = new JsonParser().parse(jsonResponse.getBody().toString()).getAsJsonArray();
      List<IndexInfo> availableIndices = new ArrayList<>();
      for(int i = 0; i < response.size(); i++) {
       JsonObject object = response.get(i).getAsJsonObject();
       String index = object.get("index").getAsString();
       if (!shouldExclude(index)) {
         Integer documentCount = Integer.parseInt(object.get("docs.count").getAsString());
          availableIndices.add(new IndexInfo(index, documentCount));
       }
      }
      return Response.ok(availableIndices).build();
    } catch (UnirestException e) {
      e.printStackTrace();
      return Response.serverError().build();
    }
  }

  private boolean shouldExclude(String index) {
    return excludedIndices.stream().anyMatch(i -> i.equals(index));
  }

  @DELETE
  @Path("/file/{fileName}")
  @Override
  public Response deleteFile(@PathParam("fileName") String fileName) {
    CouchDbClient couchDbClient = getCouchDbClient();
    JsonObject found = couchDbClient.find(JsonObject.class, fileName);
    couchDbClient.remove(found.get("_id").getAsString(), found.get("_rev").getAsString());
    File file = new File(mainFilePath + fileName);
    file.delete();
    LOG.info("Deleted: " + fileName);

    return Response.ok().build();
  }

  @GET
  @Path("/files")
  @Override
  public Response getEndpoints() {
    CouchDbClient couchDbClient = getCouchDbClient();
    List<JsonObject> endpoints = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);
    String json = new Gson().toJson(endpoints);

    return Response.ok(json).build();
  }

  private CouchDbClient getCouchDbClient() {
    return Utils.getCouchDbElasticsearchFilesEndppointClient();
  }

  private FileOutputStream getFileStream(String filePath) throws IOException {
    File file = new File(filePath);
    file.getParentFile().mkdirs();
    FileWriter fileWriter = new FileWriter(file, true);
    return new FileOutputStream(filePath);
  }

  private HttpResponse<JsonNode> unirestGet(String url) throws UnirestException {
    HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
            .header("accept", "application/json")
            .header("Content-Type", "application/json")
            .asJson();
    return jsonResponse;
  }

  private RestHighLevelClient getRestHighLevelClient() {
    String host = ElasticsearchConfig.INSTANCE.getElasticsearchHost();
    int port = ElasticsearchConfig.INSTANCE.getElasticsearchPort();

    return new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost(host, port, "http"))
                      .setRequestConfigCallback(
                              new RestClientBuilder.RequestConfigCallback() {
                                @Override
                                public RequestConfig.Builder customizeRequestConfig(
                                        RequestConfig.Builder requestConfigBuilder) {
                                  return requestConfigBuilder
                                          .setConnectTimeout(5000)
                                          .setSocketTimeout(60000);
                                }
                              })
    );

  }

}
