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
package org.apache.streampipes.wrapper.siddhi.engine.callback;

import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiddhiOutputStreamDebugCallback extends StreamCallback {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiOutputStreamDebugCallback.class);

  private SiddhiDebugCallback callback;

  public SiddhiOutputStreamDebugCallback(SiddhiDebugCallback callback) {
    this.callback = callback;
  }

  @Override
  public void receive(Event[] events) {
    LOG.info("Siddhi is firing");
    if (events.length > 0) {
      this.callback.onEvent(events[events.length - 1]);
    }
  }
}
