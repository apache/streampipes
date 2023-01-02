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

package org.apache.streampipes.wrapper.flink;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FlinkDataSinkRuntime<T extends EventSinkBindingParams> extends
    FlinkRuntime<EventSinkRuntimeParams<T>, T, DataSinkInvocation, EventSinkRuntimeContext> {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(FlinkDataSinkRuntime.class);

  public FlinkDataSinkRuntime(T params,
                              ConfigExtractor configExtractor,
                              StreamPipesClient streamPipesClient) {
    super(params, configExtractor, streamPipesClient);
  }

  @Override
  public void appendExecutionConfig(DataStream<Event>... convertedStream) {
    getSink(convertedStream);

  }

  public abstract void getSink(DataStream<Event>... convertedStream1);

  protected EventSinkRuntimeParams<T> makeRuntimeParams(ConfigExtractor configExtractor,
                                                        StreamPipesClient streamPipesClient) {
    LOG.warn(
        "The config extractor and StreamPipes Client can currently not be accessed by"
            + " a deployed Flink program due to non-serializable classes.");
    return new EventSinkRuntimeParams<>(bindingParams, false, null, null);
  }

}
