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

package org.apache.streampipes.processors.transformation.jvm.processor.fieldrenamermulti;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;

import org.apache.commons.lang3.tuple.Pair;

public class MultiFieldRenameRuntime implements IStreamPipesDataProcessor {

  private MultiFieldRenameParameters parameters;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return new MultiFieldRenameDeclarer().declareConfig();
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params,
                                SpOutputCollector spOutputCollector,
                                EventProcessorRuntimeContext runtimeContext) {
    this.parameters = new MultiFieldRenameParameters(params);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) {

    for (Pair<String, String> mapping : parameters.getMappings()) {
      AbstractField<?> field = event.getFieldBySelector(mapping.getLeft());
      if (field != null) {
        event.removeFieldBySelector(mapping.getLeft());
        event.addField(mapping.getRight(), field);
      }
    }

    spOutputCollector.collect(event);
  }

  @Override
  public void onPipelineStopped() {
    // nothing to clean up
  }
}
