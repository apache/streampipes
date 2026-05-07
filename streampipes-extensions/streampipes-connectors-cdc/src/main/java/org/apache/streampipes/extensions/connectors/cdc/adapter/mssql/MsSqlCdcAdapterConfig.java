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

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;

import java.time.ZoneId;
import java.time.ZoneOffset;

public class MsSqlCdcAdapterConfig {

  public static final String HOST_KEY = "host";
  public static final String PORT_KEY = "port";
  public static final String DATABASE_KEY = "database";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";
  public static final String TABLE_KEY = "table";
  public static final String ENCRYPT_KEY = "encrypt";
  public static final String TRUST_SERVER_CERTIFICATE_KEY = "trust-server-certificate";
  public static final String TIMEZONE_KEY = "timezone";

  private final String host;
  private final Integer port;
  private final String database;
  private final String username;
  private final String password;
  private final String table;
  private final Boolean encrypt;
  private final Boolean trustServerCertificate;
  private final String timezone;

  public MsSqlCdcAdapterConfig(String host,
                               Integer port,
                               String database,
                               String username,
                               String password,
                               String table,
                               Boolean encrypt,
                               Boolean trustServerCertificate,
                               String timezone) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.username = username;
    this.password = password;
    this.table = table;
    this.encrypt = encrypt;
    this.trustServerCertificate = trustServerCertificate;
    this.timezone = timezone;
  }

  public static MsSqlCdcAdapterConfig from(IStaticPropertyExtractor extractor) {
    return from(extractor, true);
  }

  public static MsSqlCdcAdapterConfig from(IStaticPropertyExtractor extractor, boolean requireTableSelection) {
    return new MsSqlCdcAdapterConfig(
        extractor.singleValueParameter(HOST_KEY, String.class),
        extractor.singleValueParameter(PORT_KEY, Integer.class),
        extractor.singleValueParameter(DATABASE_KEY, String.class),
        extractor.singleValueParameter(USERNAME_KEY, String.class),
        extractor.secretValue(PASSWORD_KEY),
        extractTable(extractor, requireTableSelection),
        extractor.slideToggleValue(ENCRYPT_KEY),
        extractor.slideToggleValue(TRUST_SERVER_CERTIFICATE_KEY),
        extractor.singleValueParameter(TIMEZONE_KEY, String.class)
    );
  }

  private static String extractTable(IStaticPropertyExtractor extractor, boolean requireTableSelection) {
    if (requireTableSelection) {
      return extractor.selectedSingleValue(TABLE_KEY, String.class);
    }

    RuntimeResolvableOneOfStaticProperty tableProperty =
        extractor.getStaticPropertyByName(TABLE_KEY, RuntimeResolvableOneOfStaticProperty.class);

    return tableProperty.getOptions()
        .stream()
        .filter(Option::isSelected)
        .findFirst()
        .map(Option::getName)
        .orElse(null);
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public String getDatabase() {
    return database;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getTable() {
    return table;
  }

  public Boolean getEncrypt() {
    return encrypt;
  }

  public Boolean getTrustServerCertificate() {
    return trustServerCertificate;
  }

  public String getTimezone() {
    return timezone;
  }

  public ZoneId getZoneId() {
    return timezone == null || timezone.isBlank() ? ZoneOffset.UTC : ZoneId.of(timezone);
  }
}
