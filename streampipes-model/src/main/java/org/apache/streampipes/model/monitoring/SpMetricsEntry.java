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

package org.apache.streampipes.model.monitoring;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.HashMap;
import java.util.Map;

@TsModel
public class SpMetricsEntry {

  private long lastTimestamp;
  private Map<String, MessageCounter> messagesIn;
  private MessageCounter messagesOut;

  public SpMetricsEntry() {
    this.messagesIn = new HashMap<>();
    this.messagesOut = new MessageCounter();
  }

  public long getLastTimestamp() {
    return lastTimestamp;
  }

  public void setLastTimestamp(long lastTimestamp) {
    this.lastTimestamp = lastTimestamp;
  }

  public Map<String, MessageCounter> getMessagesIn() {
    return messagesIn;
  }

  public void setMessagesIn(Map<String, MessageCounter> messagesIn) {
    this.messagesIn = messagesIn;
  }

  public MessageCounter getMessagesOut() {
    return messagesOut;
  }

  public void setMessagesOut(MessageCounter messagesOut) {
    this.messagesOut = messagesOut;
  }

  public void addOutMetrics(long lastTimestamp) {
    this.messagesOut.setLastTimestamp(lastTimestamp);
    this.messagesOut.setCounter(this.messagesOut.getCounter() + 1);
  }

  public void addInMetrics(String sourceInfo,
                           long lastTimestamp) {
    if (!this.messagesIn.containsKey(sourceInfo)) {
      this.messagesIn.put(sourceInfo, new MessageCounter());
    }

    var messagesIn = this.messagesIn.get(sourceInfo);
    messagesIn.setCounter(messagesIn.getCounter() + 1);
    messagesIn.setLastTimestamp(lastTimestamp);
  }

  public void reset() {
    this.lastTimestamp = 0;
    this.messagesIn.clear();
    this.messagesOut.setCounter(0);
    this.messagesOut.setLastTimestamp(0);
  }
}
