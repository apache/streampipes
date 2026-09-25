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

package org.apache.streampipes.dataexplorer.param;

import org.apache.streampipes.dataexplorer.InfluxDbReservedKeywords;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Compatibility validation for the existing REST query syntax. Its historical Influx restrictions
 * intentionally remain here; providers must not use this validator for typed SPI queries.
 */
final class RestQueryParameterValidator {

  private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[^\\s,;()\"']+$");
  private static final Pattern NUMERIC_FILL = Pattern.compile("^-?\\d+(\\.\\d+)?$");

  private RestQueryParameterValidator() {
  }

  static String requireSafeIdentifier(String identifier) {
    if (identifier == null
        || !SAFE_IDENTIFIER.matcher(identifier).matches()
        || InfluxDbReservedKeywords.KEYWORD_LIST.stream().anyMatch(k -> k.equalsIgnoreCase(identifier))) {
      throw new IllegalArgumentException("Invalid group by identifier: " + identifier);
    }

    return identifier;
  }

  static QuerySpec.Fill parseFill(String fill) {
    if (fill == null || fill.isBlank()) {
      return new QuerySpec.Fill(QuerySpec.FillMode.NONE, Optional.empty());
    }

    String normalized = fill.trim().toLowerCase(Locale.ROOT);

    if (normalized.equals("none")
        || normalized.equals("null")
        || normalized.equals("previous")
        || normalized.equals("linear")) {
      return new QuerySpec.Fill(QuerySpec.FillMode.valueOf(normalized.toUpperCase(Locale.ROOT)), Optional.empty());
    }

    if (NUMERIC_FILL.matcher(normalized).matches()) {
      return new QuerySpec.Fill(QuerySpec.FillMode.CONSTANT,
          Optional.of(new QuerySpec.Literal(Double.parseDouble(normalized))));
    }

    throw new IllegalArgumentException("Invalid fill parameter: " + fill);
  }
}
