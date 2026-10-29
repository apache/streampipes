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

package org.apache.streampipes.audit.management.export;

import org.apache.streampipes.audit.api.AuditEntry;
import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditExportColumn;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

final class AuditExportValueFormatter {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private AuditExportValueFormatter() {
  }

  static String format(AuditExportColumn column, AuditEntry entry, AuditEntryDetails details) throws IOException {
    return column.requiresDetails()
        ? details == null ? null : MAPPER.writeValueAsString(details.details())
        : column.value(entry);
  }
}
