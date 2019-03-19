/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.processors.filters.jvm.processor.numericalfilter;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class NumericalFilterController extends StandaloneEventProcessingDeclarer<NumericalFilterParameters> {

  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String VALUE = "value";
  private static final String OPERATION = "operation";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.numericalfilter", "Numerical Filter", "Numerical Filter Description")
            .category(DataProcessorType.FILTER)
            .providesAssets()
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(), Labels.from(NUMBER_MAPPING, "Specifies the field name where the filter operation should" +
                    " be applied on.", ""), PropertyScope.NONE).build())
            .outputStrategy(OutputStrategies.keep())
            .requiredSingleValueSelection(Labels.from(OPERATION, "Filter Operation", "Specifies the filter " +
                    "operation that should be applied on the field"), Options.from("<", "<=", ">", ">=", "=="))
            .requiredFloatParameter(Labels.from(VALUE, "Threshold value", "Specifies a threshold value."), "number")
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();

  }

  @Override
  public ConfiguredEventProcessor<NumericalFilterParameters> onInvocation
          (DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {
    Double threshold = extractor.singleValueParameter(VALUE, Double.class);
    String stringOperation = extractor.selectedSingleValue(OPERATION, String.class);

    String operation = "GT";

    if (stringOperation.equals("<=")) {
      operation = "LT";
    } else if (stringOperation.equals("<")) {
      operation = "LE";
    } else if (stringOperation.equals(">=")) {
      operation = "GE";
    } else if (stringOperation.equals("==")) {
      operation = "EQ";
    }

    String filterProperty = extractor.mappingPropertyValue(NUMBER_MAPPING);

    NumericalFilterParameters staticParam = new NumericalFilterParameters(sepa,
            threshold,
            NumericalOperator.valueOf(operation),
            filterProperty);

    return new ConfiguredEventProcessor<>(staticParam, NumericalFilter::new);
  }
}
