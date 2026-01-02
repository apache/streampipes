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

package org.apache.streampipes.service.core.migrations.v099;

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
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MigrateAdaptersToUseScript implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MigrateAdaptersToUseScript.class);

  private IAdapterStorage adapterStorage;

  // Constructor-based Injection
  public MigrateAdaptersToUseScript(IAdapterStorage adapterStorage) {
    this.adapterStorage = adapterStorage;
  }

  // Use a default constructor if the migration framework requires it,
  // but point it to the singleton here.
  public MigrateAdaptersToUseScript() {
    this(StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage());
  }

  @Override
  // Execute if there is at least one adapter with rules defined
  public boolean shouldExecute() {
    List<AdapterDescription> adapters = adapterStorage.findAll();
    return adapters != null
        && adapters.stream()
                   .filter(adapter -> adapter != null)
                   .anyMatch(this::hasRules);
  }

  private boolean hasRules(AdapterDescription adapter) {
    return adapter.getRules() != null && !adapter.getRules().isEmpty();
  }

  @Override
  public void executeMigration() throws IOException {
    adapterStorage.findAll().forEach(this::migrateAndUpdateAdapter);
  }

  private void migrateAndUpdateAdapter(AdapterDescription adapterDescription) {
    LOG.info("Migrating adapter to script preprosessing: {}", adapterDescription.getName());

    migrateAdapterRules(adapterDescription);
    updateAdapter(adapterDescription);
  }

  private void migrateAdapterRules(AdapterDescription adapter) {
    // migration logic for a single adapter
    var config = initializeTransformationConfig();
    var scriptLines = new ArrayList<String>();

    // Sort rules by priority to maintain execution order
    List<TransformationRuleDescription> sortedRules = adapter.getRules().stream()
                                                             .sorted(Comparator.comparingInt(TransformationRuleDescription::getRulePriority))
                                                             .toList();

    for (var rule : sortedRules) {
      processRule(rule, adapter, config, scriptLines);
    }

    config.setScript(assembleFinalScript(scriptLines));
    adapter.setTransformationConfig(config);

    // Clear legacy rules after successful migration
    adapter.setRules(new ArrayList<>());
  }

  private void processRule(TransformationRuleDescription rule,
                           AdapterDescription adapter,
                           TransformationConfig config,
                           List<String> scriptLines) {

    if (rule instanceof RenameRuleDescription) {
      handleRenameRule((RenameRuleDescription) rule, scriptLines);

    } else if (rule instanceof DeleteRuleDescription) {
      handleDeleteRule((DeleteRuleDescription) rule, scriptLines);

    } else if (rule instanceof AddTimestampRuleDescription) {
      handleAddTimestampRule((AddTimestampRuleDescription) rule, scriptLines);

    } else if (rule instanceof AddValueTransformationRuleDescription) {
      handleAddValueRule((AddValueTransformationRuleDescription) rule, scriptLines);

    } else if (rule instanceof EventRateTransformationRuleDescription) {
      config.setReduceEventRateRule(mapEventRate((EventRateTransformationRuleDescription) rule));

    } else if (rule instanceof RemoveDuplicatesTransformationRuleDescription) {
      config.setRemoveDuplicateRule(mapDuplicates((RemoveDuplicatesTransformationRuleDescription) rule));

    } else if (rule instanceof ChangeDatatypeTransformationRuleDescription) {
      handleDatatypePlaceholder((ChangeDatatypeTransformationRuleDescription) rule, adapter, scriptLines);

    } else if (rule instanceof UnitTransformRuleDescription) {
      handleUnitPlaceholder((UnitTransformRuleDescription) rule, adapter, scriptLines);

    } else if (rule instanceof MoveRuleDescription) {
      scriptLines.add("// Move rule detected: Not supported in script migration");

    } else {
      scriptLines.add(String.format("// Unhandled rule type: %s", rule.getClass().getSimpleName()));
    }
  }

  private void handleRenameRule(RenameRuleDescription r, List<String> scriptLines) {
    scriptLines.add(String.format("event['%s'] = event['%s'];", r.getNewRuntimeKey(), r.getOldRuntimeKey()));
    scriptLines.add(String.format("delete event['%s'];", r.getOldRuntimeKey()));
  }

  private void handleDeleteRule(DeleteRuleDescription r, List<String> scriptLines) {
    scriptLines.add(String.format("delete event['%s'];", r.getRuntimeKey()));
  }

  private void handleAddTimestampRule(AddTimestampRuleDescription r, List<String> scriptLines) {
    scriptLines.add(String.format("event['%s'] = Date.now();", r.getRuntimeKey()));
  }

  private void handleAddValueRule(AddValueTransformationRuleDescription r, List<String> scriptLines) {
    scriptLines.add(String.format("event['%s'] = '%s';", r.getRuntimeKey(), r.getStaticValue()));
  }

  private void handleDatatypePlaceholder(ChangeDatatypeTransformationRuleDescription r,
                                         AdapterDescription adapter,
                                         List<String> scriptLines) {
//    scriptLines.add(String.format("// TODO: Check datatype for %s", r.getRuntimeName()));
    // TODO
//    updatePropertyMetadata(adapter, r.getRuntimeName());
  }

  private void handleUnitPlaceholder(UnitTransformRuleDescription r,
                                     AdapterDescription adapter,
                                     List<String> scriptLines) {
//    scriptLines.add(String.format("// TODO: Check unit conversion for %s", r.getRuntimeName()));
    // TODO
//    updatePropertyMetadata(adapter, r.getRuntimeName());
  }

  private ReduceEventRateRule mapEventRate(EventRateTransformationRuleDescription rule) {
    return new ReduceEventRateRule(rule.getAggregationTimeWindow(), rule.getAggregationType());
  }

  private RemoveDuplicateRule mapDuplicates(RemoveDuplicatesTransformationRuleDescription rule) {
    return new RemoveDuplicateRule(rule.getFilterTimeWindow());
  }

  private String assembleFinalScript(List<String> scriptLines) {
    var sb = new StringBuilder();
    sb.append("function transform(event) {\n");
    if (scriptLines.isEmpty()) {
      sb.append("  // No transformations defined\n");
    } else {
      scriptLines.forEach(line -> sb.append("  ").append(line).append("\n"));
    }
    sb.append("  return event;\n");
    sb.append("}");
    return sb.toString();
  }

  private TransformationConfig initializeTransformationConfig() {
    var config = new TransformationConfig();
    config.setLanguage("javascript");
    config.setInputs(new ArrayList<>());
    config.setOutputs(new ArrayList<>());
    return config;
  }

  private void updateAdapter(AdapterDescription adapterDescription) {
    adapterStorage.updateElement(adapterDescription);
  }

  @Override
  public String getDescription() {
    return "Changes the rules based adapters to use script based transformations instead.";
  }
}
