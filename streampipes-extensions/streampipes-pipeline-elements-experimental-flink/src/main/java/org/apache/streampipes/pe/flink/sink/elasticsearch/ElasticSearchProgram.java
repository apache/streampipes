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


import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.flink.sink.elasticsearch.elastic.ElasticsearchSink;
import org.apache.streampipes.wrapper.flink.FlinkDataSinkProgram;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchProgram extends FlinkDataSinkProgram<ElasticSearchParameters> implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final String INDEX_NAME_PREFIX = "sp_";

  public ElasticSearchProgram(ElasticSearchParameters params) {
    super(params);
  }

  @Override
  public void getSink(
      DataStream<Event>... convertedStream) {

    String indexName = params.getIndexName();
    String timeName = params.getTimestampField();
    String elasticsearchHost = params.getElasticsearchHost();
    Integer elasticsearchPort = params.getElasticsearchPort();

    List<HttpHost> httpHosts = Arrays.asList(new HttpHost(
        elasticsearchHost,
        elasticsearchPort,
        "http"));

    Map<String, String> userConfig = new HashMap<>();
    // This instructs the sink to emit after every element, otherwise they would be buffered
//        userConfig.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS, "1");
    userConfig.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_INTERVAL_MS, "5000");

    convertedStream[0].flatMap(new FlatMapFunction<Event, Map<String, Object>>() {

      @Override
      public void flatMap(Event in, Collector<Map<String, Object>> out) throws Exception {
        Map<String, Object> rawEvent = in.getRaw();
        rawEvent.put("date", new Date(in.getFieldBySelector(timeName)
            .getAsPrimitive().getAsLong()));
        out.collect(rawEvent);
      }
    }).addSink(new ElasticsearchSink<>(userConfig, httpHosts, new
        ElasticsearchIndexRequestBuilder(INDEX_NAME_PREFIX + indexName, INDEX_NAME_PREFIX
        + indexName)));

  }

}
