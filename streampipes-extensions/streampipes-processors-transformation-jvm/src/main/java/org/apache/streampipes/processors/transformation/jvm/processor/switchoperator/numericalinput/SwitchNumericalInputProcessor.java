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
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.IStreamPipesSwitchProcessor;
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

import java.util.ArrayList;
import java.util.List;

public class SwitchNumericalInputProcessor implements IStreamPipesSwitchProcessor {
  private String selectedSwitchField;
  private String selectedOutputType;
  private List<SwitchCaseEntry> switchCases;
  private String defaultOutputValue;


  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        SwitchNumericalInputProcessor::new,
        ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.switchoperator.numerical"
                , 0)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.TRANSFORM)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.booleanReq(), Labels.withId(SWITCH_FILTER_KEY),
                    PropertyScope.MEASUREMENT_PROPERTY)
                .build()
            )
            .requiredSingleValueSelection(Labels.withId(OUTPUT_TYPE_KEY), Options.from("String", "Boolean", "Integer"))
            .requiredCollection(Labels.withId(SWITCH_CASE_GROUP),
                StaticProperties.singleValueSelection(Labels.withId(SWITCH_CASE_VALUE), Options.from("true", "false")),
                StaticProperties.freeTextProperty(Labels.withId(SWITCH_CASE_VALUE_OUTPUT), Datatypes.String))
            .outputStrategy(OutputStrategies.append(
                PrimitivePropertyBuilder.create(Datatypes.String, SWITCH_FILTER_OUTPUT_KEY).build())
            )
            .requiredTextParameter(Labels.withId(SWITCH_CASE_VALUE_DEFAULT_OUTPUT), "")
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params, SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    // Extract the selected field
    this.selectedSwitchField = params.extractor().mappingPropertyValue(SWITCH_FILTER_KEY);
    // Extract the output type
    this.selectedOutputType = params.extractor().selectedSingleValue(OUTPUT_TYPE_KEY, String.class);
    // Extract all switch case entries
    this.switchCases = this.getSwitchCases(params);
    // Extract the default output value
    this.defaultOutputValue = params.extractor().textParameter(SWITCH_CASE_VALUE_DEFAULT_OUTPUT);
  }

  private StaticPropertyExtractor getPropertyExtractor(StaticProperty staticProperty) {
    return StaticPropertyExtractor.from(
        ((StaticPropertyGroup) staticProperty).getStaticProperties(),
        new ArrayList<>()
    );
  }

  private SwitchCaseEntry getSwitchCaseEntry(StaticPropertyExtractor staticPropertyExtractor) {
    var switchCaseValue = staticPropertyExtractor.textParameter(SWITCH_CASE_VALUE);
    var switchCaseOutput = staticPropertyExtractor.textParameter(SWITCH_CASE_VALUE_OUTPUT);


    return new SwitchCaseEntry(switchCaseValue, switchCaseOutput);
  }

  private List<SwitchCaseEntry> getSwitchCases(IDataProcessorParameters params) {
    List<SwitchCaseEntry> switchCaseEntries = new ArrayList<>();
    CollectionStaticProperty csp = (CollectionStaticProperty) params.extractor()
        .getStaticPropertyByName(SWITCH_CASE_GROUP);

    for (StaticProperty sp : csp.getMembers()) {
      var propExtractor = getPropertyExtractor(sp);

      SwitchCaseEntry switchCaseEntry = getSwitchCaseEntry(propExtractor);

      switchCaseEntries.add(switchCaseEntry);
    }

    return switchCaseEntries;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    Boolean switchValue = event.getFieldBySelector(this.selectedSwitchField).getAsPrimitive().getAsBoolean();
    // Default result based on output type
    var resultValue = getDefaultResult();

    try {
      for (SwitchCaseEntry switchCase : this.switchCases) {
        if (switchValue == Boolean.parseBoolean(switchCase.getCaseValue())) {
          resultValue = switchCase.getOutputValue();
          break;
        }
      }
    } catch (Exception IllegalArgumentException) {
      // If any exception occurs, use the default result
      resultValue = getDefaultResult();
    }

    // Add the result to the event and forward it
    // Add the result to the event and forward it
    switch (this.selectedOutputType) {
      case "String" -> {
        if (!(resultValue instanceof String)) {
          event.addField(SWITCH_CASE_VALUE_OUTPUT, resultValue.toString());
        }
      }
      case "Boolean" -> {
        if (!(resultValue instanceof Boolean)) {
          event.addField(SWITCH_CASE_VALUE_OUTPUT, Boolean.parseBoolean(resultValue.toString()));
        }
      }
      case "Integer" -> {
        if (!(resultValue instanceof Integer)) {
          try {
            event.addField(SWITCH_CASE_VALUE_OUTPUT, Integer.parseInt(resultValue.toString()));
          } catch (NumberFormatException e) {
            event.addField(SWITCH_CASE_VALUE_OUTPUT, 0);
          }
        }
      }
      default -> throw new IllegalArgumentException("Unsupported output type: " + this.selectedOutputType);
    }
    collector.collect(event);
  }

  private Object getDefaultResult() {
    return switch (this.selectedOutputType) {
      case "String" -> this.defaultOutputValue;
      case "Boolean" -> {
        if (this.defaultOutputValue.equals("true") || this.defaultOutputValue.equals("false")) {
          yield Boolean.parseBoolean(this.defaultOutputValue);
        } else {
          yield false; // Default boolean value
        }
      }
      case "Integer" -> {
        try {
          yield Integer.parseInt(this.defaultOutputValue);
        } catch (NumberFormatException e) {
          yield 0; // Default integer value
        }
      }
      default -> throw new IllegalArgumentException("Unsupported output type: " + this.selectedOutputType);
    };
  }

  @Override
  public void onPipelineStopped() {
    this.switchCases = null;
    this.selectedOutputType = null;
    this.selectedSwitchField = null;
    this.defaultOutputValue = null;
  }
}
