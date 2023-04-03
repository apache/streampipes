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

package org.apache.streampipes.dataexplorer.param.model;

import org.apache.streampipes.dataexplorer.api.IQueryStatement;
import org.apache.streampipes.dataexplorer.querybuilder.IDataLakeQueryBuilder;

public class OffsetClauseParams implements IQueryStatement {

  private final Integer offset;

  public OffsetClauseParams(Integer offset) {
    this.offset = offset;
  }

  public static OffsetClauseParams from(Integer offset) {
    return new OffsetClauseParams(offset);
  }

  public Integer getOffset() {
    return offset;
  }

  @Override
  public void buildStatement(IDataLakeQueryBuilder<?> builder) {
    builder.withOffset(offset);
  }
}
