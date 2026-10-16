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

package org.apache.streampipes.audit.management;

import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditExportConfiguration;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;

import java.io.IOException;
import java.io.OutputStream;

final class AuditExportService {
  private final AuditEventReader reader;
  private final AuditOutputWriterFactory writerFactory;

  AuditExportService(AuditEventReader reader) {
    this(reader, new AuditOutputWriterFactory());
  }

  AuditExportService(AuditEventReader reader, AuditOutputWriterFactory writerFactory) {
    this.reader = reader;
    this.writerFactory = writerFactory;
  }

  void export(AuditQuery query, AuditExportConfiguration configuration, OutputStream outputStream)
      throws IOException {
    try (var writer = writerFactory.create(configuration)) {
      writer.beforeFirstItem(outputStream);

      AuditQuery pageQuery = query;
      do {
        AuditPage page = reader.query(pageQuery);
        for (var entry : page.items()) {
          writer.writeItem(entry);
        }
        pageQuery = nextPageQuery(query, page);
      } while (pageQuery != null);

      writer.afterLastItem(outputStream);
    }
  }

  private AuditQuery nextPageQuery(AuditQuery query, AuditPage page) {
    return page.nextCursor() == null
        ? null
        : new AuditQuery(query.from(), query.to(), query.eventType(), query.actor(), query.outcome(),
            query.limit(), page.nextCursor());
  }
}
