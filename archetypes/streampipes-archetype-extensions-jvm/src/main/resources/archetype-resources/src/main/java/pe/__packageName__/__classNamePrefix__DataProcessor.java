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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.${packageName};

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;


public class ${classNamePrefix}DataProcessor extends StreamPipesDataProcessor {

  private String exampleText;

  private static final String EXAMPLE_KEY = "example-key";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("${package}.pe.${packageName}.processor")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.AGGREGATE)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(EXAMPLE_KEY))
        .outputStrategy(OutputStrategies.append(PrimitivePropertyBuilder.create(Datatypes.String, "appendedText").build()))
        .build();
    }

  @Override
  public void onInvocation(ProcessorParams processorParams,
                           SpOutputCollector out,
                           EventProcessorRuntimeContext ctx) throws SpRuntimeException {

    this.exampleText = processorParams.extractor().singleValueParameter(EXAMPLE_KEY, String.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {
    event.addField("appendedText", exampleText);
    out.collect(event);
  }

  @Override
  public void onDetach() {

  }
}
