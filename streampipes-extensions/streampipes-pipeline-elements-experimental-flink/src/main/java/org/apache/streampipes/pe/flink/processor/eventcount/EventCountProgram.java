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
package org.apache.streampipes.pe.flink.processor.eventcount;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.flink.processor.count.TimeWindowConverter;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import org.apache.flink.streaming.api.datastream.DataStream;

public class EventCountProgram extends FlinkDataProcessorProgram<EventCountParameters> {

  public EventCountProgram(EventCountParameters params) {
    super(params);
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    return dataStreams[0]
        .map(new EventCountMapper())
        .timeWindowAll(new TimeWindowConverter().makeTimeWindow(params.getTimeWindowSize(),
            params.getTimeWindowScale()))
        .sum(0)
        .map(new EventCountOutputMapper());
  }
}
