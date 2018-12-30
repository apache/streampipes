/*
Copyright 2018 FZI Forschungszentrum Informatik

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
package org.streampipes.sources.random.model;

public class MessageResult {

  private byte[] message;
  private String topic;
  private Boolean shouldSend;

  public MessageResult(byte[] message, String topic) {
    this.message = message;
    this.topic = topic;
    this.shouldSend = true;
  }

  public MessageResult(Boolean shouldSend) {
    this.shouldSend = shouldSend;
  }

  public byte[] getMessage() {
    return message;
  }

  public String getTopic() {
    return topic;
  }

  public Boolean getShouldSend() {
    return shouldSend;
  }

}
