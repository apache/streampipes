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

package org.apache.streampipes.audit.api;

import java.util.HashSet;
import java.util.List;

public record AuditExportConfiguration(AuditExportFormat format, List<AuditExportColumn> columns,
                                       List<String> headers, char delimiter) {
  private static final int MAX_HEADER_LENGTH = 128;

  public AuditExportConfiguration {
    if (format == null || delimiter != ',' && delimiter != ';') {
      throw new IllegalArgumentException("Invalid audit export configuration");
    }
    columns = List.copyOf(columns);
    headers = List.copyOf(headers);
    if (columns.isEmpty() || columns.size() != headers.size()
        || new HashSet<>(columns).size() != columns.size()
        || headers.stream().anyMatch(AuditExportConfiguration::invalidHeader)) {
      throw new IllegalArgumentException("Invalid audit export configuration");
    }
  }

  private static boolean invalidHeader(String header) {
    return header == null || header.length() > MAX_HEADER_LENGTH
        || header.chars().anyMatch(Character::isISOControl);
  }
}
