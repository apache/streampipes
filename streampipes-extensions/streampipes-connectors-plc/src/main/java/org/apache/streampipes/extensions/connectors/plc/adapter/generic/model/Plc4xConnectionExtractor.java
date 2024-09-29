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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.model;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_CODE_BLOCK;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.plc.adapter.s7.config.ConfigurationParser;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Plc4xConnectionExtractor {

  private final IStaticPropertyExtractor extractor;
  private final String protocolCode;

  public Plc4xConnectionExtractor(IStaticPropertyExtractor extractor, String protocolCode) {
    this.extractor = extractor;
    this.protocolCode = protocolCode;
  }

  public Plc4xConnectionSettings makeSettings() {
    var host = extractHost();
    var transportCode = extractTransportCode();
    var transportConfigs = extractTransportMetadata(transportCode);
    var protocolConfigs = extractProtocolMetadata();
    var configParameters = makeConfigParameters(transportConfigs, protocolConfigs);

    return new Plc4xConnectionSettings(getConnectionString(host, transportCode, protocolCode, configParameters),
            extractor.singleValueParameter(Plc4xLabels.PLC_POLLING_INTERVAL, Integer.class), extractNodes());
  }

  private String getConnectionString(String host, String transportCode, String protocolCode, String parameters) {
    if (!parameters.isEmpty()) {
      parameters = "?" + parameters;
    }
    return String.format("%s:%s://%s%s", protocolCode, transportCode, host, parameters);
  }

  private String makeConfigParameters(Map<String, Object> transportConfigs, Map<String, Object> protocolConfigs) {
    Map<String, Object> mergedConfigs = Stream
            .concat(transportConfigs.entrySet().stream(), protocolConfigs.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value2));

    return mergedConfigs.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
  }

  private String extractHost() {
    return extractor.singleValueParameter(Plc4xLabels.PLC_IP, String.class);
  }

  private String extractTransportCode() {
    return extractor.selectedSingleValue(Plc4xLabels.SUPPORTED_TRANSPORTS, String.class);
  }

  private Map<String, Object> extractProtocolMetadata() {
    return extractMetadataAlternative("", Plc4xLabels.PROTOCOL_METADATA, Plc4xLabels.REQUIRED_GROUP_PROTOCOL,
            Plc4xLabels.ADVANCED_GROUP_PROTOCOL);
  }

  private Map<String, Object> extractTransportMetadata(String transportCode) {
    return extractMetadataAlternative(transportCode + ".", Plc4xLabels.TRANSPORT_METADATA,
            Plc4xLabels.REQUIRED_GROUP_TRANSPORT, Plc4xLabels.ADVANCED_GROUP_TRANSPORT);
  }

  private Map<String, Object> extractMetadataAlternative(String prefix, String labelId, String requiredGroupLabelId,
          String advancedGroupLabelId) {
    var selectedAlternative = extractor.selectedAlternativeInternalId(labelId);

    if (selectedAlternative.equals(Plc4xLabels.REQUIRED_OPTIONS)) {
      var group = extractor.getStaticPropertyByName(requiredGroupLabelId, StaticPropertyGroup.class);
      return toMetadataValueMap(prefix, group.getStaticProperties());
    } else {
      var group = extractor.getStaticPropertyByName(advancedGroupLabelId, StaticPropertyGroup.class);
      return toMetadataValueMap(prefix, group.getStaticProperties());
    }
  }

  private Map<String, Object> toMetadataValueMap(String prefix, List<StaticProperty> staticProperties) {
    var map = new HashMap<String, Object>();

    staticProperties.stream().filter(sp -> sp instanceof FreeTextStaticProperty).map(sp -> (FreeTextStaticProperty) sp)
            .forEach(sp -> {
              if (sp.getValue() != null && !sp.getValue().isEmpty()) {
                map.put(String.format("%s%s", prefix, sp.getInternalName()), sp.getValue());
              }
            });

    return map;
  }

  private Map<String, String> extractNodes() {
    var codePropertyInput = extractor.codeblockValue(PLC_CODE_BLOCK);
    return new ConfigurationParser().getNodeInformationFromCodeProperty(codePropertyInput);
  }
}
