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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.config;

import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_GROUP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_QUERY;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_RESULTS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_TAGS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.MANUAL_TAGS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_IP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PROTOCOL_METADATA;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.SUPPORTED_TRANSPORTS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.TAG_INPUT;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.TRANSPORT_METADATA;

public class PlcTagConfiguration {

  public static StaticProperty make(StaticProperty manualTags, boolean hasProtocolMetadata, String protocol) {
    var dependencies = new ArrayList<>(List.of(PLC_IP, SUPPORTED_TRANSPORTS, TRANSPORT_METADATA, BROWSE_QUERY));
    if (hasProtocolMetadata) {
      dependencies.add(PROTOCOL_METADATA);
    }
    var query = StaticProperties.stringFreeTextProperty(Labels.withId(BROWSE_QUERY));
    query.setValue(browseQuery(protocol, "*"));
    return StaticProperties.alternatives(Labels.withId(TAG_INPUT),
        Alternatives.from(Labels.withId(MANUAL_TAGS), manualTags, true),
        Alternatives.from(Labels.withId(BROWSE_TAGS), StaticProperties.group(
            Labels.withId(BROWSE_GROUP), false, query,
            StaticProperties.runtimeResolvableTreeInput(Labels.withId(BROWSE_RESULTS),
                dependencies,
                false, true))));
  }
  public static String browseQuery(String protocol, String query) {
    // Older generic configurations used a wildcard that OPC UA interprets as an invalid NodeId.
    return "opcua".equals(protocol) && "*".equals(query.trim()) ? "ns=0;i=85" : query;
  }
}
