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

package org.apache.streampipes.service.core.migrations.v099.connect;

import org.apache.streampipes.model.connect.TransformationConfig;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    this(StorageDispatcher.INSTANCE.getNoSqlStore()
                                   .getAdapterInstanceStorage());
  }

  @Override
  // Execute if there is at least one adapter with rules defined
  public boolean shouldExecute() {
    List<AdapterDescription> adapters = adapterStorage.findAll();
    return adapters != null
        && adapters.stream()
                   .anyMatch(adapter -> adapter.getTransformationConfig() == null);
  }

  private boolean hasRules(AdapterDescription adapter) {
    return adapter.getRules() != null && !adapter.getRules()
                                                 .isEmpty();
  }

  @Override
  public void executeMigration() throws IOException {
    adapterStorage.findAll()
                  .forEach(this::migrateAndUpdateAdapter);
  }

  @Override
  public String getDescription() {
    return "Changes the rules based adapters to use script based transformations instead.";
  }

  private void migrateAndUpdateAdapter(AdapterDescription adapterDescription) {
    LOG.info("Migrating adapter to script preprosessing: {}", adapterDescription.getName());

    migrateAdapter(adapterDescription);
    updateAdapter(adapterDescription);
  }

  private void migrateAdapter(AdapterDescription adapter) {

    removeAdditionalMetadata(adapter);

    // migration logic for a single adapter
    var config = initializeTransformationConfig();
    var scriptLines = new ArrayList<String>();

    var scriptBuilder = TransformationScriptBuilder.create();

    // Sort rules by priority to maintain execution order
    List<TransformationRuleDescription> sortedRules = adapter.getRules()
                                                             .stream()
                                                             .sorted(Comparator.comparingInt(
                                                                 TransformationRuleDescription::getRulePriority))
                                                             .toList();

    for (var rule : sortedRules) {
      new AdapterRuleConverter().processRule(rule, adapter, config, scriptLines);
    }

    config.setScript(scriptBuilder.build());
    adapter.setTransformationConfig(config);

    // Clear legacy rules after successful migration
    adapter.setRules(new ArrayList<>());
  }

  private void removeAdditionalMetadata(AdapterDescription adapter) {
    if (adapter.getDataStream() != null && adapter.getDataStream()
                                                  .getEventSchema() != null) {
      adapter.getDataStream()
             .getEventSchema()
             .getEventProperties()
             .forEach(prop -> {
               prop.setAdditionalMetadata(new HashMap<>());
             });
    }
  }

  // TODO
//  private String assembleFinalScript(List<String> scriptLines) {
//    var sb = new StringBuilder();
//    sb.append("function transform(event) {\n");
//    if (scriptLines.isEmpty()) {
//      sb.append("  // No transformations defined\n");
//    } else {
//      scriptLines.forEach(line -> sb.append("  ")
//                                    .append(line)
//                                    .append("\n"));
//    }
//    sb.append("  return event;\n");
//    sb.append("}");
//    return sb.toString();
//  }

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


}
