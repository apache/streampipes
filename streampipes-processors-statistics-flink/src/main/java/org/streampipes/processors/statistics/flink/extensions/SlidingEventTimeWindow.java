/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.streampipes.processors.statistics.flink.extensions;

import org.apache.flink.streaming.api.operators.AbstractUdfStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlidingEventTimeWindow<IN> extends AbstractUdfStreamOperator<List<IN>,
        TimestampMappingFunction<IN>>
        implements
        OneInputStreamOperator<IN, List<IN>>, Serializable {

  private Long timeWindowSizeInMillis;
  private List<IN> currentEvents;

  public SlidingEventTimeWindow(Long time, TimeUnit timeUnit, TimestampMappingFunction<IN>
          timestampMappingFunction) {
    super(timestampMappingFunction);
    this.timeWindowSizeInMillis = toMilliseconds(time, timeUnit);
    this.currentEvents = new ArrayList<>();

  }

  private Long toMilliseconds(Long time, TimeUnit timeUnit) {
    return timeUnit.toMillis(time);
  }

  @Override
  public void processElement(StreamRecord<IN> in) throws Exception {
    Long currentTimestamp = userFunction.getTimestamp(in.getValue());

    checkForRemoval(currentTimestamp);
    currentEvents.add(in.getValue());

    output.collect(new StreamRecord<>(currentEvents, System.currentTimeMillis()));

  }

  private void checkForRemoval(Long currentTimestamp) {
    Iterator<IN> it = currentEvents.iterator();

    while(it.hasNext()) {
      IN next = it.next();
      if (removalRequired(userFunction.getTimestamp(next), currentTimestamp)) {
        it.remove();
      } else {
        break;
      }
    }
  }

  private boolean removalRequired(Long oldTimestamp, Long currentTimestamp) {
    return (currentTimestamp - oldTimestamp > timeWindowSizeInMillis);
  }

}
