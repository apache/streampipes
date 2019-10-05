/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.aggregation.flink.processor.eventcount;

import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.runtime.Event;

public class EventCountOutputMapper implements MapFunction<Integer, Event> {

  private static final Logger LOG = LoggerFactory.getLogger(EventCountOutputMapper.class);

  @Override
  public Event map(Integer integer) throws Exception {
    Event outEvent = new Event();
    outEvent.addField("timestamp", System.currentTimeMillis());
    outEvent.addField("count", integer);

    LOG.info("timestamp {}, count {}", System.currentTimeMillis(), integer);
    return outEvent;
  }
}
