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


import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputConfig;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputType;

import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SiddhiOutputStreamDebugCallback extends StreamCallback {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiOutputStreamDebugCallback.class);

  private final SiddhiDebugCallback callback;
  private final SiddhiOutputConfig outputConfig;

  public SiddhiOutputStreamDebugCallback(SiddhiDebugCallback callback,
                                         SiddhiOutputConfig outputConfig) {
    this.callback = callback;
    this.outputConfig = outputConfig;
  }

  @Override
  public void receive(Event[] inEvents) {
    LOG.info("Siddhi is firing");
    if (inEvents.length > 0) {
      if (this.outputConfig.getSiddhiOutputType() == SiddhiOutputType.FIRST) {
        this.callback.onEvent(inEvents[inEvents.length - 1]);
      } else if (this.outputConfig.getSiddhiOutputType() == SiddhiOutputType.LIST) {
        this.callback.onEvent(Arrays.asList(inEvents));
      }

    }
  }
}
