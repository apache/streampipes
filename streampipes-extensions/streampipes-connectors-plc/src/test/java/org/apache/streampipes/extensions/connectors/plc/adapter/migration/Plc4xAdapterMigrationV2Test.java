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
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;

import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.modbus.base.tag.ModbusTag;
import org.apache.plc4x.java.s7.configuration.S7CotpTransportConfiguration;
import org.apache.plc4x.java.spi.config.ConfigurationFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Plc4xAdapterMigrationV2Test {

  @Test
  void migratesS7OptionsAndArraysWithoutModifyingOriginal() throws Exception {
    var original = new AdapterDescription();
    original.setConfig(new ArrayList<>(List.of(
        text(Plc4xLabels.PLC_IP, "localhost"),
        text(Plc4xLabels.PLC_POLLING_INTERVAL, "1000"),
        StaticProperties.singleValueSelection(Labels.withId(Plc4xLabels.SUPPORTED_TRANSPORTS),
            List.of(new Option("tcp", true))),
        metadata(Plc4xLabels.PROTOCOL_METADATA, Plc4xLabels.ADVANCED_GROUP_PROTOCOL,
            text("remote-slot", "3"), text("read-timeout", "1234"),
            text("local-tsap", "512"), text("remote-tsap", "258")),
        metadata(Plc4xLabels.TRANSPORT_METADATA, Plc4xLabels.ADVANCED_GROUP_TRANSPORT,
            text("tcp-no-delay", "false"), text("connect-timeout", "4567")),
        code("// retain comment\na=%DB42:28.0:BYTE[4] // array\nb=%DB1:0:STRING(20)[1]")
    )));
    var driver = PlcDriverManager.getDefault().getDriver("s7");
    var result = new Plc4xAdapterMigrationV2(driver).migrate(original, null);
    assertTrue(result.success(), result.message());
    var extractor = StaticPropertyExtractor.from(result.element().getConfig(), List.of());
    var settings = new Plc4xConnectionExtractor(extractor, "s7").makeSettings();
    assertTrue(settings.connectionString().startsWith("s7:cotp://localhost?"));
    assertTrue(settings.connectionString().contains("cotp.remote-slot=3"));
    assertTsapOverrides(settings.connectionString());
    assertTrue(settings.connectionString().contains("read-timeout-ms=1234"));
    assertTrue(settings.connectionString().contains("cotp.no-delay=false"));
    assertTrue(settings.connectionString().contains("cotp.connect-timeout-ms=4567"));
    assertEquals("%DB42:28.0[0..3]:BYTE", settings.nodes().get("a"));
    assertEquals("%DB1:0:STRING(20)", settings.nodes().get("b"));
    assertTrue(extractor.codeblockValue(Plc4xLabels.PLC_CODE_BLOCK).contains("// array"));
    var oldExtractor = StaticPropertyExtractor.from(original.getConfig(), List.of());
    assertEquals("tcp", oldExtractor.selectedSingleValue(Plc4xLabels.SUPPORTED_TRANSPORTS, String.class));
    assertEquals("3", oldExtractor.textParameter("remote-slot"));
  }

  @Test
  void migratesInactiveMetadataAlternativesToo() throws Exception {
    var original = new AdapterDescription();
    var metadata = (StaticPropertyAlternatives) metadata(Plc4xLabels.PROTOCOL_METADATA,
        Plc4xLabels.ADVANCED_GROUP_PROTOCOL, text("request-timeout", "400"));
    metadata.getAlternatives().getFirst().setSelected(false);
    original.setConfig(List.of(metadata));
    var result = new Plc4xAdapterMigrationV2(PlcDriverManager.getDefault().getDriver("modbus-tcp"))
        .migrate(original, null);
    assertTrue(result.success());
    var migrated = (StaticPropertyAlternatives) result.element().getConfig().getFirst();
    assertFalse(migrated.getAlternatives().getFirst().getSelected());
    var group = (StaticPropertyGroup) migrated.getAlternatives().getFirst().getStaticProperty();
    assertEquals("request-timeout-ms", group.getStaticProperties().getFirst().getInternalName());
    assertEquals("400", ((FreeTextStaticProperty) group.getStaticProperties().getFirst()).getValue());
  }

  @Test
  void migratesLegacyS7HostParameters() throws Exception {
    var original = new AdapterDescription();
    original.setConfig(List.of(text(Plc4xLabels.PLC_IP,
        "localhost?remote-rack=1&remote-slot=3&read-timeout=50&local-tsap=512&remote-tsap=258")));
    var migration = new Plc4xAdapterMigrationV2("org.apache.streampipes.connect.iiot.adapters.plc4x.s7",
        PlcDriverManager.getDefault().getDriver("s7"));
    var result = migration.migrate(original, null);
    assertTrue(result.success());
    var host = ((FreeTextStaticProperty) result.element().getConfig().getFirst()).getValue();
    assertEquals("localhost?cotp.remote-rack=1&cotp.remote-slot=3&read-timeout-ms=50"
        + "&cotp.local-tsap=512&cotp.remote-tsap=258", host);
    assertTsapOverrides(host);
  }

  @Test
  void rejectsInvalidCountsWithoutPartiallyMigrating() throws Exception {
    var original = new AdapterDescription();
    original.setConfig(List.of(code("a=%DB1:0:INT[0]")));
    var result = new Plc4xAdapterMigrationV2(PlcDriverManager.getDefault().getDriver("s7")).migrate(original, null);
    assertFalse(result.success());
    assertEquals("a=%DB1:0:INT[0]", ((CodeInputStaticProperty) original.getConfig().getFirst()).getValue());
  }

  @Test
  void convertsProtocolSpecificArraysAndPreservesUnchangedSyntax() {
    assertEquals("holding-register:1[0..3]:INT", Plc4xV1Configuration.tag("modbus-tcp", "holding-register:1:INT[4]"));
    assertEquals("0x4020/0[0..3]:DINT", Plc4xV1Configuration.tag("ads", "0x4020/0:DINT[4]"));
    assertEquals("myArray[2..5]:DINT", Plc4xV1Configuration.tag("eip", "myArray[2]:DINT:4"));
    assertEquals("MAIN.g_arr[3]", Plc4xV1Configuration.tag("ads", "MAIN.g_arr[3]"));
    assertEquals("3[0..3]", Plc4xV1Configuration.tag("firmata", "3[4]"));
    assertEquals("%DB1:0[2..4]:INT", Plc4xV1Configuration.tag("s7", "%DB1:0[2..4]:INT"));
    assertEquals("ns=2;s=foo[4]", Plc4xV1Configuration.tag("opcua", "ns=2;s=foo[4]"));
    assertEquals("tls.verify", Plc4xV1Configuration.optionName("opcua", "insecure-certificate-verification"));
    assertEquals("false", Plc4xV1Configuration.optionValue("insecure-certificate-verification", "true"));
    assertEquals("tls.keystore", Plc4xV1Configuration.optionName("opcua", "key-store-file"));
  }

  @Test
  void preservesUntypedModbusArrayAddressesAndShapes() throws Exception {
    for (var protocol : List.of("modbus-tcp", "modbus-rtu", "modbus-ascii")) {
      for (var address : List.of("holding-register:1", "coil:1", "40001")) {
        for (int count : List.of(1, 4)) {
          var original = new AdapterDescription();
          original.setConfig(List.of(code("value=" + address + "[" + count + "]")));
          var result = new Plc4xAdapterMigrationV2(PlcDriverManager.getDefault().getDriver(protocol))
              .migrate(original, null);
          assertTrue(result.success(), result.message());
          var migratedCode = ((CodeInputStaticProperty) result.element().getConfig().getFirst()).getValue();
          var tag = ModbusTag.of(migratedCode.substring("value=".length()));
          assertEquals(ModbusTag.of(address).getLogicalAddress(), tag.getLogicalAddress());
          assertEquals(count, tag.getNumberOfElements());
          assertEquals(count == 1, tag.getArrayInfo().isEmpty());
        }
      }
      var original = new AdapterDescription();
      original.setConfig(List.of(code("value=holding-register:1[0]")));
      assertFalse(new Plc4xAdapterMigrationV2(PlcDriverManager.getDefault().getDriver(protocol))
          .migrate(original, null).success());
    }
  }

  private void assertTsapOverrides(String connectionString) {
    var configuration = new ConfigurationFactory().createPrefixedConfiguration(
        S7CotpTransportConfiguration.class, "cotp",
        connectionString.substring(connectionString.indexOf('?') + 1));
    assertEquals(512, configuration.getLocalTsap());
    assertEquals(258, configuration.getRemoteTsap());
  }

  private CodeInputStaticProperty code(String value) {
    var property = StaticProperties.codeStaticProperty(Labels.withId(Plc4xLabels.PLC_CODE_BLOCK), CodeLanguage.None, "");
    property.setValue(value);
    return property;
  }

  private StaticProperty metadata(String name, String groupName, StaticProperty... properties) {
    return StaticProperties.alternatives(Labels.withId(name),
        Alternatives.from(Labels.withId(Plc4xLabels.ADVANCED_OPTIONS),
            StaticProperties.group(Labels.withId(groupName), false, properties), true));
  }

  private FreeTextStaticProperty text(String name, String value) {
    var property = StaticProperties.stringFreeTextProperty(Labels.withId(name));
    property.setValue(value);
    return property;
  }
}
