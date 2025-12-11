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

package org.apache.streampipes.client.api;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.datalake.SpQueryResult;

import java.util.HashMap;
import java.util.Map;

public class DataLakeResourceApi extends AbstractClientApi implements IDataLakeResourceApi {

  public DataLakeResourceApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }

  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromStreamPipesBasePath()
        .addToPath("api")
        .addToPath("v4")
        .addToPath("datalake")
        .addToPath("measurements");
  }

  @Override
  public void delete(String measurementID, Long startDate, Long endDate) {

    Map<String, String> queryParams = new HashMap<>();
    if (startDate != null) {
      queryParams.put("startDate", startDate.toString());
    }
    if (endDate != null) {
      queryParams.put("endDate", endDate.toString());
    }
    delete(getBaseResourcePath().addToPath(measurementID).withQueryParameters(queryParams), Void.class);

  }

  @Override
  public void update(String measurementID, SpQueryResult queryResult, boolean ignoreSchemaMismatch) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("ignoreSchemaMismatch", String.valueOf(ignoreSchemaMismatch));
    post(getBaseResourcePath().addToPath(measurementID).withQueryParameters(queryParams), queryResult);
  }

  @Override
  public SpQueryResult get(String measurementID, Map<String, String> queryParams) {
    return getSingle(getBaseResourcePath().addToPath(measurementID).withQueryParameters(queryParams),
        SpQueryResult.class);
  }

}
