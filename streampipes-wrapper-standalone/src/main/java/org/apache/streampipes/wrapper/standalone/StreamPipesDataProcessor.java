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
package org.apache.streampipes.wrapper.standalone;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.runtime.EventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.function.Supplier;

public abstract class StreamPipesDataProcessor extends StandaloneEventProcessingDeclarer<ProcessorParams>
    implements EventProcessor<ProcessorParams> {

  @Override
  public ConfiguredEventProcessor<ProcessorParams> onInvocation(DataProcessorInvocation graph,
                                                                ProcessingElementParameterExtractor extractor) {
    Supplier<EventProcessor<ProcessorParams>> supplier = () -> this;
    return new ConfiguredEventProcessor<>(new ProcessorParams(graph), supplier);
  }

}
