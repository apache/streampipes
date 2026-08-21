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

package org.apache.streampipes.extensions.connectors.mssql.adapter.polling;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record MsSqlTableIdentifier(String schema, String table) {

  private static final String ENCODING_PREFIX = "mssql-table:";

  public MsSqlTableIdentifier {
    if (schema == null || schema.isBlank() || table == null || table.isBlank()) {
      throw new IllegalArgumentException("SQL Server schema and table names must not be blank.");
    }
  }

  public String encode() {
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    return ENCODING_PREFIX
        + encoder.encodeToString(schema.getBytes(StandardCharsets.UTF_8))
        + "."
        + encoder.encodeToString(table.getBytes(StandardCharsets.UTF_8));
  }

  public String displayName() {
    return MsSqlTablePollingClient.quoteIdentifier(schema) + "." + MsSqlTablePollingClient.quoteIdentifier(table);
  }

  public static MsSqlTableIdentifier decode(String encoded) {
    if (encoded == null) {
      throw new IllegalArgumentException("Invalid SQL Server table selection.");
    }
    if (!encoded.startsWith(ENCODING_PREFIX)) {
      String[] legacyParts = encoded.split("\\.", -1);
      if (legacyParts.length == 2) {
        return new MsSqlTableIdentifier(legacyParts[0], legacyParts[1]);
      }
      throw new IllegalArgumentException("Invalid SQL Server table selection.");
    }
    String[] parts = encoded.substring(ENCODING_PREFIX.length()).split("\\.", -1);
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid SQL Server table selection.");
    }
    Base64.Decoder decoder = Base64.getUrlDecoder();
    return new MsSqlTableIdentifier(
        new String(decoder.decode(parts[0]), StandardCharsets.UTF_8),
        new String(decoder.decode(parts[1]), StandardCharsets.UTF_8)
    );
  }
}
