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

package org.apache.streampipes.extensions.connectors.camel.kamelet.module;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IExtensionModuleExport;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.connectors.camel.kamelet.factory.KameletSinkFactory;
import org.apache.streampipes.extensions.connectors.camel.kamelet.filter.KameletTemplateFilter;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.ClasspathKameletTemplateProvider;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCamelKameletModuleExport implements IExtensionModuleExport {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractCamelKameletModuleExport.class);

  private List<IStreamPipesPipelineElement<?>> pipelineElements;

  @Override
  public List<StreamPipesAdapter> adapters() {
    return Collections.emptyList();
  }

  @Override
  public synchronized List<IStreamPipesPipelineElement<?>> pipelineElements() {
    if (pipelineElements == null) {
      KameletTemplateProvider templateProvider = createTemplateProvider();
      pipelineElements = createSinkFactory(templateProvider).createSinks();

      if (pipelineElements.isEmpty()) {
        LOG.warn("No Camel Kamelet sinks discovered for module {}", getClass().getSimpleName());
      }
    }

    return pipelineElements;
  }

  @Override
  public List<IModelMigrator<?, ?>> migrators() {
    return Collections.emptyList();
  }

  protected KameletTemplateProvider createTemplateProvider() {
    return new ClasspathKameletTemplateProvider(getTemplateFilter());
  }

  protected KameletSinkFactory createSinkFactory(KameletTemplateProvider templateProvider) {
    return new KameletSinkFactory(templateProvider, getAppIdPrefix(), getAssetRoot());
  }

  protected String getAssetRoot() {
    return "camel-kamelets";
  }

  protected abstract String getAppIdPrefix();

  protected abstract KameletTemplateFilter getTemplateFilter();
}
