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

package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.audit.events.AuditChange;
import org.apache.streampipes.audit.events.extraction.StaticPropertyAuditExtractor;
import org.apache.streampipes.audit.events.pipeline.PipelineEditedDetails;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

/** Compares processor and sink configurations without retaining the complete pipeline graph. */
public final class PipelineEditChanges {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineEditChanges.class);
  private static final String REDACTED = "[REDACTED]";
  private final StaticPropertyAuditExtractor extractor = new StaticPropertyAuditExtractor(SecretEncryptionManager::decrypt);

  public PipelineEditedDetails detect(Pipeline before, Pipeline after) {
    try {
      var changes = new ArrayList<AuditChange>();
      compareElements("processor", before.getSepas(), after.getSepas(), changes);
      compareElements("sink", before.getActions(), after.getActions(), changes);
      return new PipelineEditedDetails(changes);
    } catch (RuntimeException e) {
      // Audit comparison must not prevent the business operation; no raw configuration or error is logged.
      LOG.warn("Pipeline audit change detection unavailable ({})", e.getClass().getSimpleName());
      return null;
    }
  }

  private void compareElements(String type,
                               List<? extends InvocableStreamPipesEntity> before,
                               List<? extends InvocableStreamPipesEntity> after,
                               List<AuditChange> changes) {
    var oldElements = elementsById(before);
    var newElements = elementsById(after);
    var elementIds = new TreeSet<>(oldElements.keySet());
    elementIds.addAll(newElements.keySet());
    for (var elementId : elementIds) {
      var oldElement = oldElements.get(elementId);
      var newElement = newElements.get(elementId);
      var oldConfig = extractor.extract(oldElement == null ? null : oldElement.getStaticProperties());
      var newConfig = extractor.extract(newElement == null ? null : newElement.getStaticProperties());
      var propertyNames = new TreeSet<>(oldConfig.keySet());
      propertyNames.addAll(newConfig.keySet());
      for (var propertyName : propertyNames) {
        var oldValue = oldConfig.get(propertyName);
        var newValue = newConfig.get(propertyName);
        if (!Objects.equals(oldValue, newValue)) {
          boolean secret = oldValue != null && oldValue.secret() || newValue != null && newValue.secret();
          changes.add(new AuditChange(type + "[" + elementId + "]." + propertyName,
              secret ? REDACTED : oldValue == null ? null : oldValue.value(),
              secret ? REDACTED : newValue == null ? null : newValue.value()));
        }
      }
    }
  }

  private Map<String, InvocableStreamPipesEntity> elementsById(List<? extends InvocableStreamPipesEntity> elements) {
    var result = new LinkedHashMap<String, InvocableStreamPipesEntity>();
    if (elements != null) {
      for (int index = 0; index < elements.size(); index++) {
        var element = elements.get(index);
        String elementId = element.getElementId();
        String key = elementId == null || elementId.isBlank() ? "element[" + index + "]" : elementId;
        if (result.putIfAbsent(key, element) != null) {
          throw new IllegalArgumentException("Duplicate pipeline element ID");
        }
      }
    }
    return result;
  }
}
