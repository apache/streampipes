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

package org.apache.streampipes.connect.management.compact.generator;

import org.apache.streampipes.connect.shared.preprocessing.convert.ToOriginalSchemaConverter;
import org.apache.streampipes.manager.template.CompactConfigGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.connect.adapter.compact.CreateOptions;
import org.apache.streampipes.model.connect.adapter.compact.EnrichmentConfig;
import org.apache.streampipes.model.connect.adapter.compact.TransformationConfig;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompactAdapterGenerator {

  private final AdapterDescription adapterDescription;

  public CompactAdapterGenerator(AdapterDescription adapterDescription) {
    this.adapterDescription = adapterDescription;
  }

  public List<Map<String, Object>> getConfig() {
    var configs = new ArrayList<Map<String, Object>>();
    adapterDescription.getConfig().forEach(c -> {
      configs.add(new CompactConfigGenerator(c).toTemplateValue());
    });
    return configs;
  }

  public Map<String, CompactEventProperty> getSchema() {
    var map = new HashMap<String, CompactEventProperty>();
    var originalProperties = new ToOriginalSchemaConverter(
        adapterDescription.getEventSchema().getEventProperties()
    ).getTransformedProperties();
    originalProperties
        .forEach(ep -> map.put(ep.getRuntimeName(), new CompactEventProperty(
            ep.getLabel(),
            ep.getDescription(),
            ep.getPropertyScope(),
            ep.getSemanticType()
        )));
    return map;
  }

  public EnrichmentConfig getEnrichmentConfig() {
    if (hasTimestampEnrichmentRule()) {
      return new EnrichmentConfig(AdapterEnrichmentRuleGenerator.TIMESTAMP_FIELD);
    } else {
      return null;
    }
  }

  public TransformationConfig getTransformationConfig() {
    var renameRules = new HashMap<String, String>();
    var unitTransformRules = new HashMap<String, String>();
    if (hasTransformationRule()) {
      if (hasRule(RenameRuleDescription.class)) {
        var rules = getRules(RenameRuleDescription.class);
        rules.forEach(rule -> {
          renameRules.put(rule.getOldRuntimeKey(), rule.getNewRuntimeKey());
        });
      } else if (hasRule(UnitTransformRuleDescription.class)) {
        var rules = getRules(UnitTransformRuleDescription.class);
        rules.forEach(rule -> {
          unitTransformRules.put(rule.getRuntimeKey(), rule.getToUnitRessourceURL());
        });
      }
    }
    return new TransformationConfig(renameRules, unitTransformRules);
  }

  public CreateOptions getCreateOptions() {
    return new CreateOptions(
        true,
        true
    );
  }

  private boolean hasTimestampEnrichmentRule() {
    return hasRule(AddTimestampRuleDescription.class);
  }

  private boolean hasTransformationRule() {
    return adapterDescription.getRules().stream()
        .anyMatch(r -> hasRule(RenameRuleDescription.class) || hasRule(UnitTransformRuleDescription.class));
  }

  private boolean hasRule(Class<? extends TransformationRuleDescription> rule) {
    return adapterDescription.getRules().stream().anyMatch(r -> r.getClass().equals(rule));
  }

  private <T extends TransformationRuleDescription> List<T> getRules(Class<T> rule) {
    return adapterDescription.getRules()
        .stream()
        .filter(rule::isInstance)
        .map(rule::cast)
        .toList();
  }
}
