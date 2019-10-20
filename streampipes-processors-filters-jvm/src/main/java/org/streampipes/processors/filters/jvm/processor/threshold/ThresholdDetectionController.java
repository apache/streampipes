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

package org.streampipes.processors.filters.jvm.processor.threshold;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ThresholdDetectionController extends StandaloneEventProcessingDeclarer<ThresholdDetectionParameters> {

  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String VALUE = "value";
  private static final String OPERATION = "operation";

  private static final String RESULT_FIELD = "thresholdDetected";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.threshold")
            .category(DataProcessorType.FILTER)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                            Labels.withId(NUMBER_MAPPING),
                            PropertyScope.NONE).build())
            .outputStrategy(
                    OutputStrategies.append(
                            EpProperties.booleanEp(Labels.empty(), RESULT_FIELD, SO.Boolean)))
            .requiredSingleValueSelection(Labels.withId(OPERATION), Options.from("<", "<=", ">",
                    ">=", "==", "!="))
            .requiredFloatParameter(Labels.withId(VALUE), NUMBER_MAPPING)
            .build();

  }

  @Override
  public ConfiguredEventProcessor<ThresholdDetectionParameters> onInvocation
          (DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {
    Double threshold = extractor.singleValueParameter(VALUE, Double.class);
    String stringOperation = extractor.selectedSingleValue(OPERATION, String.class);

    String operation = "GT";

    if (stringOperation.equals("<=")) {
      operation = "LE";
    } else if (stringOperation.equals("<")) {
      operation = "LT";
    } else if (stringOperation.equals(">=")) {
      operation = "GE";
    } else if (stringOperation.equals("==")) {
      operation = "EQ";
    } else if (stringOperation.equals("!=")) {
      operation = "IE";
    }

    String filterProperty = extractor.mappingPropertyValue(NUMBER_MAPPING);

    ThresholdDetectionParameters staticParam = new ThresholdDetectionParameters(sepa,
            threshold,
            ThresholdDetectionOperator.valueOf(operation),
            filterProperty);

    return new ConfiguredEventProcessor<>(staticParam, ThresholdDetection::new);
  }
}
