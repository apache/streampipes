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
import java.util.HashMap;
import java.util.Map;

public class FileWatcherCheckpoint implements Serializable {

  private String currentFileName;
  private long currentSequence;
  private long lastProcessedRecord;
  private FileFingerprint currentFingerprint;
  private Map<String, FileFingerprint> processedGenerations;

  public FileWatcherCheckpoint() {
    this.lastProcessedRecord = -1L;
    this.processedGenerations = new HashMap<>();
  }

  public String getCurrentFileName() {
    return currentFileName;
  }

  public void setCurrentFileName(String currentFileName) {
    this.currentFileName = currentFileName;
  }

  public long getCurrentSequence() {
    return currentSequence;
  }

  public void setCurrentSequence(long currentSequence) {
    this.currentSequence = currentSequence;
  }

  public long getLastProcessedRecord() {
    return lastProcessedRecord;
  }

  public void setLastProcessedRecord(long lastProcessedRecord) {
    this.lastProcessedRecord = lastProcessedRecord;
  }

  public FileFingerprint getCurrentFingerprint() {
    return currentFingerprint;
  }

  public void setCurrentFingerprint(FileFingerprint currentFingerprint) {
    this.currentFingerprint = currentFingerprint;
  }

  public Map<String, FileFingerprint> getProcessedGenerations() {
    return processedGenerations;
  }

  public void setProcessedGenerations(Map<String, FileFingerprint> processedGenerations) {
    this.processedGenerations = processedGenerations;
  }
}
