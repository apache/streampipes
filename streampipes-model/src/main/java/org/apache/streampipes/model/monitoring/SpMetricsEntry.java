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

@TsModel
public class SpMetricsEntry {

  private long lastTimestamp;
  private long messagesIn = 0;
  private long messagesOut = 0;

  public SpMetricsEntry() {
  }

  public long getLastTimestamp() {
    return lastTimestamp;
  }

  public void setLastTimestamp(long lastTimestamp) {
    this.lastTimestamp = lastTimestamp;
  }

  public long getMessagesIn() {
    return messagesIn;
  }

  public void setMessagesIn(long messagesIn) {
    this.messagesIn = messagesIn;
  }

  public long getMessagesOut() {
    return messagesOut;
  }

  public void setMessagesOut(long messagesOut) {
    this.messagesOut = messagesOut;
  }
}
