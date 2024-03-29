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
package org.apache.streampipes.pe.flink.processor.count;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class CountTrigger extends Trigger<Tuple3<String, String, Integer>, TimeWindow> {
  @Override
  public TriggerResult onElement(Tuple3<String, String, Integer> stringStringIntegerTuple3, long l,
                                 TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    return TriggerResult.FIRE;
  }

  @Override
  public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    return TriggerResult.CONTINUE;
  }

  @Override
  public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    return TriggerResult.CONTINUE;
  }

  @Override
  public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

  }
}
