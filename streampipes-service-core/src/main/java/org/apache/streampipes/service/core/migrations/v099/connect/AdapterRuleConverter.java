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

package org.apache.streampipes.service.core.migrations.v099.connect;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.ReduceEventRateRule;
import org.apache.streampipes.model.connect.RemoveDuplicateRule;
import org.apache.streampipes.model.connect.TransformationConfig;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.RegexTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.XSD;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

public class AdapterRuleConverter {

  public void processRule(
      TransformationRuleDescription rule,
      AdapterDescription adapter,
      TransformationConfig config,
      TransformationScriptBuilder scriptBuilder
  ) {

    if (rule instanceof RenameRuleDescription) {
      handleRenameRule((RenameRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof DeleteRuleDescription) {
      handleDeleteRule((DeleteRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof AddTimestampRuleDescription) {
      handleAddTimestampRule((AddTimestampRuleDescription) rule, adapter, scriptBuilder);

    } else if (rule instanceof AddValueTransformationRuleDescription) {
      handleAddValueRule((AddValueTransformationRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof EventRateTransformationRuleDescription) {
      config.setReduceEventRateRule(mapEventRate((EventRateTransformationRuleDescription) rule));

    } else if (rule instanceof RemoveDuplicatesTransformationRuleDescription) {
      config.setRemoveDuplicateRule(mapDuplicates((RemoveDuplicatesTransformationRuleDescription) rule));

    } else if (rule instanceof CorrectionValueTransformationRuleDescription) {
      handleCorrectionValueRule((CorrectionValueTransformationRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof RegexTransformationRuleDescription) {
      handleRegexRule((RegexTransformationRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof TimestampTranfsformationRuleDescription) {
      handleTimestampTransformationRule((TimestampTranfsformationRuleDescription) rule, scriptBuilder);

    } else if (rule instanceof ChangeDatatypeTransformationRuleDescription) {
      handleDatatype((ChangeDatatypeTransformationRuleDescription) rule, adapter, scriptBuilder);

    } else if (rule instanceof UnitTransformRuleDescription) {
      handleUnit((UnitTransformRuleDescription) rule, adapter, scriptBuilder);

    } else if (rule instanceof MoveRuleDescription) {
      scriptBuilder.appendLine("// Move rule detected: Not supported in script migration");

    } else {
      scriptBuilder.appendLine(String.format("// Unhandled rule type: %s", rule.getClass().getSimpleName()));
    }
  }

  private void handleRenameRule(RenameRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    scriptBuilder.appendLine(String.format("event['%s'] = event['%s'];", rule.getNewRuntimeKey(), rule.getOldRuntimeKey()));
    scriptBuilder.appendLine(String.format("delete event['%s'];", rule.getOldRuntimeKey()));
  }

  private void handleDeleteRule(DeleteRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    scriptBuilder.appendLine(String.format("delete event['%s'];", rule.getRuntimeKey()));
  }

  private void handleAddTimestampRule(AddTimestampRuleDescription rule, AdapterDescription adapter, TransformationScriptBuilder scriptBuilder) {
    // Add timestamp to the script
    scriptBuilder.appendLine(String.format("event['%s'] = new Date().getTime();", rule.getRuntimeKey()));
    if (adapter == null || rule.getRuntimeKey() == null || rule.getRuntimeKey().isEmpty()) {
      return;
    }

    // add origin type to ensure that timestamp will be transformed to a long
    findPrimitiveProperty(adapter, rule.getRuntimeKey()).ifPresent(property -> {
      ensureAdditionalMetadata(property);
      property.getAdditionalMetadata().put("originType", XSD.FLOAT.toString());
    });
  }

  private void handleAddValueRule(AddValueTransformationRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    scriptBuilder.appendLine(String.format("event['%s'] = '%s';", rule.getRuntimeKey(), rule.getStaticValue()));
  }

  private void handleCorrectionValueRule(CorrectionValueTransformationRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    var operator = "+";
    switch (rule.getOperator()) {
      case "MULTIPLY":
        operator = "*";
        break;
      case "ADD":
        operator = "+";
        break;
      case "SUBTRACT":
        operator = "-";
        break;
      case "DIVIDE":
        operator = "/";
        break;
    }

    var symbols = new DecimalFormatSymbols(Locale.US);
    var df = new DecimalFormat("0.########################", symbols);
    var formattedValue = df.format(rule.getCorrectionValue());

    scriptBuilder.appendLine(String.format("event['%s'] = Number(event['%s']) %s %s;",
        rule.getRuntimeKey(), rule.getRuntimeKey(), operator, formattedValue));
  }

  private void handleRegexRule(RegexTransformationRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    scriptBuilder.appendLine(String.format("event['%s'] = String(event['%s']).replace(new RegExp('%s', 'g'), '%s');",
        rule.getRuntimeKey(), rule.getRuntimeKey(), rule.getRegex(), rule.getReplaceWith()));
  }

  private void handleTimestampTransformationRule(TimestampTranfsformationRuleDescription rule, TransformationScriptBuilder scriptBuilder) {
    if ("timeUnit".equals(rule.getMode())) {
      scriptBuilder.appendLine(String.format("event['%s'] = Number(event['%s']) * %d;",
          rule.getRuntimeKey(), rule.getRuntimeKey(), rule.getMultiplier()));
    } else if ("formatString".equals(rule.getMode())) {
      scriptBuilder.appendLine(String.format("if (event['%s']) { event['%s'] = new Date(event['%s']).getTime(); }",
          rule.getRuntimeKey(), rule.getRuntimeKey(), rule.getRuntimeKey()));
      scriptBuilder.appendLine(String.format("// Target format hint: %s", rule.getFormatString()));
    }
  }

  private void handleDatatype(
      ChangeDatatypeTransformationRuleDescription rule,
      AdapterDescription adapter,
      TransformationScriptBuilder scriptBuilder
  ) {
    findPrimitiveProperty(adapter, rule.getRuntimeKey()).ifPresent(property -> {
      // Update metadata and type
      property.getAdditionalMetadata().put("originType", rule.getOriginalDatatypeXsd());
      property.setRuntimeType(rule.getTargetDatatypeXsd());

      // Document the change in the script for the user
      scriptBuilder.appendLine(String.format("// Datatype for '%s' migrated to %s",
          rule.getRuntimeKey(), rule.getTargetDatatypeXsd()));
    });
  }

  private void handleUnit(
      UnitTransformRuleDescription rule,
      AdapterDescription adapter,
      TransformationScriptBuilder scriptBuilder
  ) {
    findPrimitiveProperty(adapter, rule.getRuntimeKey()).ifPresent(property -> {
      property.getAdditionalMetadata().put("fromMeasurementUnit", rule.getFromUnitRessourceURL());
      property.getAdditionalMetadata().put("toMeasurementUnit", rule.getToUnitRessourceURL());
    });
  }

  private Optional<EventPropertyPrimitive> findPrimitiveProperty(AdapterDescription adapter, String runtimeKey) {
    return Optional.ofNullable(adapter.getDataStream())
        .map(SpDataStream::getEventSchema)
        .map(EventSchema::getEventProperties)
        .orElse(Collections.emptyList())
        .stream()
        .filter(ep -> ep.getRuntimeName().equals(runtimeKey))
        .filter(EventPropertyPrimitive.class::isInstance)
        .map(EventPropertyPrimitive.class::cast)
        .findFirst();
  }

  private void ensureAdditionalMetadata(EventPropertyPrimitive property) {
    if (property.getAdditionalMetadata() == null) {
      property.setAdditionalMetadata(new HashMap<>());
    }
  }

  private ReduceEventRateRule mapEventRate(EventRateTransformationRuleDescription rule) {
    return new ReduceEventRateRule(rule.getAggregationTimeWindow(), rule.getAggregationType());
  }

  private RemoveDuplicateRule mapDuplicates(RemoveDuplicatesTransformationRuleDescription rule) {
    return new RemoveDuplicateRule(rule.getFilterTimeWindow());
  }
}
