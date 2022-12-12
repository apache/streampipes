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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.apache.streampipes.model.datalake.DataLakeRetentionPolicy;

import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class ShowRetentionPolicyQuery
    extends ParameterizedDataExplorerQuery<RetentionPolicyQueryParams, List<DataLakeRetentionPolicy>> {

  public ShowRetentionPolicyQuery(RetentionPolicyQueryParams queryParams) {
    super(queryParams);
  }


  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add(showRetentionPolicyStatement());
  }

  @Override
  protected List<DataLakeRetentionPolicy> postQuery(QueryResult result) throws RuntimeException {
    List<DataLakeRetentionPolicy> policies = new ArrayList<>();
    for (List<Object> a : result.getResults().get(0).getSeries().get(0).getValues()) {
      boolean isDefault = false;
      if (a.get(4).toString().equals("true")) {
        isDefault = true;
      }
      policies.add(new DataLakeRetentionPolicy(a.get(0).toString(), a.get(1).toString(), isDefault));
    }
    return policies;
  }

  private String showRetentionPolicyStatement() {
    return "SHOW RETENTION POLICIES  ON " + "sp";
  }
}
