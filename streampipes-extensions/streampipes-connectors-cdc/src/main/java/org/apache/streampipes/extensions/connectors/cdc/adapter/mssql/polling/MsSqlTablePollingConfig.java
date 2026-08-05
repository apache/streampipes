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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZoneOffset;

public record MsSqlTablePollingConfig(
    String host,
    int port,
    String database,
    String username,
    String password,
    boolean encrypt,
    boolean trustServerCertificate,
    String timezone,
    MsSqlTableIdentifier table,
    String sequenceColumn,
    StartupMode startupMode,
    BigDecimal customSequence,
    int pollingIntervalSeconds,
    int batchSize,
    int maxRowsPerPoll
) {

  public static final String HOST_KEY = "host";
  public static final String PORT_KEY = "port";
  public static final String DATABASE_KEY = "database";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";
  public static final String ENCRYPT_KEY = "encrypt";
  public static final String TRUST_SERVER_CERTIFICATE_KEY = "trust-server-certificate";
  public static final String TIMEZONE_KEY = "timezone";
  public static final String TABLE_KEY = "table";
  public static final String SEQUENCE_COLUMN_KEY = "sequence-column";
  public static final String STARTUP_MODE_KEY = "startup-mode";
  public static final String CUSTOM_SEQUENCE_KEY = "custom-sequence";
  public static final String POLLING_INTERVAL_SECONDS_KEY = "polling-interval-seconds";
  public static final String BATCH_SIZE_KEY = "batch-size";
  public static final String MAX_ROWS_PER_POLL_KEY = "max-rows-per-poll";

  public static final int DEFAULT_BATCH_SIZE = 500;
  public static final int DEFAULT_MAX_ROWS_PER_POLL = 10_000;

  public static MsSqlTablePollingConfig from(IStaticPropertyExtractor extractor) {
    return from(extractor, true, true);
  }

  public static MsSqlTablePollingConfig from(IStaticPropertyExtractor extractor,
                                             boolean requireTable,
                                             boolean requireSequenceColumn) {
    String customSequence = extractor.singleValueParameter(CUSTOM_SEQUENCE_KEY, String.class);
    return new MsSqlTablePollingConfig(
        extractor.singleValueParameter(HOST_KEY, String.class),
        extractor.singleValueParameter(PORT_KEY, Integer.class),
        extractor.singleValueParameter(DATABASE_KEY, String.class),
        extractor.singleValueParameter(USERNAME_KEY, String.class),
        extractor.secretValue(PASSWORD_KEY),
        extractor.slideToggleValue(ENCRYPT_KEY),
        extractor.slideToggleValue(TRUST_SERVER_CERTIFICATE_KEY),
        extractor.singleValueParameter(TIMEZONE_KEY, String.class),
        selectedTable(extractor, requireTable),
        selectedRuntimeValue(extractor, SEQUENCE_COLUMN_KEY, requireSequenceColumn),
        StartupMode.valueOf(extractor.selectedSingleValue(STARTUP_MODE_KEY, String.class)),
        customSequence == null || customSequence.isBlank() ? null : new BigDecimal(customSequence),
        extractor.singleValueParameter(POLLING_INTERVAL_SECONDS_KEY, Integer.class),
        extractor.singleValueParameter(BATCH_SIZE_KEY, Integer.class),
        extractor.singleValueParameter(MAX_ROWS_PER_POLL_KEY, Integer.class)
    );
  }

  public MsSqlPollingSettings pollingSettings() {
    return new MsSqlPollingSettings(startupMode, customSequence, batchSize, maxRowsPerPoll);
  }

  public ZoneId zoneId() {
    return timezone == null || timezone.isBlank() ? ZoneOffset.UTC : ZoneId.of(timezone);
  }

  public void validate(int minimumPollingIntervalSeconds) {
    if (pollingIntervalSeconds < minimumPollingIntervalSeconds) {
      throw new IllegalArgumentException(
          "Polling interval " + pollingIntervalSeconds + " seconds is below the administrative minimum of "
              + minimumPollingIntervalSeconds + " seconds."
      );
    }
    pollingSettings();
  }

  private static String selectedRuntimeValue(IStaticPropertyExtractor extractor,
                                             String propertyName,
                                             boolean required) {
    RuntimeResolvableOneOfStaticProperty property =
        extractor.getStaticPropertyByName(propertyName, RuntimeResolvableOneOfStaticProperty.class);
    Option selected = property.getOptions()
        .stream()
        .filter(Option::isSelected)
        .findFirst()
        .orElse(null);
    if (selected == null) {
      if (required) {
        throw new IllegalArgumentException("No value selected for " + propertyName + ".");
      }
      return null;
    }
    return selected.getInternalName() == null || selected.getInternalName().isBlank()
        ? selected.getName()
        : selected.getInternalName();
  }

  private static MsSqlTableIdentifier selectedTable(IStaticPropertyExtractor extractor, boolean required) {
    String selected = selectedRuntimeValue(extractor, TABLE_KEY, required);
    return selected == null ? null : MsSqlTableIdentifier.decode(selected);
  }
}
