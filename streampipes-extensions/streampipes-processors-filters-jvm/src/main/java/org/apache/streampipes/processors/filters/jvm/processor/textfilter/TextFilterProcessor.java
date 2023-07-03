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

package org.apache.streampipes.processors.filters.jvm.processor.textfilter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class TextFilterProcessor extends StreamPipesDataProcessor {

  private static final String KEYWORD_ID = "keyword";
  private static final String OPERATION_ID = "operation";
  private static final String MAPPING_PROPERTY_ID = "text";

  private String keyword;
  private StringOperator stringOperator;
  private String filterProperty;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.textfilter")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.FILTER, DataProcessorType.STRING_OPERATOR)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements
                .stringReq(), Labels.withId(MAPPING_PROPERTY_ID), PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(OPERATION_ID), Options.from("MATCHES",
            "CONTAINS"))
        .requiredTextParameterWithLink(Labels.withId(KEYWORD_ID), "text")
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
    this.keyword = processorParams.extractor().singleValueParameter(KEYWORD_ID, String.class);
    this.stringOperator =
        StringOperator.valueOf(processorParams.extractor().selectedSingleValue(OPERATION_ID, String.class));
    this.filterProperty = processorParams.extractor().mappingPropertyValue(MAPPING_PROPERTY_ID);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    Boolean satisfiesFilter = false;
    String value = event.getFieldBySelector(this.filterProperty)
        .getAsPrimitive()
        .getAsString();

    if (this.stringOperator == StringOperator.MATCHES) {
      satisfiesFilter = (value.equals(this.keyword));
    } else if (this.stringOperator == StringOperator.CONTAINS) {
      satisfiesFilter = (value.contains(this.keyword));
    }

    if (satisfiesFilter) {
      spOutputCollector.collect(event);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

}
