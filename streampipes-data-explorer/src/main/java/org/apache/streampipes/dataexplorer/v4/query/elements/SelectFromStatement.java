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

package org.apache.streampipes.dataexplorer.v4.query.elements;

import org.apache.streampipes.dataexplorer.v4.params.SelectFromStatementParams;

import java.util.StringJoiner;

public class SelectFromStatement extends QueryElement<SelectFromStatementParams> {

  public SelectFromStatement(SelectFromStatementParams selectFromStatementParams) {
    super(selectFromStatementParams);
  }

  @Override
  protected String buildStatement(SelectFromStatementParams params) {
    if (params.isSelectWildcard()) {
      return "SELECT * FROM " + escapeIndex(params.getIndex());
    } else {
      StringJoiner joiner = new StringJoiner(",");
      String queryPrefix = "SELECT ";
      String queryAppendix = " FROM " + escapeIndex(params.getIndex());

      params.getSelectedColumns().forEach(column -> {
        joiner.add(column.toQueryString());
      });

      return queryPrefix + joiner + queryAppendix;
    }
  }

  private String escapeIndex(String index) {
    return "\"" + index + "\"";
  }
}
