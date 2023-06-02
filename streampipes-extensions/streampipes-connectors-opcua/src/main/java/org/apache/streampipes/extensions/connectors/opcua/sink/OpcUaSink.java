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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

public class OpcUaSink implements IStreamPipesDataSink {

  private static final String OPC_SERVER_KEY = "opc_host";
  private static final String OPC_PORT_KEY = "opc_port";
  private static final String OPC_NAMESPACE_INDEX_KEY = "opc_namespace_index";
  private static final String OPC_NODE_ID_KEY = "opc_node_id_index";
  private static final String MAPPING_PROPERTY_KEY = "mapping_property_key";

  private OpcUa opcUa;

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        OpcUaSink::new,
        DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.opcua")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.FORWARD)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                    Labels.withId(MAPPING_PROPERTY_KEY),
                    PropertyScope.NONE).build())
            .requiredTextParameter(Labels.withId(OPC_SERVER_KEY))
            .requiredIntegerParameter(Labels.withId(OPC_PORT_KEY))
            .requiredIntegerParameter(Labels.withId(OPC_NAMESPACE_INDEX_KEY))
            .requiredTextParameter(Labels.withId(OPC_NODE_ID_KEY))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    var extractor = parameters.extractor();
    String hostname = extractor.singleValueParameter(OPC_SERVER_KEY, String.class);
    Integer port = extractor.singleValueParameter(OPC_PORT_KEY, Integer.class);

    String nodeId = extractor.singleValueParameter(OPC_NODE_ID_KEY, String.class);
    Integer nameSpaceIndex = extractor.singleValueParameter(OPC_NAMESPACE_INDEX_KEY, Integer.class);

    String mappingPropertySelector = extractor.mappingPropertyValue(MAPPING_PROPERTY_KEY);

    String mappingPropertyType = "";
    try {
      mappingPropertyType = extractor.getEventPropertyTypeBySelector(mappingPropertySelector);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }

    OpcUaParameters params = new OpcUaParameters(hostname, port, nodeId, nameSpaceIndex,
        mappingPropertySelector, mappingPropertyType);

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
}
