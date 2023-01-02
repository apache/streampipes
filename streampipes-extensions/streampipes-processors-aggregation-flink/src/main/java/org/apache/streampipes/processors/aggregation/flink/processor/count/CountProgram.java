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
package org.apache.streampipes.processors.aggregation.flink.processor.count;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.aggregation.flink.AbstractAggregationProgram;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CountProgram extends AbstractAggregationProgram<CountParameters> {

  public CountProgram(CountParameters params,
                      ConfigExtractor configExtractor,
                      StreamPipesClient streamPipesClient) {
    super(params, configExtractor, streamPipesClient);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    return dataStreams[0]
        .map(new CountMapper(bindingParams.getFieldToCount()))
        .keyBy(1)
        .timeWindow(new TimeWindowConverter().makeTimeWindow(bindingParams.getTimeWindowSize(),
            bindingParams.getTimeWindowScale()))
        .trigger(new CountTrigger())
        .sum(2)
        .map(new Tuple2MapMapper());
  }


  @Override
  public void appendEnvironmentConfig(StreamExecutionEnvironment env) {
    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

}
