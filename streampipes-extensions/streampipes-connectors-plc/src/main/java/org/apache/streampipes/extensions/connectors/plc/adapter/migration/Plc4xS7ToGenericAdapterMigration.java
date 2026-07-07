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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.AdapterConfigurationProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Plc4xS7ToGenericAdapterMigration implements IAdapterMigrator {

  private static final String OLD_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";
  static final String NEW_APP_ID = AdapterConfigurationProvider.ID + "s7";

  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";
  private static final String PLC_NODE_INPUT_ALTERNATIVES = "plc_node_input_alternatives";
  private static final String PLC_NODE_INPUT_COLLECTION_ALTERNATIVE = "plc_node_input_collection_alternative";

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        OLD_APP_ID,
        SpServiceTagPrefix.ADAPTER,
        1,
        1
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var ipAddress = extractor.singleValueParameter(PLC_IP, String.class);
    var pollingInterval = extractor.singleValueParameter(PLC_POLLING_INTERVAL, Integer.class);

    var splitAddress = SplitPlcAddress.from(ipAddress);

    element.setAppId(NEW_APP_ID);
    element.setConfig(List.of(
        StaticProperties.stringFreeTextProperty(Labels.withId(Plc4xLabels.PLC_IP), splitAddress.host()),
        StaticProperties.integerFreeTextProperty(Labels.withId(Plc4xLabels.PLC_POLLING_INTERVAL), pollingInterval),
        makeSupportedTransports(),
        makeTransportMetadata(),
        makeProtocolMetadata(splitAddress.queryParameters()),
        makeCodeBlock(extractor)
    ));

    return MigrationResult.success(element);
  }

  private StaticProperty makeSupportedTransports() {
    return StaticProperties.singleValueSelection(
        Labels.withId(Plc4xLabels.SUPPORTED_TRANSPORTS),
        List.of(new Option("tcp", true))
    );
  }

  private StaticProperty makeTransportMetadata() {
    return StaticProperties.alternatives(
        Labels.withId(Plc4xLabels.TRANSPORT_METADATA),
        Alternatives.from(
            Labels.withId(Plc4xLabels.REQUIRED_OPTIONS),
            makeRuntimeResolvableGroup(Plc4xLabels.REQUIRED_GROUP_TRANSPORT),
            true
        ),
        Alternatives.from(
            Labels.withId(Plc4xLabels.ADVANCED_OPTIONS),
            makeRuntimeResolvableGroup(Plc4xLabels.ADVANCED_GROUP_TRANSPORT)
        )
    );
  }

  private StaticProperty makeRuntimeResolvableGroup(String internalName) {
    var group = StaticProperties.runtimeResolvableGroupStaticProperty(
        Labels.withId(internalName),
        List.of(Plc4xLabels.SUPPORTED_TRANSPORTS)
    );
    group.setStaticProperties(List.of());
    group.setHorizontalRendering(false);
    return group;
  }

  private StaticProperty makeProtocolMetadata(List<QueryParameter> queryParameters) {
    return StaticProperties.alternatives(
        Labels.withId(Plc4xLabels.PROTOCOL_METADATA),
        Alternatives.from(
            Labels.withId(Plc4xLabels.REQUIRED_OPTIONS),
            makeProtocolGroup(Plc4xLabels.REQUIRED_GROUP_PROTOCOL, queryParameters),
            true
        ),
        Alternatives.from(
            Labels.withId(Plc4xLabels.ADVANCED_OPTIONS),
            makeProtocolGroup(Plc4xLabels.ADVANCED_GROUP_PROTOCOL, List.of())
        )
    );
  }

  private StaticProperty makeProtocolGroup(String internalName,
                                           List<QueryParameter> queryParameters) {
    var group = StaticProperties.group(
        Labels.withId(internalName),
        false,
        queryParameters.stream()
            .map(this::makeProtocolParameter)
            .toArray(StaticProperty[]::new)
    );
    group.setHorizontalRendering(false);
    return group;
  }

  private StaticProperty makeProtocolParameter(QueryParameter queryParameter) {
    var property = StaticProperties.freeTextProperty(
        Labels.from(queryParameter.name(), queryParameter.name(), ""),
        Datatypes.String
    );
    property.setOptional(true);
    property.setValue(queryParameter.value());
    return property;
  }

  private StaticProperty makeCodeBlock(IStaticPropertyExtractor extractor) {
    var codeBlock = StaticProperties.codeStaticProperty(
        Labels.withId(Plc4xLabels.PLC_CODE_BLOCK),
        CodeLanguage.None,
        ""
    );
    codeBlock.setValue(makeTags(extractor));
    return codeBlock;
  }

  private String makeTags(IStaticPropertyExtractor extractor) {
    var selectedAlternative = extractor.selectedAlternativeInternalId(PLC_NODE_INPUT_ALTERNATIVES);
    if (Objects.equals(selectedAlternative, PLC_NODE_INPUT_COLLECTION_ALTERNATIVE)) {
      var nodes = extractor.getStaticPropertyByName(PLC_NODES, CollectionStaticProperty.class);
      return makeTagsFromCollection(nodes);
    } else {
      return extractor.codeblockValue(Plc4xLabels.PLC_CODE_BLOCK);
    }
  }

  private String makeTagsFromCollection(CollectionStaticProperty nodes) {
    var tags = new ArrayList<String>();
    for (StaticProperty member : nodes.getMembers()) {
      var memberExtractor =
          StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());
      tags.add("%s=%s:%s".formatted(
          memberExtractor.textParameter(PLC_NODE_RUNTIME_NAME),
          memberExtractor.textParameter(PLC_NODE_NAME),
          memberExtractor.selectedSingleValue(PLC_NODE_TYPE, String.class)
              .toUpperCase()
              .replaceAll(" ", "_")
      ));
    }
    return String.join(System.lineSeparator(), tags);
  }

  private record SplitPlcAddress(String host,
                                 List<QueryParameter> queryParameters) {

    static SplitPlcAddress from(String address) {
      var splitAddress = address.split("\\?", 2);
      var host = splitAddress[0];
      var queryParameters = splitAddress.length == 2
          ? parseQueryParameters(splitAddress[1])
          : List.<QueryParameter>of();

      return new SplitPlcAddress(host, queryParameters);
    }

    private static List<QueryParameter> parseQueryParameters(String query) {
      if (query == null || query.isBlank()) {
        return List.of();
      }
      return Arrays.stream(query.split("&"))
          .map(QueryParameter::from)
          .filter(Objects::nonNull)
          .toList();
    }
  }

  private record QueryParameter(String name,
                                String value) {

    static QueryParameter from(String queryParameter) {
      var splitParameter = queryParameter.split("=", 2);
      if (splitParameter.length != 2 || splitParameter[0].isBlank()) {
        return null;
      }
      return new QueryParameter(splitParameter[0], splitParameter[1]);
    }
  }
}
