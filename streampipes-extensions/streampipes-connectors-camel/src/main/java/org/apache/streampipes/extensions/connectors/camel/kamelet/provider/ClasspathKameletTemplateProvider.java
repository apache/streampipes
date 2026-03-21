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

package org.apache.streampipes.extensions.connectors.camel.kamelet.provider;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.connectors.camel.kamelet.discovery.KameletTemplateDiscovery;
import org.apache.streampipes.extensions.connectors.camel.kamelet.filter.KameletTemplateFilter;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClasspathKameletTemplateProvider implements KameletTemplateProvider {

  private final KameletTemplateDiscovery templateDiscovery;
  private final KameletTemplateFilter templateFilter;

  private Map<String, KameletTemplate> templates;

  public ClasspathKameletTemplateProvider(KameletTemplateFilter templateFilter) {
    this.templateDiscovery = new KameletTemplateDiscovery();
    this.templateFilter = templateFilter;
  }

  @Override
  public synchronized List<KameletTemplate> getTemplates() {
    return List.copyOf(loadTemplates().values());
  }

  @Override
  public synchronized Optional<KameletTemplate> getTemplate(String templateName) {
    return Optional.ofNullable(loadTemplates().get(templateName));
  }

  private Map<String, KameletTemplate> loadTemplates() {
    if (templates == null) {
      try {
        templates = new LinkedHashMap<>(templateDiscovery.discoverTemplates(templateFilter));
      } catch (SpConfigurationException e) {
        throw new SpRuntimeException("Could not discover registered Kamelet templates", e);
      }
    }

    return templates;
  }
}
