/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.config.backend;

public class BackendBrokerSettings {

  private Integer batchSize;
  private Integer messageMaxBytes;
  private Integer lingerMs;
  private Integer acks;

  public static BackendBrokerSettings fromDefault() {
    return new BackendBrokerSettings(1638400, 5000012, 20, 2);
  }

  public BackendBrokerSettings(Integer batchSize, Integer messageMaxBytes, Integer lingerMs,
                               Integer acks) {
    this.batchSize = batchSize;
    this.messageMaxBytes = messageMaxBytes;
    this.lingerMs = lingerMs;
    this.acks = acks;
  }

  public BackendBrokerSettings() {

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
}
