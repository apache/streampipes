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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.List;


public class SwitchOperatorProcessor implements IStreamPipesDataProcessor {
  private static final String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";
  private static final String SWITCH_FILTER_KEY = "switch-filter-key";
  private static final String SWITCH_CASE_VALUE = "switch-case-value";
  private static final String SWITCH_CASE_VALUE_OUTPUT = "switch-case-value-output";
  private static final String SWITCH_CASE_GROUP = "switch-case-group";
  private static final String OUTPUT_TYPE_KEY = "output-type";
  private static final String SWITCH_CASE_VALUE_DEFAULT_OUTPUT = "switch-case-value-default-output";

  // Fields to store runtime parameters
  private String selectedField;
  private List<SwitchCaseEntry> switchCases;
  private String outputType;

  // Inner class to store switch case entries
  private static class SwitchCaseEntry {
    private final String caseValue;
    private final Object outputValue;

    public SwitchCaseEntry(String caseValue, Object outputValue) {
      this.caseValue = caseValue;
      this.outputValue = outputValue;
    }

    public String getCaseValue() {
      return caseValue;
    }

    public Object getOutputValue() {
      return outputValue;
    }
  }

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        SwitchOperatorProcessor::new,
        ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.switchoperator", 0)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.TRANSFORM)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId(SWITCH_FILTER_KEY),
                    PropertyScope.MEASUREMENT_PROPERTY)
                .build()
            )
            .requiredSingleValueSelection(Labels.withId(OUTPUT_TYPE_KEY), Options.from("String", "Boolean", "Integer"))
            .requiredCollection(Labels.withId(SWITCH_CASE_GROUP),
                StaticProperties.freeTextProperty(Labels.withId(SWITCH_CASE_VALUE), Datatypes.String),
                StaticProperties.freeTextProperty(Labels.withId(SWITCH_CASE_VALUE_OUTPUT), Datatypes.String))
            .requiredTextParameter(Labels.withId(SWITCH_CASE_VALUE_DEFAULT_OUTPUT), "")
            .outputStrategy(OutputStrategies.append(
                PrimitivePropertyBuilder.create(Datatypes.String, SWITCH_FILTER_OUTPUT_KEY).build())
            )
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params, SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    // Extract the selected field
    this.selectedField = params.extractor().mappingPropertyValue(SWITCH_FILTER_KEY);

    // Extract the output type
    this.outputType = params.extractor().selectedSingleValue(OUTPUT_TYPE_KEY, String.class);

    // Extract all switch case entries
    this.switchCases = new java.util.ArrayList<>();

    String caseValue = params.extractor().textParameter(SWITCH_CASE_VALUE);
    Object outputValue = parseOutputValue(params.extractor().selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT,
        String.class));

    switchCases.add(new SwitchCaseEntry(caseValue, outputValue));
  }

  private Object parseOutputValue(String outputValueStr) {
    return switch (outputType) {
      case "String" -> outputValueStr;
      case "Boolean" -> Boolean.parseBoolean(outputValueStr);
      case "Integer" -> Integer.parseInt(outputValueStr);
      default -> throw new IllegalArgumentException("Unsupported output type: " + outputType);
    };
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    // Default result based on output type
    Object result = getDefaultResult();

    try {
      // Get the value of the selected field from the event
      if (!this.selectedField.isEmpty()) {
        String fieldValue = null;

        // Handle possible null values gracefully
        if (event.getFieldBySelector(selectedField) != null
            && event.getFieldBySelector(selectedField).getAsPrimitive() != null) {
          fieldValue = event.getFieldBySelector(selectedField).getAsPrimitive().getAsString();
        }

        // Compare the field value with each case value
        if (fieldValue != null) {
          for (SwitchCaseEntry entry : switchCases) {
            if (fieldValue.equals(entry.getCaseValue())) {
              result = entry.getOutputValue();
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      // Use default result if any exception occurs
    }

    // Add the result to the event and forward it
    event.addField(SWITCH_FILTER_OUTPUT_KEY, result);
    collector.collect(event);
  }

  private Object getDefaultResult() {
    return switch (outputType) {
      case "String" -> "";
      case "Boolean" -> false;
      case "Integer" -> 0;
      default -> throw new IllegalArgumentException("Unsupported output type: " + outputType);
    };
  }

  @Override
  public void onPipelineStopped() {
    // Cleanup logic here
  }
}