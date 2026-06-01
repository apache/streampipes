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

package org.apache.streampipes.extensions.connectors.filewatcher.model;

import java.io.Serializable;

public class FileGenerationState implements Serializable {

  private FileFingerprint fingerprint;
  private long lastProcessedRecord;

  public FileGenerationState() {
    this.lastProcessedRecord = -1L;
  }

  public FileGenerationState(FileFingerprint fingerprint, long lastProcessedRecord) {
    this.fingerprint = fingerprint;
    this.lastProcessedRecord = lastProcessedRecord;
  }

  public FileFingerprint getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(FileFingerprint fingerprint) {
    this.fingerprint = fingerprint;
  }

  public long getLastProcessedRecord() {
    return lastProcessedRecord;
  }

  public void setLastProcessedRecord(long lastProcessedRecord) {
    this.lastProcessedRecord = lastProcessedRecord;
  }
}
