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

import org.apache.streampipes.manager.template.CompactConfigGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.connect.adapter.compact.CreateOptions;
import org.apache.streampipes.model.util.Cloner;

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
    var properties = new Cloner().properties(adapterDescription.getEventSchema().getEventProperties());
    properties
        .forEach(ep -> map.put(ep.getRuntimeName(), new CompactEventProperty(
            ep.getLabel(),
            ep.getDescription(),
            ep.getPropertyScope(),
            ep.getSemanticType()
        )));
    return map;
  }

  public CreateOptions getCreateOptions() {
    return new CreateOptions(
        true,
        true
    );
  }
}
