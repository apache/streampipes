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

package org.apache.streampipes.dataexplorer.param.model;

import org.apache.streampipes.dataexplorer.InfluxDbReservedKeywords;

import java.util.regex.Pattern;

final class InfluxQueryParameterValidator {

  private static final Pattern SAFE_TIME_INTERVAL = Pattern.compile("^\\d+(ms|s|m|h|d|w)$");
  private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[^\\s,;()\"']+$");

  private InfluxQueryParameterValidator() {
  }

  static String requireSafeTimeInterval(String timeInterval) {
    if (timeInterval == null || !SAFE_TIME_INTERVAL.matcher(timeInterval).matches()) {
      throw new IllegalArgumentException("Invalid time interval format: " + timeInterval);
    }

    return timeInterval;
  }

  static String requireSafeIdentifier(String identifier) {
    if (identifier == null
        || !SAFE_IDENTIFIER.matcher(identifier).matches()
        || InfluxDbReservedKeywords.KEYWORD_LIST.stream().anyMatch(k -> k.equalsIgnoreCase(identifier))) {
      throw new IllegalArgumentException("Invalid group by identifier: " + identifier);
    }

    return identifier;
  }
}
