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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtensionItemProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionItemProvider.class);

  /**
   * Gather all extensions provided by this service.
   *
   * @return set of extension item descriptions
   */
  public Set<ExtensionItemDescription> getAllItemDescriptions() {
    return Stream.concat(
            DeclarersSingleton.getInstance().getDeclarers().values().stream()
                .map(declarer -> declarer.declareConfig().getDescription()),
            DeclarersSingleton.getInstance().getAdapters().stream().map(a -> a.declareConfig().getAdapterDescription())
        )
        .peek(this::applyLocales)
        .map(entity -> entity.toExtensionDescription(false, true, true))
        .collect(Collectors.toSet());
  }

  private void applyLocales(NamedStreamPipesEntity entity) {
    try {
      if (entity.isIncludesLocales()) {
        var labelGenerator = new LabelGenerator<>(entity);
        entity.setName(labelGenerator.getElementTitle());
        entity.setDescription(labelGenerator.getElementDescription());
      }
    } catch (IOException e) {
      LOG.warn("Could not read locales file for extension {}", entity.getAppId());
    }
  }
}
