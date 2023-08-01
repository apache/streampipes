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
public class SpLogEntry {

  private long timestamp;
  private SpLogMessage errorMessage;

  public SpLogEntry() {

  }

  public SpLogEntry(SpLogEntry other) {
    this.timestamp = other.getTimestamp();
    this.errorMessage = new SpLogMessage(other.getErrorMessage());
  }

  private SpLogEntry(long timestamp,
                     SpLogMessage errorMessage) {
    this.timestamp = timestamp;
    this.errorMessage = errorMessage;
  }

  public static SpLogEntry from(long timestamp,
                                SpLogMessage errorMessage) {
    return new SpLogEntry(timestamp, errorMessage);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public SpLogMessage getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(SpLogMessage errorMessage) {
    this.errorMessage = errorMessage;
  }
}
