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

package org.apache.streampipes.pe.flink.sink.elasticsearch;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

public class ElasticSearchParameters extends SinkParams {

  private String timestampField;
  private String indexName;
  private String elasticsearchHost;
  private Integer elasticsearchPort;

  public ElasticSearchParameters(DataSinkInvocation graph,
                                 String timestampField,
                                 String indexName,
                                 String elasticsearchHost,
                                 Integer elasticsearchPort) {
    super(graph);
    this.timestampField = timestampField;
    this.indexName = indexName;
    this.elasticsearchHost = elasticsearchHost;
    this.elasticsearchPort = elasticsearchPort;
  }

  public String getTimestampField() {
    return timestampField;
  }

  public String getIndexName() {
    return indexName;
  }

  public String getElasticsearchHost() {
    return elasticsearchHost;
  }

  public Integer getElasticsearchPort() {
    return elasticsearchPort;
  }
}
