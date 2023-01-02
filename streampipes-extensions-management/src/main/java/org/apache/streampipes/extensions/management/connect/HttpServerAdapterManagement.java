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
package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.messaging.InternalEventProcessor;

import java.util.HashMap;
import java.util.Map;

public enum HttpServerAdapterManagement {

  INSTANCE;

  private Map<String, InternalEventProcessor<byte[]>> httpServerAdapters;

  HttpServerAdapterManagement() {
    this.httpServerAdapters = new HashMap<>();
  }

  public void addAdapter(String endpointId,
                         InternalEventProcessor<byte[]> callback) {
    this.httpServerAdapters.put(endpointId, callback);
  }

  public void removeAdapter(String endpointId) {
    this.httpServerAdapters.remove(endpointId);
  }

  public void notify(String endpointId, byte[] event) throws IllegalArgumentException {
    if (httpServerAdapters.containsKey(endpointId)) {
      httpServerAdapters.get(endpointId).onEvent(event);
    } else {
      throw new IllegalArgumentException("Adapter id " + endpointId + " does not exist.");
    }
  }
}
