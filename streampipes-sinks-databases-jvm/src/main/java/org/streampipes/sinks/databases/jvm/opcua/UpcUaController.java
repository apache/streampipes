/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.databases.jvm.opcua;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class UpcUaController extends StandaloneEventSinkDeclarer<OpcUaParameters> {

  private static final String OPC_SERVER_KEY = "opc_host";
  private static final String OPC_PORT_KEY = "opc_port";
  private static final String OPC_NAMESPACE_INDEX_KEY = "opc_namespace_index";
  private static final String OPC_NODE_ID_KEY = "opc_node_id_index";
  private static final String NUMBER_MAPPING_KEY = "number_mapping_key";


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.streampipes.sinks.databases.jvm.opcua")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.STORAGE)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                            Labels.withId(NUMBER_MAPPING_KEY),
                            PropertyScope.NONE).build())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .requiredTextParameter(Labels.withId(OPC_SERVER_KEY))
            .requiredIntegerParameter(Labels.withId(OPC_PORT_KEY))
            .requiredIntegerParameter(Labels.withId(OPC_NAMESPACE_INDEX_KEY))
            .requiredTextParameter(Labels.withId(OPC_NODE_ID_KEY))
            .build();
  }


  @Override
  public ConfiguredEventSink<OpcUaParameters> onInvocation(DataSinkInvocation graph,
                                                           DataSinkParameterExtractor extractor) {

    String hostname = extractor.singleValueParameter(OPC_SERVER_KEY, String.class);
    Integer port = extractor.singleValueParameter(OPC_PORT_KEY, Integer.class);

    String nodeId = extractor.singleValueParameter(OPC_NODE_ID_KEY, String.class);
    Integer nameSpaceIndex = extractor.singleValueParameter(OPC_NAMESPACE_INDEX_KEY, Integer.class);

    String numberMapping = extractor.mappingPropertyValue(NUMBER_MAPPING_KEY);

    OpcUaParameters params = new OpcUaParameters(graph, hostname, port, nodeId, nameSpaceIndex, numberMapping);

    return new ConfiguredEventSink<>(params, OpcUa::new);
  }

}
