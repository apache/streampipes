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

package org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements;

import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.Util;

import java.util.List;
import java.util.Map;

public class SendToBrokerReplayAdapterSink implements IAdapterPipelineElement {

  private final SendToBrokerAdapterSink sendToBrokerAdapterSink;
  private long lastEventTimestamp;
  private final List<String> timestampKeys;
  private final boolean replaceTimestamp;
  private final float speedUp;


  public SendToBrokerReplayAdapterSink(SendToBrokerAdapterSink sendToBrokerAdapterSink,
                                       String timestampKey, boolean replaceTimestamp, float speedUp) {
    this.sendToBrokerAdapterSink = sendToBrokerAdapterSink;
    this.lastEventTimestamp = -1;
    this.timestampKeys = Util.toKeyArray(timestampKey);
    this.replaceTimestamp = replaceTimestamp;
    this.speedUp = speedUp;
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    if ((event != null) && (lastEventTimestamp != -1)) {
      long actualEventTimestamp = getTimestampInEvent(event);
      try {
        if ((actualEventTimestamp - lastEventTimestamp) > 0) {
          Thread.sleep((long) ((actualEventTimestamp - lastEventTimestamp) / speedUp));
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (replaceTimestamp) {
        setTimestampInEvent(event, System.currentTimeMillis());
      }
      lastEventTimestamp = actualEventTimestamp;

    } else if (lastEventTimestamp == -1) {
      lastEventTimestamp = getTimestampInEvent(event);
      if (replaceTimestamp) {
        setTimestampInEvent(event, System.currentTimeMillis());
      }
    }
    return sendToBrokerAdapterSink.process(event);
  }

  private long getTimestampInEvent(Map<String, Object> event) {
    if (timestampKeys.size() == 1) {
      try {
        return (long) event.get(timestampKeys.get(0));
      } catch (ClassCastException e) {
        return lastEventTimestamp;
      }
    }
    Map<String, Object> subEvent = event;
    for (int i = 0; i < timestampKeys.size() - 1; i++) {
      subEvent = (Map<String, Object>) subEvent.get(timestampKeys.get(i));
    }
    return (long) subEvent.get(timestampKeys.get(timestampKeys.size() - 1));

  }

  private void setTimestampInEvent(Map<String, Object> event, long timestamp) {
    if (timestampKeys.size() == 1) {
      event.put(timestampKeys.get(0), timestamp);
    } else {
      Map<String, Object> subEvent = event;
      for (int i = 0; i < timestampKeys.size() - 1; i++) {
        subEvent = (Map<String, Object>) subEvent.get(timestampKeys.get(i));
      }
      subEvent.put(timestampKeys.get(timestampKeys.size() - 1), timestamp);
    }
  }
}
