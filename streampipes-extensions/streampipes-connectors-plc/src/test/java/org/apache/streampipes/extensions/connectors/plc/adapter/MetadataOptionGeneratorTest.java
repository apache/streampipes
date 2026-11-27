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

package org.apache.streampipes.extensions.connectors.plc.adapter;

import org.apache.streampipes.extensions.connectors.plc.PlcConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.MetadataOptionGenerator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionExtractor;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.extensions.connectors.plc.adapter.migration.Plc4xAdapterMigrationV2;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.XSD;

import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.metadata.Option;
import org.apache.plc4x.java.api.metadata.OptionMetadata;
import org.apache.plc4x.java.api.metadata.PlcDriverMetadata;
import org.apache.plc4x.java.api.types.OptionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetadataOptionGeneratorTest {

  @Test
  void supportsDefaultsDoubleFileAndSecretOptions() {
    var number = option("factor", OptionType.DOUBLE);
    when(number.getDefaultValue()).thenReturn(Optional.of(1.5));
    var file = option("keystore", OptionType.FILE);
    var secret = option("password", OptionType.STRING);
    when(secret.isSecret()).thenReturn(true);
    when(secret.isRequired()).thenReturn(true);
    var properties = new MetadataOptionGenerator().getOptions(List.of(number, file, secret));
    var numeric = assertInstanceOf(FreeTextStaticProperty.class, properties[0]);
    assertEquals(XSD.DOUBLE, numeric.getRequiredDatatype());
    assertEquals("1.5", numeric.getValue());
    assertTrue(numeric.isOptional());
    assertEquals(XSD.STRING, assertInstanceOf(FreeTextStaticProperty.class, properties[1]).getRequiredDatatype());
    assertInstanceOf(SecretStaticProperty.class, properties[2]);
    assertFalse(properties[2].isOptional());
  }

  @Test
  void migratedSecretsRemainAvailableInConnectionSettings() {
    var option = option("password", OptionType.STRING);
    when(option.isSecret()).thenReturn(true);
    when(option.isRequired()).thenReturn(true);
    var metadata = mock(OptionMetadata.class);
    when(metadata.getOptions()).thenReturn(List.of(option));
    when(metadata.getRequiredOptions()).thenReturn(List.of(option));
    var driverMetadata = mock(PlcDriverMetadata.class);
    when(driverMetadata.getProtocolConfigurationOptionMetadata()).thenReturn(Optional.of(metadata));
    when(driverMetadata.getSupportedTransportCodes()).thenReturn(List.of("tcp"));
    var driver = mock(PlcDriver.class);
    when(driver.getProtocolCode()).thenReturn("example");
    when(driver.getMetadata()).thenReturn(driverMetadata);
    // Construct the old plaintext form using metadata before secret annotations were supported.
    when(option.isSecret()).thenReturn(false);
    var protocol = new MetadataOptionGenerator().makeMetadata(Plc4xLabels.PROTOCOL_METADATA, metadata);
    var original = new AdapterDescription();
    original.setConfig(List.of(
        StaticProperties.stringFreeTextProperty(Labels.withId(Plc4xLabels.PLC_IP), "localhost"),
        StaticProperties.stringFreeTextProperty(Labels.withId(Plc4xLabels.PLC_POLLING_INTERVAL), "1000"),
        StaticProperties.singleValueSelection(Labels.withId(Plc4xLabels.SUPPORTED_TRANSPORTS),
            List.of(new org.apache.streampipes.model.staticproperty.Option("tcp", true))),
        protocol,
        StaticProperties.codeStaticProperty(Labels.withId(Plc4xLabels.PLC_CODE_BLOCK), CodeLanguage.None, "")
    ));
    var oldExtractor = StaticPropertyExtractor.from(original.getConfig(), List.of());
    oldExtractor.getStaticPropertyByName(Plc4xLabels.PLC_CODE_BLOCK,
        CodeInputStaticProperty.class).setValue("");
    oldExtractor.getStaticPropertyByName("password", FreeTextStaticProperty.class).setValue("test-secret");
    when(option.isSecret()).thenReturn(true);
    var result = new Plc4xAdapterMigrationV2(driver).migrate(original, null);
    assertTrue(result.success(), result.message());
    var extractor = StaticPropertyExtractor.from(result.element().getConfig(), List.of());
    assertInstanceOf(SecretStaticProperty.class, extractor.getStaticPropertyByName("password"));
    assertEquals("example:tcp://localhost?password=test-secret",
        new Plc4xConnectionExtractor(extractor, "example").makeSettings().connectionString());
  }

  @Test
  void extractsRuntimeResolvedTransportSecretsAndKeepsExistingQueryParameters() {
    var transport = (StaticPropertyAlternatives) new MetadataOptionGenerator()
        .makeRuntimeResolvableMetadata(Plc4xLabels.TRANSPORT_METADATA);
    var secret = StaticProperties.secretValue(Labels.withId("keystore-password"));
    secret.setValue("transport-secret");
    ((StaticPropertyGroup) transport.getAlternatives().getFirst().getStaticProperty())
        .setStaticProperties(List.of(secret));
    var code = StaticProperties.codeStaticProperty(Labels.withId(Plc4xLabels.PLC_CODE_BLOCK), CodeLanguage.None, "");
    code.setValue("");
    List<org.apache.streampipes.model.staticproperty.StaticProperty> config = List.of(
        StaticProperties.stringFreeTextProperty(Labels.withId(Plc4xLabels.PLC_IP), "localhost?tls.verify=true"),
        StaticProperties.stringFreeTextProperty(Labels.withId(Plc4xLabels.PLC_POLLING_INTERVAL), "1000"),
        StaticProperties.singleValueSelection(Labels.withId(Plc4xLabels.SUPPORTED_TRANSPORTS),
            List.of(new org.apache.streampipes.model.staticproperty.Option("tls", true))),
        transport, code
    );
    var extractor = StaticPropertyExtractor.from(config, List.of());
    assertEquals("example:tls://localhost?tls.verify=true&tls.keystore-password=transport-secret",
        new Plc4xConnectionExtractor(extractor, "example").makeSettings().connectionString());
  }

  @Test
  void allInstalledDriversCanDeclareTheirAdaptersAndMigrations() {
    var module = new PlcConnectorsModuleExport();
    var adapters = module.adapters();
    assertFalse(adapters.isEmpty());
    adapters.forEach(adapter -> adapter.declareConfig());
    assertTrue(module.migrators().stream().anyMatch(Plc4xAdapterMigrationV2.class::isInstance));
  }

  private Option option(String key, OptionType type) {
    var option = mock(Option.class);
    when(option.getKey()).thenReturn(key);
    when(option.getType()).thenReturn(type);
    when(option.getDefaultValue()).thenReturn(Optional.empty());
    return option;
  }
}
