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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql;

import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MsSqlCdcAdapterTest {

  @Test
  void configExtractionAllowsMissingTableDuringRuntimeResolution() {
    RuntimeResolvableOneOfStaticProperty tableProperty =
        new RuntimeResolvableOneOfStaticProperty(MsSqlCdcAdapterConfig.TABLE_KEY, "", "");
    tableProperty.setOptions(List.of(new Option("dbo.Measurements")));

    MsSqlCdcAdapterConfig config = MsSqlCdcAdapterConfig.from(
        StaticPropertyExtractor.from(List.of(
            textProperty(MsSqlCdcAdapterConfig.HOST_KEY, "localhost"),
            textProperty(MsSqlCdcAdapterConfig.PORT_KEY, "1433"),
            textProperty(MsSqlCdcAdapterConfig.DATABASE_KEY, "testdb"),
            textProperty(MsSqlCdcAdapterConfig.USERNAME_KEY, "sa"),
            secretProperty(MsSqlCdcAdapterConfig.PASSWORD_KEY, "secret"),
            toggleProperty(MsSqlCdcAdapterConfig.ENCRYPT_KEY, true),
            toggleProperty(MsSqlCdcAdapterConfig.TRUST_SERVER_CERTIFICATE_KEY, false),
            textProperty(MsSqlCdcAdapterConfig.TIMEZONE_KEY, "UTC"),
            tableProperty
        )),
        false
    );

    assertNull(config.getTable());
  }

  @Test
  void buildDebeziumPropertiesUsesDecimalHandlingAndElementId() {
    MsSqlCdcAdapter adapter = new MsSqlCdcAdapter();
    MsSqlCdcAdapterConfig config = new MsSqlCdcAdapterConfig(
        "localhost",
        1433,
        "testdb",
        "sa",
        "secret",
        "dbo.Measurements",
        true,
        false,
        "UTC"
    );

    Properties properties = adapter.buildDebeziumProperties(config, "adapter-42");

    assertEquals("double", properties.getProperty("decimal.handling.mode"));
    assertEquals("1000", properties.getProperty("poll.interval.ms"));
    assertEquals("testdb_dbo_measurements_adapter_42", properties.getProperty("topic.prefix"));
    assertEquals("testdb_dbo_measurements_adapter_42-engine", properties.getProperty("name"));
  }

  @Test
  void timestampNormalizationUsesConfiguredTimezone() throws Exception {
    MsSqlCdcAdapter adapter = new MsSqlCdcAdapter();
    Field configuredZoneId = MsSqlCdcAdapter.class.getDeclaredField("configuredZoneId");
    configuredZoneId.setAccessible(true);
    configuredZoneId.set(adapter, ZoneId.of("Europe/Berlin"));

    Object normalized = adapter.normalizeTemporalValue(
        1_778_161_800_000L,
        MsSqlMetadataClient.TemporalColumnMode.TIMESTAMP_MILLIS
    );

    long expected = LocalDateTime.of(2026, 5, 7, 13, 50)
        .atZone(ZoneId.of("Europe/Berlin"))
        .toInstant()
        .toEpochMilli();

    assertEquals(expected, normalized);
  }

  @Test
  void engineCompletionReportsFailuresToRuntimeLogger() throws Exception {
    MsSqlCdcAdapter adapter = new MsSqlCdcAdapter();
    TestLogger logger = new TestLogger();
    RuntimeException cause = new RuntimeException("boom");

    adapter.handleEngineCompletion(false, "connector failed", cause, logger, new CountDownLatch(1));

    Field engineFailure = MsSqlCdcAdapter.class.getDeclaredField("engineFailure");
    engineFailure.setAccessible(true);

    assertNotNull(engineFailure.get(adapter));
    assertEquals("Debezium engine failed for MSSQL CDC adapter.", logger.errorDetails);
    assertEquals(cause, logger.errorException);
  }

  private static FreeTextStaticProperty textProperty(String internalName, String value) {
    FreeTextStaticProperty property = FreeTextStaticProperty.of(internalName, value);
    property.setInternalName(internalName);
    return property;
  }

  private static SecretStaticProperty secretProperty(String internalName, String value) {
    SecretStaticProperty property = new SecretStaticProperty(internalName, "", "");
    property.setValue(value);
    return property;
  }

  private static SlideToggleStaticProperty toggleProperty(String internalName, boolean selected) {
    SlideToggleStaticProperty property = new SlideToggleStaticProperty(internalName, "", "", selected);
    property.setSelected(selected);
    return property;
  }

  private static class TestLogger implements IExtensionsLogger {
    private String errorDetails;
    private Exception errorException;

    @Override
    public void log(SpLogMessage logMessage) {
    }

    @Override
    public void error(Exception e) {
      this.errorException = e;
    }

    @Override
    public void error(String details, Exception e) {
      this.errorDetails = details;
      this.errorException = e;
    }

    @Override
    public void info(String title, String details) {
    }

    @Override
    public void warn(String title, String details) {
    }
  }
}
