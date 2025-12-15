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
package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;

public class MergeBySchemaProcessor implements IStreamPipesDataProcessor {

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        MergeBySchemaProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.filters.jvm.schema", 0)
            .category(DataProcessorType.TRANSFORM)
            .withAssets(ExtensionAssetType.DOCUMENTATION)
            .withLocales(Locales.EN)
            .outputStrategy(OutputStrategies.keep())
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params, SpOutputCollector spOutputCollector,
                                EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var schema1 = params.getInputStreamParams()
                                .get(1)
                                .getSchemaInfo()
                                .getEventSchema();
    var schema2 = params.getInputStreamParams()
                                .get(0)
                                .getSchemaInfo()
                                .getEventSchema();

    if (!schema1.equals(schema2)) {
      throw new SpRuntimeException("Schemas do not match. Cannot merge events.");
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    spOutputCollector.collect(event);
  }

  @Override
  public void onPipelineStopped() throws SpRuntimeException {
  }
}
