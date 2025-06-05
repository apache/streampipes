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

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSwitchOperatorProcessor implements IStreamPipesSwitchProcessor {
  // Common Constants (moved from individual processors)
  public static final String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";
  public static final String SWITCH_FILTER_INPUT_FIELD_KEY = "switch-filter-key";
  public static final String SWITCH_CASE_VALUE_KEY = "switch-case-value"; // General name for case value key
  public static final String SWITCH_CASE_OPERATOR_KEY = "switch-case-value-operator"; // Specific to numerical,
  // but declared here for common parsing
  public static final String SWITCH_CASE_OUTPUT_VALUE_KEY = "switch-case-value-output";
  public static final String SWITCH_CASE_GROUP_KEY = "switch-case-group";
  public static final String OUTPUT_TYPE_SELECTION_KEY = "output-type";
  public static final String DEFAULT_OUTPUT_VALUE_KEY = "switch-case-value-default-output";

  // Common Instance Fields
  protected String selectedSwitchField;
  protected String selectedOutputType;
  protected List<SwitchCaseEntry> switchCaseEntries;
  protected String defaultOutputValue;

  // Common Methods

  /**
   * Extracts properties from a StaticPropertyGroup.
   * This helper is used internally when parsing collections of static properties.
   * @param staticProperty The StaticProperty to extract from.
   * @return A StaticPropertyExtractor instance for the given StaticProperty.
   */
  protected StaticPropertyExtractor getPropertyExtractor(StaticProperty staticProperty) {
    return StaticPropertyExtractor.from(
        ((StaticPropertyGroup) staticProperty).getStaticProperties(),
        new ArrayList<>()
    );
  }

  /**
   * Parses the collection of switch cases from the processor parameters.
   * @param params The IDataProcessorParameters containing the static property configurations.
   * @return A list of SwitchCaseEntry objects.
   **/
  protected List<SwitchCaseEntry> getSwitchCases(IDataProcessorParameters params) {
    List<SwitchCaseEntry> cases = new ArrayList<>();
    CollectionStaticProperty csp = (CollectionStaticProperty) params.extractor()
        .getStaticPropertyByName(SWITCH_CASE_GROUP_KEY);

    if (csp != null && csp.getMembers() != null) { // Added null checks for robustness
      for (StaticProperty sp : csp.getMembers()) {
        var propExtractor = getPropertyExtractor(sp);
        SwitchCaseEntry switchCaseEntry = parseSwitchCaseEntry(propExtractor); // Using new abstract method
        cases.add(switchCaseEntry);
      }
    }
    return cases;
  }
  protected abstract Object findMatchingResult(Event event);

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    var resultValue = findMatchingResult(event); // Calls the specific implementation

    // Add the result to the event and forward it
    switch (this.selectedOutputType) {
      case "String" -> event.addField(SWITCH_FILTER_OUTPUT_KEY, resultValue.toString());
      case "Boolean" -> {
        try {
          event.addField(SWITCH_FILTER_OUTPUT_KEY, Boolean.parseBoolean(resultValue.toString()));
        } catch (IllegalArgumentException e) {
          event.addField(SWITCH_FILTER_OUTPUT_KEY, false);
        }
      }
      case "Integer" -> {
        try {
          event.addField(SWITCH_FILTER_OUTPUT_KEY, Integer.parseInt(resultValue.toString()));
        } catch (NumberFormatException e) {
          event.addField(SWITCH_FILTER_OUTPUT_KEY, 0);
        }
      }
      default -> throw new IllegalArgumentException("Unsupported output type: " + this.selectedOutputType);
    }
    collector.collect(event);
  }

  /**
   * Parses a single SwitchCaseEntry from the provided StaticPropertyExtractor.
   * This method is abstract because the parsing logic for case value and operator varies
   * slightly between Boolean, Numerical, and String processors.
   * @param staticPropertyExtractor Extractor for the current switch case group.
   * @return A SwitchCaseEntry object.
   */
  protected SwitchCaseEntry parseSwitchCaseEntry(StaticPropertyExtractor staticPropertyExtractor) {
    return null;
  }

  /**
   * Determines the default result value based on the selected output type.
   * This method handles parsing the defaultOutputValue string into the correct type.
   * @return The default result value as an Object (String, Boolean, or Integer).
   */
  protected Object getDefaultResult() {
    return switch (this.selectedOutputType) {
      case "String" -> this.defaultOutputValue;
      case "Boolean" -> {
        if (this.defaultOutputValue != null && (this.defaultOutputValue.equalsIgnoreCase("true")
            || this.defaultOutputValue.equalsIgnoreCase("false"))) {
          yield Boolean.parseBoolean(this.defaultOutputValue);
        } else {
          yield false; // Default boolean value if parsing fails or input is null
        }
      }
      case "Integer" -> {
        try {
          yield Integer.parseInt(this.defaultOutputValue);
        } catch (NumberFormatException e) {
          yield 0; // Default integer value if parsing fails
        }
      }
      default -> throw new IllegalArgumentException("Unsupported output type: " + this.selectedOutputType);
    };
  }
}
