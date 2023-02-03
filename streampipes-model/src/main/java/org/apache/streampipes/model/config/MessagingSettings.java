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
package org.apache.streampipes.model.config;

import java.util.List;

public class MessagingSettings {

  private Integer batchSize;
  private Integer messageMaxBytes;
  private Integer lingerMs;
  private Integer acks;

  private List<SpDataFormat> prioritizedFormats;
  private List<SpProtocol> prioritizedProtocols;



  public MessagingSettings(Integer batchSize,
                           Integer messageMaxBytes,
                           Integer lingerMs,
                           Integer acks,
                           List<SpDataFormat> prioritizedFormats,
                           List<SpProtocol> prioritizedProtocols) {
    this.batchSize = batchSize;
    this.messageMaxBytes = messageMaxBytes;
    this.lingerMs = lingerMs;
    this.acks = acks;
    this.prioritizedFormats = prioritizedFormats;
    this.prioritizedProtocols = prioritizedProtocols;
  }

  public MessagingSettings() {

  }

  public Integer getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }

  public Integer getMessageMaxBytes() {
    return messageMaxBytes;
  }

  public void setMessageMaxBytes(Integer messageMaxBytes) {
    this.messageMaxBytes = messageMaxBytes;
  }

  public Integer getLingerMs() {
    return lingerMs;
  }

  public void setLingerMs(Integer lingerMs) {
    this.lingerMs = lingerMs;
  }

  public Integer getAcks() {
    return acks;
  }

  public void setAcks(Integer acks) {
    this.acks = acks;
  }

  public List<SpDataFormat> getPrioritizedFormats() {
    return prioritizedFormats;
  }

  public void setPrioritizedFormats(List<SpDataFormat> prioritizedFormats) {
    this.prioritizedFormats = prioritizedFormats;
  }

  public List<SpProtocol> getPrioritizedProtocols() {
    return prioritizedProtocols;
  }

  public void setPrioritizedProtocols(List<SpProtocol> prioritizedProtocols) {
    this.prioritizedProtocols = prioritizedProtocols;
  }
}
