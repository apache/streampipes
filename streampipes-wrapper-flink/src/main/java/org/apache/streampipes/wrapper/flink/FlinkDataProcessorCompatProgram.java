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

package org.apache.streampipes.wrapper.flink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.runtime.Event;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

import java.util.Map;

public class FlinkDataProcessorCompatProgram implements IDataProcessorProgram {

  private IStreamPipesDataProcessor pipelineElement;

  public FlinkDataProcessorCompatProgram(IStreamPipesDataProcessor pipelineElement) {
    this.pipelineElement = pipelineElement;
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream) {
    return messageStream[0]
        .flatMap(new FlatMapFunction<Event, Event>() {
          @Override
          public void flatMap(Event value, Collector<Event> out) throws Exception {
            pipelineElement.onEvent(value, new SpOutputCollector() {
              @Override
              public void collect(Event event) {
                out.collect(event);
              }

              @Override
              public void registerConsumer(String routeId, InternalEventProcessor<Map<String, Object>> consumer) {

              }

              @Override
              public void unregisterConsumer(String routeId) {

              }

              @Override
              public void connect() throws SpRuntimeException {

              }

              @Override
              public void disconnect() throws SpRuntimeException {

              }
            });
          }
        });
  }
}
