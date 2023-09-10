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

package org.apache.streampipes.smp.extractor;

import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.config.IPipelineElementConfiguration;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;

import org.apache.maven.artifact.DependencyResolutionRequiredException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtensionsFinder {

  private final ClassLoader loader;
  private final String initClass;

  public ExtensionsFinder(ClassLoader loader,
                          String initClass) {
    this.loader = loader;
    this.initClass = initClass;
  }

  public List<AssetModel> findExtensions()
      throws MalformedURLException, DependencyResolutionRequiredException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    var extensions = new ArrayList<AssetModel>();
    var serviceDef = ((ExtensionsModelSubmitter) loader.loadClass(initClass).newInstance()).provideServiceDefinition();

    extensions.addAll(findAdapters(serviceDef));
    extensions.addAll(findPipelineElements(serviceDef, IDataProcessorConfiguration.class, PeType.PROCESSOR));
    extensions.addAll(findPipelineElements(serviceDef, IDataSinkConfiguration.class, PeType.SINK));

    return extensions;
  }

  private List<AssetModel> findPipelineElements(SpServiceDefinition serviceDef,
                                                Class<? extends IPipelineElementConfiguration<?, ?>> configType,
                                                PeType peType) {
    return serviceDef.getDeclarers()
        .stream()
        .map(IStreamPipesPipelineElement::declareConfig)
        .filter(configType::isInstance)
        .map(config -> new AssetModel(config.getDescription().getAppId(), peType)).toList();
  }

  private Collection<? extends AssetModel> findAdapters(SpServiceDefinition serviceDef) {
    return serviceDef.getAdapters().stream().map(adapter -> {
      var config = adapter.declareConfig();
      return new AssetModel(config.getAdapterDescription().getAppId(), PeType.ADAPTER);
    }).toList();
  }
}
