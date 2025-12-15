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

package org.apache.streampipes.processors.geo.jvm.jts.processor.epsg;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Datatypes;

public class EpsgProcessor implements IStreamPipesDataProcessor {
  private static final String EPSG_KEY = "epsg-key";
  private static final String EPSG_RUNTIME = "epsg";

  private int epsgCode;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        EpsgProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.geo.jvm.jts.processor.epsg", 0)
            .category(DataProcessorType.GEO)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .build())
            .outputStrategy(OutputStrategies.append(PrimitivePropertyBuilder
                                                        .create(Datatypes.Integer, EPSG_RUNTIME)
                                                        .semanticType("http://data.ign.fr/def/ignf#CartesianCS")
                                                        .build())
            )
            .requiredIntegerParameter(Labels.withId(EPSG_KEY), 4326)
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) throws SpRuntimeException {
    this.epsgCode = params.extractor()
                          .singleValueParameter(EPSG_KEY, Integer.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    event.addField(EpsgProcessor.EPSG_RUNTIME, this.epsgCode);
    collector.collect(event);
  }

  @Override
  public void onPipelineStopped() {

  }
}
