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

package org.apache.streampipes.service.core.migrations.v099.connect;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MigratePlc4xS7AdaptersToGenericAdapter implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MigratePlc4xS7AdaptersToGenericAdapter.class);

  private static final String OLD_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";
  private static final String NEW_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.generic.s7";
  private static final int GENERIC_ADAPTER_VERSION = 1;
  private static final String DEFAULT_LOCALE = "strings.en";
  private static final URI XSD_STRING = URI.create("http://www.w3.org/2001/XMLSchema#string");
  private static final URI XSD_INTEGER = URI.create("http://www.w3.org/2001/XMLSchema#integer");

  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";
  private static final String PLC_NODE_INPUT_ALTERNATIVES = "plc_node_input_alternatives";
  private static final String PLC_NODE_INPUT_COLLECTION_ALTERNATIVE = "plc_node_input_collection_alternative";
  private static final String PLC_CODE_BLOCK = "plc_code_block";
  private static final String SUPPORTED_TRANSPORTS = "supported_transports";
  private static final String TRANSPORT_METADATA = "transport_metadata";
  private static final String PROTOCOL_METADATA = "protocol_metadata";
  private static final String REQUIRED_OPTIONS = "required_options";
  private static final String ADVANCED_OPTIONS = "advanced_options";
  private static final String REQUIRED_GROUP_TRANSPORT = "required_group_transport";
  private static final String ADVANCED_GROUP_TRANSPORT = "advanced_group_transport";
  private static final String REQUIRED_GROUP_PROTOCOL = "required_group_protocol";
  private static final String ADVANCED_GROUP_PROTOCOL = "advanced_group_protocol";

  private final IAdapterStorage adapterStorage;

  public MigratePlc4xS7AdaptersToGenericAdapter(IAdapterStorage adapterStorage) {
    this.adapterStorage = adapterStorage;
  }

  @Override
  public boolean shouldExecute() {
    return adapterStorage.findAll()
        .stream()
        .anyMatch(adapter -> OLD_APP_ID.equals(adapter.getAppId()));
  }

  @Override
  public void executeMigration() throws IOException {
    adapterStorage.findAll()
        .stream()
        .filter(adapter -> OLD_APP_ID.equals(adapter.getAppId()))
        .forEach(this::migrateAndUpdateAdapter);
  }

  @Override
  public String getDescription() {
    return "Migrates PLC4X S7 adapters to the generic PLC4X adapter.";
  }

  private void migrateAndUpdateAdapter(AdapterDescription adapter) {
    LOG.info("Migrating PLC4X S7 adapter to generic PLC4X adapter: {}", adapter.getElementId());

    var splitAddress = SplitPlcAddress.from(textValue(adapter, PLC_IP));
    adapter.setAppId(NEW_APP_ID);
    adapter.setVersion(GENERIC_ADAPTER_VERSION);
    adapter.setIncludesLocales(true);
    adapter.setIncludedLocales(List.of(DEFAULT_LOCALE));
    adapter.setConfig(List.of(
        makeFreeText(PLC_IP, "PLC Address", "Example: 192.168.34.56", splitAddress.host(), false),
        makeFreeText(
            PLC_POLLING_INTERVAL,
            "Polling Interval [ms]",
            "Polling Interval of adapter in milliseconds. Minimum value is 10.",
            textValue(adapter, PLC_POLLING_INTERVAL),
            false,
            XSD_INTEGER
        ),
        makeSupportedTransports(),
        makeTransportMetadata(),
        makeProtocolMetadata(splitAddress.queryParameters()),
        makeCodeBlock(makeTags(adapter))
    ));

    adapterStorage.updateElement(adapter);
  }

  private String textValue(AdapterDescription adapter,
                           String internalName) {
    return adapter.getConfig()
        .stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }

  private StaticProperty makeSupportedTransports() {
    var property = new OneOfStaticProperty(
        SUPPORTED_TRANSPORTS,
        "Supported Transports",
        "Select supported transport protocols"
    );
    property.setOptions(List.of(new Option("tcp", true)));
    return property;
  }

  private StaticProperty makeTransportMetadata() {
    return makeAlternatives(
        TRANSPORT_METADATA,
        "Transport Metadata",
        "Apply transport metadata configurations",
        makeAlternative(
            REQUIRED_OPTIONS,
            "Required options",
            "",
            makeRuntimeResolvableGroup(REQUIRED_GROUP_TRANSPORT),
            true
        ),
        makeAlternative(
            ADVANCED_OPTIONS,
            "Advanced options",
            "",
            makeRuntimeResolvableGroup(ADVANCED_GROUP_TRANSPORT),
            false
        )
    );
  }

  private StaticProperty makeRuntimeResolvableGroup(String internalName) {
    var group = new RuntimeResolvableGroupStaticProperty(
        internalName,
        "",
        "",
        List.of(SUPPORTED_TRANSPORTS)
    );
    group.setStaticProperties(List.of());
    group.setHorizontalRendering(false);
    return group;
  }

  private StaticProperty makeProtocolMetadata(List<QueryParameter> queryParameters) {
    return makeAlternatives(
        PROTOCOL_METADATA,
        "Protocol Metadata",
        "Apply protocol metadata configurations",
        makeAlternative(
            REQUIRED_OPTIONS,
            "Required options",
            "",
            makeProtocolGroup(REQUIRED_GROUP_PROTOCOL, queryParameters),
            true
        ),
        makeAlternative(
            ADVANCED_OPTIONS,
            "Advanced options",
            "",
            makeProtocolGroup(ADVANCED_GROUP_PROTOCOL, List.of()),
            false
        )
    );
  }

  private StaticProperty makeProtocolGroup(String internalName,
                                           List<QueryParameter> queryParameters) {
    var group = new StaticPropertyGroup(
        internalName,
        "",
        ""
    );
    group.setStaticProperties(queryParameters.stream()
        .map(queryParameter -> makeFreeText(
            queryParameter.name(),
            queryParameter.name(),
            "",
            queryParameter.value(),
            true,
            XSD_STRING
        ))
        .toList());
    group.setHorizontalRendering(false);
    return group;
  }

  private StaticProperty makeCodeBlock(String value) {
    var codeBlock = new CodeInputStaticProperty(
        PLC_CODE_BLOCK,
        "Tags",
        "Enter the tags in the code block below, according to the described format"
    );
    codeBlock.setLanguage("None");
    codeBlock.setCodeTemplate("");
    codeBlock.setValue(value);
    return codeBlock;
  }

  private String makeTags(AdapterDescription adapter) {
    var alternatives = getProperty(adapter, PLC_NODE_INPUT_ALTERNATIVES, StaticPropertyAlternatives.class);
    if (alternatives.isPresent()) {
      return makeTagsFromAlternatives(alternatives.get());
    }

    return getProperty(adapter, PLC_NODES, CollectionStaticProperty.class)
        .map(this::makeTagsFromCollection)
        .orElse("");
  }

  private String makeTagsFromAlternatives(StaticPropertyAlternatives alternatives) {
    var selectedAlternative = alternatives.getAlternatives()
        .stream()
        .filter(StaticPropertyAlternative::getSelected)
        .findFirst();

    if (selectedAlternative.isPresent()
        && PLC_NODE_INPUT_COLLECTION_ALTERNATIVE.equals(selectedAlternative.get().getInternalName())) {
      return Optional.ofNullable(selectedAlternative.get().getStaticProperty())
          .filter(CollectionStaticProperty.class::isInstance)
          .map(CollectionStaticProperty.class::cast)
          .map(this::makeTagsFromCollection)
          .orElse("");
    }

    return selectedAlternative
        .map(StaticPropertyAlternative::getStaticProperty)
        .filter(CodeInputStaticProperty.class::isInstance)
        .map(CodeInputStaticProperty.class::cast)
        .map(CodeInputStaticProperty::getValue)
        .orElse("");
  }

  private String makeTagsFromCollection(CollectionStaticProperty nodes) {
    return nodes.getMembers()
        .stream()
        .filter(StaticPropertyGroup.class::isInstance)
        .map(StaticPropertyGroup.class::cast)
        .map(this::makeTag)
        .filter(tag -> !tag.isBlank())
        .toList()
        .stream()
        .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
  }

  private String makeTag(StaticPropertyGroup node) {
    var staticProperties = node.getStaticProperties();
    var runtimeName = textValue(staticProperties, PLC_NODE_RUNTIME_NAME);
    var nodeName = textValue(staticProperties, PLC_NODE_NAME);
    var nodeType = selectedValue(staticProperties, PLC_NODE_TYPE)
        .toUpperCase()
        .replaceAll(" ", "_");

    if (runtimeName.isBlank() || nodeName.isBlank() || nodeType.isBlank()) {
      return "";
    }

    return "%s=%s:%s".formatted(runtimeName, nodeName, nodeType);
  }

  private <T extends StaticProperty> Optional<T> getProperty(AdapterDescription adapter,
                                                            String internalName,
                                                            Class<T> propertyClass) {
    return adapter.getConfig()
        .stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .filter(propertyClass::isInstance)
        .map(propertyClass::cast)
        .findFirst();
  }

  private StaticProperty makeFreeText(String internalName,
                                      String label,
                                      String description,
                                      String value,
                                      boolean optional) {
    return makeFreeText(internalName, label, description, value, optional, XSD_STRING);
  }

  private StaticProperty makeFreeText(String internalName,
                                      String label,
                                      String description,
                                      String value,
                                      boolean optional,
                                      URI datatype) {
    var property = new FreeTextStaticProperty(
        internalName,
        label,
        description,
        datatype
    );
    property.setValue(value);
    property.setOptional(optional);
    return property;
  }

  private StaticProperty makeAlternatives(String internalName,
                                          String label,
                                          String description,
                                          StaticPropertyAlternative... alternatives) {
    var property = new StaticPropertyAlternatives(internalName, label, description);
    property.setAlternatives(List.of(alternatives));
    return property;
  }

  private StaticPropertyAlternative makeAlternative(String internalName,
                                                   String label,
                                                   String description,
                                                   StaticProperty staticProperty,
                                                   boolean selected) {
    var alternative = new StaticPropertyAlternative(internalName, label, description);
    alternative.setStaticProperty(staticProperty);
    alternative.setSelected(selected);
    return alternative;
  }

  private String textValue(List<StaticProperty> staticProperties,
                           String internalName) {
    return staticProperties
        .stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }

  private String selectedValue(List<StaticProperty> staticProperties,
                               String internalName) {
    return staticProperties
        .stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .filter(OneOfStaticProperty.class::isInstance)
        .map(OneOfStaticProperty.class::cast)
        .flatMap(property -> property.getOptions().stream())
        .filter(Option::isSelected)
        .findFirst()
        .map(Option::getName)
        .orElse("");
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
