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

import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionExtractor;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Plc4xS7ToGenericAdapterMigrationTest {

  private static final String OLD_APP_ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.s7";
  private static final String PLC_IP = "plc_ip";
  private static final String PLC_POLLING_INTERVAL = "plc_polling_interval";
  private static final String PLC_NODES = "plc_nodes";
  private static final String PLC_NODE_NAME = "plc_node_name";
  private static final String PLC_NODE_RUNTIME_NAME = "plc_node_runtime_name";
  private static final String PLC_NODE_TYPE = "plc_node_type";
  private static final String PLC_NODE_INPUT_ALTERNATIVES = "plc_node_input_alternatives";
  private static final String PLC_NODE_INPUT_CODE_BLOCK_ALTIVE = "plc_node_input_code_block_altive";
  private static final String PLC_NODE_INPUT_COLLECTION_ALTERNATIVE = "plc_node_input_collection_alternative";

  private Plc4xS7ToGenericAdapterMigration migration;

  @BeforeEach
  void setUp() {
    this.migration = new Plc4xS7ToGenericAdapterMigration();
  }

  @Test
  void migrateSimpleCollectionToGenericS7Adapter() {
    var adapter = makeAdapter(
        "192.168.34.56?remote-rack=0&remote-slot=3",
        makeCollectionAlternative()
    );

    var result = migration.migrate(adapter, StaticPropertyExtractor.from(adapter.getConfig()));
    var migratedAdapter = result.element();
    var settings = new Plc4xConnectionExtractor(
        StaticPropertyExtractor.from(migratedAdapter.getConfig()),
        "s7"
    ).makeSettings();

    assertEquals(Plc4xS7ToGenericAdapterMigration.NEW_APP_ID, migratedAdapter.getAppId());
    assertTrue(settings.connectionString().startsWith("s7-light:tcp://192.168.34.56?"));
    assertTrue(Arrays.asList(settings.connectionString().split("\\?", 2)[1].split("&"))
        .containsAll(List.of("remote-rack=0", "remote-slot=3")));
    assertEquals(1000, settings.pollingInterval());
    assertEquals("%I0.0:BOOL", settings.nodes().get("input"));
    assertEquals("%Q0.4:TIME_OF_DAY", settings.nodes().get("output"));
  }

  @Test
  void migrateAdvancedCodeBlockToGenericS7Adapter() {
    var codeBlock = """
        temperature=%I0.0:INT
        pressure=%Q0.4:REAL
        """;
    var adapter = makeAdapter(
        "192.168.34.56",
        makeCodeBlockAlternative(codeBlock)
    );

    var result = migration.migrate(adapter, StaticPropertyExtractor.from(adapter.getConfig()));
    var migratedAdapter = result.element();
    var settings = new Plc4xConnectionExtractor(
        StaticPropertyExtractor.from(migratedAdapter.getConfig()),
        "s7"
    ).makeSettings();

    assertEquals(Plc4xS7ToGenericAdapterMigration.NEW_APP_ID, migratedAdapter.getAppId());
    assertEquals("s7-light:tcp://192.168.34.56", settings.connectionString());
    assertEquals("%I0.0:INT", settings.nodes().get("temperature"));
    assertEquals("%Q0.4:REAL", settings.nodes().get("pressure"));
  }

  private AdapterDescription makeAdapter(StaticProperty nodeInputAlternative) {
    return makeAdapter("192.168.34.56", nodeInputAlternative);
  }

  private AdapterDescription makeAdapter(String ipAddress,
                                         StaticProperty nodeInputAlternative) {
    var adapter = new AdapterDescription();
    adapter.setAppId(OLD_APP_ID);
    adapter.setVersion(1);
    adapter.setConfig(List.of(
        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_IP), ipAddress),
        StaticProperties.integerFreeTextProperty(Labels.withId(PLC_POLLING_INTERVAL), 1000),
        nodeInputAlternative
    ));
    return adapter;
  }

  private StaticProperty makeCollectionAlternative() {
    var collection = StaticProperties.collection(
        Labels.withId(PLC_NODES),
        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
        StaticProperties.singleValueSelection(
            Labels.withId(PLC_NODE_TYPE),
            Options.from("Bool", "Time of day")
        )
    );
    collection.setMembers(new ArrayList<>(List.of(
        makeNode("input", "%I0.0", "Bool"),
        makeNode("output", "%Q0.4", "Time of day")
    )));

    return StaticProperties.alternatives(
        Labels.withId(PLC_NODE_INPUT_ALTERNATIVES),
        Alternatives.from(
            Labels.withId(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE),
            collection,
            true
        ),
        Alternatives.from(
            Labels.withId(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE),
            StaticProperties.codeStaticProperty(
                Labels.withId(Plc4xLabels.PLC_CODE_BLOCK),
                CodeLanguage.None,
                ""
            )
        )
    );
  }

  private StaticProperty makeCodeBlockAlternative(String codeBlockValue) {
    var codeBlock = StaticProperties.codeStaticProperty(
        Labels.withId(Plc4xLabels.PLC_CODE_BLOCK),
        CodeLanguage.None,
        ""
    );
    codeBlock.setValue(codeBlockValue);

    return StaticProperties.alternatives(
        Labels.withId(PLC_NODE_INPUT_ALTERNATIVES),
        Alternatives.from(
            Labels.withId(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE),
            StaticProperties.collection(
                Labels.withId(PLC_NODES),
                StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME))
            )
        ),
        Alternatives.from(
            Labels.withId(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE),
            codeBlock,
            true
        )
    );
  }

  private StaticProperty makeNode(String runtimeName,
                                  String nodeName,
                                  String type) {
    return StaticProperties.group(
        Labels.withId("node"),
        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME), runtimeName),
        StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME), nodeName),
        StaticProperties.singleValueSelection(
            Labels.withId(PLC_NODE_TYPE),
            List.of(
                new Option(type, true)
            )
        )
    );
  }
}
