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
package org.apache.streampipes.sinks.databases.flink.elasticsearch;

import org.apache.streampipes.sinks.databases.flink.elasticsearch.elastic.ElasticsearchSinkFunction;
import org.apache.streampipes.sinks.databases.flink.elasticsearch.elastic.RequestIndexer;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.Map;

public class ElasticsearchIndexRequestBuilder implements ElasticsearchSinkFunction<Map<String, Object>> {

  private String indexName;
  private String typeName;

  public ElasticsearchIndexRequestBuilder(String indexName, String typeName) {
    this.indexName = indexName;
    this.typeName = typeName;
  }

  private static final long serialVersionUID = 1L;

  private IndexRequest createIndexRequest(Map<String, Object> element) {

    return Requests.indexRequest()
        .index(indexName)
        .type(typeName)
        .source(element);
  }

  @Override
  public void process(Map<String, Object> stringObjectMap, RuntimeContext runtimeContext,
                      RequestIndexer requestIndexer) {
    requestIndexer.add(createIndexRequest(stringObjectMap));
  }
}

