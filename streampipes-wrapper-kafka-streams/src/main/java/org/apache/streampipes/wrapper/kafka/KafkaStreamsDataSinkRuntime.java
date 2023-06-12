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
package org.apache.streampipes.wrapper.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IDataSinkRuntime;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.context.generator.DataSinkContextGenerator;
import org.apache.streampipes.wrapper.params.generator.DataSinkParameterGenerator;

public class KafkaStreamsDataSinkRuntime extends KafkaStreamsRuntime<
    IStreamPipesDataSink,
    DataSinkInvocation,
    EventSinkRuntimeContext,
    IDataSinkParameterExtractor,
    IDataSinkParameters> implements IDataSinkRuntime {

  public KafkaStreamsDataSinkRuntime() {
    super(new DataSinkContextGenerator(), new DataSinkParameterGenerator());
  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    // TODO
  }

  @Override
  protected void afterStop() {
    this.pipelineElement.onPipelineStopped();
  }
}
