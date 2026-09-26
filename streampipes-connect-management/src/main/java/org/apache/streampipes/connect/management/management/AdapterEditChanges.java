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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.audit.events.AuditChange;
import org.apache.streampipes.audit.events.adapter.AdapterEditedDetails;
import org.apache.streampipes.audit.events.extraction.StaticPropertyAuditExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/** Compares effective values of editable adapter fields; secret values never leave the comparison. */
final class AdapterEditChanges {
  private static final Logger LOG = LoggerFactory.getLogger(AdapterEditChanges.class);
  private static final String REDACTED = "[REDACTED]";
  private final StaticPropertyAuditExtractor extractor = new StaticPropertyAuditExtractor(SecretEncryptionManager::decrypt);

  AdapterEditedDetails detect(AdapterDescription before, AdapterDescription after) {
    try {
      var changes = new ArrayList<AuditChange>();
      compare("name", before.getName(), after.getName(), changes);
      compare("description", before.getDescription(), after.getDescription(), changes);
      var oldConfig = extractor.extract(before.getConfig());
      var newConfig = extractor.extract(after.getConfig());
      var names = new TreeSet<>(oldConfig.keySet());
      names.addAll(newConfig.keySet());
      for (var name : names) {
        var oldValue = oldConfig.get(name);
        var newValue = newConfig.get(name);
        if (!Objects.equals(oldValue, newValue)) {
          boolean secret = oldValue != null && oldValue.secret() || newValue != null && newValue.secret();
          changes.add(new AuditChange(name,
              secret ? REDACTED : oldValue == null ? null : oldValue.value(),
              secret ? REDACTED : newValue == null ? null : newValue.value()));
        }
      }
      return new AdapterEditedDetails(changes);
    } catch (RuntimeException e) {
      // Audit comparison must not prevent the business operation; no raw config or error is logged.
      LOG.warn("Adapter audit change detection unavailable ({})", e.getClass().getSimpleName());
      return null;
    }
  }

  private void compare(String name, Object before, Object after, List<AuditChange> changes) {
    if (!Objects.equals(before, after)) {
      changes.add(new AuditChange(name, before, after));
    }
  }
}
