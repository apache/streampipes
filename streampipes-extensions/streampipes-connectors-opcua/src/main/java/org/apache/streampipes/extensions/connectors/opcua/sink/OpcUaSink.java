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

package org.apache.streampipes.extensions.connectors.opcua.sink;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtil;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.MAPPING_PROPERY;

public class OpcUaSink implements IStreamPipesDataSink, SupportsRuntimeConfig {

  private OpcUa opcUa;

  @Override
  public IDataSinkConfiguration declareConfig() {
    var builder = DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.opcua")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.FORWARD)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                Labels.withId(MAPPING_PROPERY),
                PropertyScope.NONE).build());

    SharedUserConfiguration.appendSharedOpcUaConfig(builder, false);

    return DataSinkConfiguration.create(
        OpcUaSink::new,
        builder.build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    var extractor = parameters.extractor();
    var config = SpOpcUaConfigExtractor.extractSinkConfig(extractor);

    String mappingPropertySelector = extractor.mappingPropertyValue(MAPPING_PROPERY.name());

    String mappingPropertyType = "";
    try {
      mappingPropertyType = extractor.getEventPropertyTypeBySelector(mappingPropertySelector);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }

    OpcUaParameters params = new OpcUaParameters(
        config,
        mappingPropertySelector,
        mappingPropertyType,
        config.getSelectedNodeNames().get(0)
    );

    this.opcUa = new OpcUa();
    this.opcUa.onInvocation(params);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    this.opcUa.onEvent(event);
  }

  @Override
  public void onPipelineStopped() {
    this.opcUa.onDetach();
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
    return OpcUaUtil.resolveConfiguration(staticPropertyInternalName, extractor);
  }
}
