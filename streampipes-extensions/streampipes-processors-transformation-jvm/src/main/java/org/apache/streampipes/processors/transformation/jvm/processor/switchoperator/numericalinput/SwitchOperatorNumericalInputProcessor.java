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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.numericalinput;

import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.AbstractSwitchOperatorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.LogicalOperator;
import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.NumericalSwitchCaseEntry;
import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.SwitchCaseEntry;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Datatypes;

public class SwitchOperatorNumericalInputProcessor extends AbstractSwitchOperatorProcessor {

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        SwitchOperatorNumericalInputProcessor::new,
        ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.switchoperator.numerical"
                , 0)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.TRANSFORM)
            .requiredStream(
                StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        EpRequirements.datatypeReq(Datatypes.Number),
                        Labels.withId(SWITCH_FILTER_INPUT_FIELD_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                    .build()
            )
            .requiredSingleValueSelection(
                Labels.withId(OUTPUT_TYPE_SELECTION_KEY), Options.from("String", "Boolean", "Integer"))
            .requiredCollection(
                Labels.withId(SWITCH_CASE_GROUP_KEY), StaticProperties.doubleFreeTextProperty(
                    Labels.withId(SWITCH_CASE_VALUE_KEY)
                ),
                StaticProperties.singleValueSelection(
                    Labels.withId(SWITCH_CASE_OPERATOR_KEY),
                    Options.from("==", "!=", "<", "<=", ">", ">=")
                ),
                StaticProperties.freeTextProperty(
                    Labels.withId(SWITCH_CASE_OUTPUT_VALUE_KEY),
                    Datatypes.String
                )
            )
            .outputStrategy(
                OutputStrategies.append(
                    PrimitivePropertyBuilder.create(Datatypes.String, SWITCH_FILTER_OUTPUT_KEY).build()
                )
            )
            .requiredTextParameter(Labels.withId(DEFAULT_OUTPUT_VALUE_KEY), "")
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params, SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    // Extract the selected field
    this.selectedSwitchField = params.extractor().mappingPropertyValue(SWITCH_FILTER_INPUT_FIELD_KEY);
    // Extract the output type
    this.selectedOutputType = params.extractor().selectedSingleValue(OUTPUT_TYPE_SELECTION_KEY, String.class);
    // Extract all switch case entries
    this.switchCaseEntries = this.getSwitchCases(params);
    // Extract the default output value
    this.defaultOutputValue = params.extractor().textParameter(DEFAULT_OUTPUT_VALUE_KEY);
  }

  @Override
  protected SwitchCaseEntry parseSwitchCaseEntry(StaticPropertyExtractor staticPropertyExtractor) {
    // Specific implementation for NumericalInputProcessor
    var switchCaseValue = staticPropertyExtractor.textParameter(SWITCH_CASE_VALUE_KEY);
    var switchCaseOperator = staticPropertyExtractor.selectedSingleValue(SWITCH_CASE_OPERATOR_KEY, String.class);
    var switchCaseOutput = staticPropertyExtractor.textParameter(SWITCH_CASE_OUTPUT_VALUE_KEY);
    return new NumericalSwitchCaseEntry(switchCaseValue, switchCaseOutput, switchCaseOperator);
  }

  @Override
  protected Object findMatchingResult(Event event) {
    Double switchValue = event.getFieldBySelector(this.selectedSwitchField).getAsPrimitive().getAsDouble();
    Object resultValue = getDefaultResult();

    try {
      for (SwitchCaseEntry entry : this.switchCaseEntries) {
        // Must cast to NumericalSwitchCaseEntry to get the operator
        if (entry instanceof NumericalSwitchCaseEntry numericalCase) {
          if (LogicalOperator.evaluate(numericalCase.getOperator(), switchValue, numericalCase.getCaseValue())) {
            resultValue = numericalCase.getOutputValue();
            break;
          }
        }
      }
    } catch (Exception e) {
      resultValue = getDefaultResult();
    }
    return resultValue;
  }

  @Override
  public void onPipelineStopped() {
    this.switchCaseEntries = null;
    this.selectedOutputType = null;
    this.selectedSwitchField = null;
    this.defaultOutputValue = null;
  }
}
