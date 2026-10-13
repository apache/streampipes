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

import org.apache.streampipes.extensions.connectors.plc.adapter.generic.GenericPlc4xAdapter;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;

import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_RESULTS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.MANUAL_TAGS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_CODE_BLOCK;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_IP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.TAG_INPUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class Plc4xAdapterMigrationV3Test {

  @Test
  void preservesManualTagsAndDoesNotChangeOriginal() {
    var code = code();
    var original = new AdapterDescription();
    original.setConfig(new ArrayList<>(List.of(code)));
    var result = new Plc4xAdapterMigrationV3("s7").migrate(original, null);
    assertTrue(result.success());
    var extractor = StaticPropertyExtractor.from(result.element().getConfig(), List.of());
    assertEquals(MANUAL_TAGS, extractor.selectedAlternativeInternalId(TAG_INPUT));
    assertEquals(code.getValue(), extractor.codeblockValue(PLC_CODE_BLOCK));
    assertTrue(original.getConfig().getFirst() instanceof CodeInputStaticProperty);
  }

  @Test
  void refusesMigrationWithoutExistingTags() {
    var original = new AdapterDescription();
    original.setConfig(List.of());
    assertFalse(new Plc4xAdapterMigrationV3("s7").migrate(original, null).success());
  }

  @Test
  void extractsSelectedAddressesWithStableFieldNames() throws Exception {
    var driver = PlcDriverManager.getDefault().getDriver("s7");
    var config = new GenericPlc4xAdapter(driver, mock(PlcConnectionManager.class)).declareConfig()
        .getAdapterDescription().getConfig();
    var extractor = StaticPropertyExtractor.from(config, List.of());
    extractor.getStaticPropertyByName(PLC_IP, FreeTextStaticProperty.class)
        .setValue("localhost");
    var alternatives = extractor.getStaticPropertyByName(TAG_INPUT, StaticPropertyAlternatives.class);
    alternatives.getAlternatives().getFirst().setSelected(false);
    alternatives.getAlternatives().get(1).setSelected(true);
    var tree = extractor.getStaticPropertyByName(BROWSE_RESULTS, RuntimeResolvableTreeInputStaticProperty.class);
    tree.setSelectedNodesInternalNames(List.of("%DB1:0:REAL", "%DB1:4:INT"));
    var connectionExtractor = new Plc4xConnectionExtractor(extractor, "s7");
    var settings = connectionExtractor.makeSettings();
    assertEquals("%DB1:0:REAL", settings.nodes().get("tag__DB1_0_REAL"));
    tree.setSelectedNodesInternalNames(List.of("%DB1:4:INT", "%DB1:0:REAL"));
    assertEquals(settings.nodes(), connectionExtractor.makeSettings().nodes());
    tree.setSelectedNodesInternalNames(List.of());
    assertTrue(connectionExtractor.makeConnectionString().startsWith("s7:cotp://localhost"));
    assertThrows(IllegalArgumentException.class, connectionExtractor::makeSettings);
  }

  private CodeInputStaticProperty code() {
    var property = StaticProperties.codeStaticProperty(Labels.withId(PLC_CODE_BLOCK), CodeLanguage.None, "");
    property.setValue("// saved configuration\ntemperature=%DB1:0:REAL");
    return property;
  }
}
