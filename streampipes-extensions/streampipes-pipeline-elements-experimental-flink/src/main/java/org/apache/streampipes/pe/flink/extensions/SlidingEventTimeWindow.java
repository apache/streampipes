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

package org.apache.streampipes.pe.flink.extensions;

import org.apache.flink.streaming.api.operators.AbstractUdfStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlidingEventTimeWindow<T> extends AbstractUdfStreamOperator<List<T>,
    TimestampMappingFunction<T>>
    implements
    OneInputStreamOperator<T, List<T>>, Serializable {

  private Long timeWindowSizeInMillis;
  private List<T> currentEvents;

  public SlidingEventTimeWindow(Long time, TimeUnit timeUnit, TimestampMappingFunction<T>
      timestampMappingFunction) {
    super(timestampMappingFunction);
    this.timeWindowSizeInMillis = toMilliseconds(time, timeUnit);
    this.currentEvents = new ArrayList<>();

  }

  private Long toMilliseconds(Long time, TimeUnit timeUnit) {
    return timeUnit.toMillis(time);
  }

  @Override
  public void processElement(StreamRecord<T> in) throws Exception {
    Long currentTimestamp = userFunction.getTimestamp(in.getValue());

    checkForRemoval(currentTimestamp);
    currentEvents.add(in.getValue());

    output.collect(new StreamRecord<>(currentEvents, System.currentTimeMillis()));

  }

  private void checkForRemoval(Long currentTimestamp) {
    Iterator<T> it = currentEvents.iterator();

    while (it.hasNext()) {
      T next = it.next();
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
