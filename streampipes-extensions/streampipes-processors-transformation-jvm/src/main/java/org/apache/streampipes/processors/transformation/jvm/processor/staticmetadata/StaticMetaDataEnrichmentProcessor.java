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

package org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class StaticMetaDataEnrichmentProcessor extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String STATIC_METADATA_INPUT = "static-metadata-input";
  private static final String STATIC_METADATA_INPUT_RUNTIME_NAME = "static-metadata-input-runtime-name";
  private static final String STATIC_METADATA_INPUT_VALUE = "static-metadata-input-value";
  private static final String STATIC_METADATA_INPUT_DATATYPE = "static-metadata-input-datatype";

  private static final String OPTION_BOOL = "Bool";
  private static final String OPTION_STRING = "String";
  private static final String OPTION_FLOAT = "Float";
  private static final String OPTION_INTEGER = "Integer";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
                                       "org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata",
                                       1
                                   )
                                   .category(DataProcessorType.ENRICH)
                                   .withLocales(Locales.EN)
                                   .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                                   .requiredCollection(
                                       Labels.withId(STATIC_METADATA_INPUT),
                                       StaticProperties.stringFreeTextProperty(Labels.withId(
                                           STATIC_METADATA_INPUT_RUNTIME_NAME)),
                                       StaticProperties.stringFreeTextProperty(
                                           Labels.withId(STATIC_METADATA_INPUT_VALUE)),
                                       StaticProperties.singleValueSelection(
                                           Labels.withId(STATIC_METADATA_INPUT_DATATYPE),
                                           Options.from(
                                               OPTION_BOOL,
                                               OPTION_STRING,
                                               OPTION_FLOAT,
                                               OPTION_INTEGER
                                           )
                                       )
                                   )
                                   .requiredStream(StreamRequirementsBuilder.any())
                                   .outputStrategy(OutputStrategies.customTransformation())
                                   .build();
  }

  @Override
  public EventSchema resolveOutputStrategy(
      DataProcessorInvocation processingElement,
      ProcessingElementParameterExtractor parameterExtractor
  ) throws SpConfigurationException {
    return null;
  }

  @Override
  public void onInvocation(
      ProcessorParams parameters,
      SpOutputCollector spOutputCollector,
      EventProcessorRuntimeContext runtimeContext
  ) throws SpRuntimeException {

  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {

  }

  @Override
  public void onDetach() {

  }

}
