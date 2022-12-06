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

package org.apache.streampipes.sinks.brokers.jvm.bufferrest.buffer;

import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
  private int bufferSize;
  private List<BufferListener> listeners;
  private List<String> messages;

  public MessageBuffer(int bufferSize) {
    this.bufferSize = bufferSize;
    this.messages = new ArrayList<String>();
    this.listeners = new ArrayList<BufferListener>();
  }

  public void addMessage(String message) {
    messages.add(message);

    if (bufferSize <= messages.size()) {
      notifyListeners();
      clearBuffer();
    }
  }

  private void clearBuffer() {
    this.messages = new ArrayList<String>();
  }

  public void addListener(BufferListener listener) {
    listeners.add(listener);
  }

  public void removeListener(BufferListener listener) {
    listeners.remove(listener);
  }

  private String getMessagesAsJsonString() {
    String messagesAsJson;
    if (bufferSize > 1) {
      messagesAsJson = "[";
      int i = 1;
      for (String message : messages) {
        messagesAsJson += message;
        if (i < messages.size()) {
          messagesAsJson += ",";
        }
        i++;
      }
      messagesAsJson += "]";
    } else {
      messagesAsJson = messages.get(0);
    }
    return messagesAsJson;
  }

  private void notifyListeners() {
    String messagesJsonArray = getMessagesAsJsonString();
    for (BufferListener listener : listeners) {
      listener.bufferFull(messagesJsonArray);
    }
  }
}
