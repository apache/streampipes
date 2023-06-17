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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.client.connection.Connection;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendation;
import org.apache.streampipes.storage.api.IPipelineElementConnectionStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConnectionStorageImpl extends AbstractDao<Connection> implements
    IPipelineElementConnectionStorage {


  public ConnectionStorageImpl() {
    super(Utils::getCouchDbConnectionClient, Connection.class);
  }

  @Override
  public void addConnection(Connection connection) {
    persist(connection);
  }

  @Override
  public List<PipelineElementRecommendation> getRecommendedElements(String from) {
    // doesn't work as required object array is not created by lightcouch
    //List<JsonObject> obj = dbClient.view("connection/frequent").startKey(from).endKey(from, new Object())
    // .group(true).query(JsonObject.class);
    String query;
    query = buildQuery(from);
    Optional<JsonObject> jsonObjectOpt = getFrequentConnections(query);
    if (jsonObjectOpt.isPresent()) {
      return handleResponse(jsonObjectOpt.get());
    } else {
      return Collections.emptyList();
    }

  }

  private String buildQuery(String from) {
    String escapedPath = Utils
        .escapePathSegment("startkey=[\"" + from + "\"]&endkey=[\"" + from + "\", {}]&group=true");
    return couchDbClientSupplier.get().getDBUri() + "_design/connection/_view/frequent?" + escapedPath;
  }

  private List<PipelineElementRecommendation> handleResponse(JsonObject jsonObject) {
    List<PipelineElementRecommendation> recommendations = new ArrayList<>();
    JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
    jsonArray.forEach(resultObj ->
        recommendations.add(makeRecommendation(resultObj)));
    return recommendations;
  }

  private PipelineElementRecommendation makeRecommendation(JsonElement resultObj) {
    PipelineElementRecommendation recommendation = new PipelineElementRecommendation();
    recommendation.setElementId(resultObj
        .getAsJsonObject()
        .get("key")
        .getAsJsonArray()
        .get(1).getAsString());

    recommendation.setCount(resultObj
        .getAsJsonObject()
        .get("value")
        .getAsInt());

    return recommendation;
  }

  private Optional<JsonObject> getFrequentConnections(String query) {
    try {
      var request = Utils.append(Request.Get(query));
      return Optional.of((JsonObject) new JsonParser().parse(request.execute().returnContent().asString()));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

}
