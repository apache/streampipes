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
package org.apache.streampipes.wrapper.flink.converter;

import org.apache.streampipes.extensions.api.pe.param.IInternalRuntimeParameters;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.params.InternalRuntimeParameters;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class MapToEventConverter<T extends IPipelineElementParameters<?, ?>> implements
    FlatMapFunction<Map<String,
        Object>, Event> {

  private static final long serialVersionUID = 1L;

  private final T runtimeParams;
  private final String sourceId;

  private final IInternalRuntimeParameters internalRuntimeParameters;

  public MapToEventConverter(String sourceId, T runtimeParams) {
    this.sourceId = sourceId;
    this.runtimeParams = runtimeParams;
    this.internalRuntimeParameters = new InternalRuntimeParameters();
  }

  @Override
  public void flatMap(Map<String, Object> inMap, Collector<Event> collector) throws Exception {
    collector.collect(internalRuntimeParameters.makeEvent(runtimeParams, inMap, sourceId));
  }
}
