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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigratePlc4xS7AdaptersToGenericAdapterTest {

  private static final String OLD_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";
  private static final String NEW_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.generic.s7";
  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";
  private static final String PLC_NODE_INPUT_ALTERNATIVES = "plc_node_input_alternatives";
  private static final String PLC_NODE_INPUT_CODE_BLOCK_ALTIVE = "plc_node_input_code_block_altive";
  private static final String PLC_NODE_INPUT_COLLECTION_ALTERNATIVE = "plc_node_input_collection_alternative";
  private static final String PLC_CODE_BLOCK = "plc_code_block";

  @Test
  void migratesCollectionBasedS7Adapter() throws IOException {
    var adapter = makeAdapter(
        "192.168.34.56?remote-rack=0&remote-slot=3",
        makeNodeCollection()
    );
    adapter.setVersion(0);

    var storage = new InMemoryAdapterStorage(adapter);
    var migration = new MigratePlc4xS7AdaptersToGenericAdapter(storage);

    assertTrue(migration.shouldExecute());
    migration.executeMigration();

    var migratedAdapter = storage.updatedAdapter;
    assertGenericAdapterMetadata(migratedAdapter);
    assertEquals("192.168.34.56", value(migratedAdapter.getConfig().get(0)));
    assertEquals("1000", value(migratedAdapter.getConfig().get(1)));
    assertEquals(URI.create("http://www.w3.org/2001/XMLSchema#integer"),
        ((FreeTextStaticProperty) migratedAdapter.getConfig().get(1)).getRequiredDatatype());
    assertEquals("PLC Address", migratedAdapter.getConfig().get(0).getLabel());
    assertEquals("Tags", migratedAdapter.getConfig().get(5).getLabel());
    assertEquals(
        String.join(System.lineSeparator(), "input=%I0.0:BOOL", "output=%Q0.4:TIME_OF_DAY"),
        value(migratedAdapter.getConfig().get(5))
    );
    assertProtocolParameter(migratedAdapter, "remote-rack", "0");
    assertProtocolParameter(migratedAdapter, "remote-slot", "3");
  }

  @Test
  void migratesCodeBlockBasedS7Adapter() throws IOException {
    var codeBlock = String.join(System.lineSeparator(), "temperature=%I0.0:INT", "pressure=%Q0.4:REAL");
    var adapter = makeAdapter(
        "192.168.34.56",
        makeNodeInputAlternatives(makeNodeCollection(), makeCodeBlock(codeBlock), false)
    );

    var storage = new InMemoryAdapterStorage(adapter);
    var migration = new MigratePlc4xS7AdaptersToGenericAdapter(storage);

    migration.executeMigration();

    var migratedAdapter = storage.updatedAdapter;
    assertGenericAdapterMetadata(migratedAdapter);
    assertEquals("192.168.34.56", value(migratedAdapter.getConfig().get(0)));
    assertEquals(codeBlock, value(migratedAdapter.getConfig().get(5)));
  }

  @Test
  void ignoresNonS7Adapters() throws IOException {
    var adapter = makeAdapter("192.168.34.56", makeNodeCollection());
    adapter.setAppId("other");

    var storage = new InMemoryAdapterStorage(adapter);
    var migration = new MigratePlc4xS7AdaptersToGenericAdapter(storage);

    assertFalse(migration.shouldExecute());
    migration.executeMigration();

    assertEquals(0, storage.updateCount);
  }

  private void assertGenericAdapterMetadata(AdapterDescription adapter) {
    assertEquals(NEW_APP_ID, adapter.getAppId());
    assertEquals(1, adapter.getVersion());
    assertTrue(adapter.isIncludesLocales());
    assertEquals(List.of("strings.en"), adapter.getIncludedLocales());
    assertEquals(List.of(
        PLC_IP,
        PLC_POLLING_INTERVAL,
        "supported_transports",
        "transport_metadata",
        "protocol_metadata",
        PLC_CODE_BLOCK
    ), adapter.getConfig().stream().map(StaticProperty::getInternalName).toList());
  }

  private void assertProtocolParameter(AdapterDescription adapter,
                                       String name,
                                       String expectedValue) {
    var protocolMetadata = (StaticPropertyAlternatives) adapter.getConfig().get(4);
    var requiredOptions = protocolMetadata.getAlternatives().get(0);
    var requiredGroup = (StaticPropertyGroup) requiredOptions.getStaticProperty();
    var parameter = requiredGroup.getStaticProperties()
        .stream()
        .filter(property -> name.equals(property.getInternalName()))
        .map(FreeTextStaticProperty.class::cast)
        .findFirst()
        .orElseThrow();

    assertTrue(parameter.isOptional());
    assertEquals(expectedValue, parameter.getValue());
  }

  private AdapterDescription makeAdapter(String ipAddress,
                                         StaticProperty nodeInput) {
    var adapter = new AdapterDescription();
    adapter.setAppId(OLD_APP_ID);
    adapter.setVersion(1);
    adapter.setConfig(List.of(
        makeFreeText(PLC_IP, ipAddress),
        makeFreeText(PLC_POLLING_INTERVAL, "1000"),
        nodeInput
    ));
    return adapter;
  }

  private CollectionStaticProperty makeNodeCollection() {
    var collection = new CollectionStaticProperty();
    collection.setInternalName(PLC_NODES);
    collection.setMembers(new ArrayList<>(List.of(
        makeNode("input", "%I0.0", "Bool"),
        makeNode("output", "%Q0.4", "Time of day")
    )));
    return collection;
  }

  private StaticProperty makeNodeInputAlternatives(CollectionStaticProperty collection,
                                                  CodeInputStaticProperty codeBlock,
                                                  boolean collectionSelected) {
    var alternatives = new StaticPropertyAlternatives(PLC_NODE_INPUT_ALTERNATIVES, "", "");
    alternatives.setAlternatives(List.of(
        makeAlternative(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE, collection, collectionSelected),
        makeAlternative(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE, codeBlock, !collectionSelected)
    ));
    return alternatives;
  }

  private StaticPropertyAlternative makeAlternative(String internalName,
                                                   StaticProperty staticProperty,
                                                   boolean selected) {
    var alternative = new StaticPropertyAlternative(internalName, "", "");
    alternative.setStaticProperty(staticProperty);
    alternative.setSelected(selected);
    return alternative;
  }

  private StaticPropertyGroup makeNode(String runtimeName,
                                       String nodeName,
                                       String type) {
    var group = new StaticPropertyGroup();
    group.setStaticProperties(List.of(
        makeFreeText(PLC_NODE_RUNTIME_NAME, runtimeName),
        makeFreeText(PLC_NODE_NAME, nodeName),
        makeSelection(PLC_NODE_TYPE, type)
    ));
    return group;
  }

  private FreeTextStaticProperty makeFreeText(String internalName,
                                             String value) {
    var property = new FreeTextStaticProperty();
    property.setInternalName(internalName);
    property.setValue(value);
    return property;
  }

  private OneOfStaticProperty makeSelection(String internalName,
                                            String selectedValue) {
    var property = new OneOfStaticProperty();
    property.setInternalName(internalName);
    property.setOptions(List.of(new Option(selectedValue, true)));
    return property;
  }

  private CodeInputStaticProperty makeCodeBlock(String value) {
    var codeBlock = new CodeInputStaticProperty();
    codeBlock.setInternalName(PLC_CODE_BLOCK);
    codeBlock.setValue(value);
    return codeBlock;
  }

  private String value(StaticProperty property) {
    if (property instanceof FreeTextStaticProperty freeTextStaticProperty) {
      return freeTextStaticProperty.getValue();
    } else if (property instanceof CodeInputStaticProperty codeInputStaticProperty) {
      return codeInputStaticProperty.getValue();
    } else {
      throw new IllegalArgumentException("Unsupported property type");
    }
  }

  private static class InMemoryAdapterStorage implements IAdapterStorage {

    private final List<AdapterDescription> adapters;
    private AdapterDescription updatedAdapter;
    private int updateCount;

    private InMemoryAdapterStorage(AdapterDescription... adapters) {
      this.adapters = new ArrayList<>(List.of(adapters));
    }

    @Override
    public AdapterDescription getFirstAdapterByAppId(String appId) {
      return getAdaptersByAppId(appId).stream().findFirst().orElse(null);
    }

    @Override
    public List<AdapterDescription> getAdaptersByAppId(String appId) {
      return adapters.stream().filter(adapter -> appId.equals(adapter.getAppId())).toList();
    }

    @Override
    public List<AdapterDescription> findAll() {
      return adapters;
    }

    @Override
    public Tuple2<Boolean, String> persist(AdapterDescription element) {
      adapters.add(element);
      return new Tuple2<>(true, "");
    }

    @Override
    public AdapterDescription getElementById(String id) {
      return adapters.stream().filter(adapter -> id.equals(adapter.getElementId())).findFirst().orElse(null);
    }

    @Override
    public AdapterDescription updateElement(AdapterDescription element) {
      updatedAdapter = element;
      updateCount++;
      return element;
    }

    @Override
    public void deleteElement(AdapterDescription element) {
      adapters.remove(element);
    }
  }
}
