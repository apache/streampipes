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

import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.dataset.DataRetentionConfig;
import org.apache.streampipes.model.dataset.RetentionAction;
import org.apache.streampipes.model.dataset.RetentionInterval;
import org.apache.streampipes.model.dataset.RetentionTimeConfig;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditRetentionManagerTest {

  @Test
  void deletesAuditEventsOlderThanTheConfiguredNumberOfDays() {
    var coreConfiguration = new SpCoreConfiguration();
    coreConfiguration.setAuditRetentionConfiguration(new RetentionTimeConfig(
        new DataRetentionConfig(RetentionInterval.DAILY, 7, RetentionAction.DELETE), null));
    var deletedBefore = new AtomicReference<Instant>();
    var store = new AuditEventStore() {
      @Override
      public void append(AuditEvent<?> event) {
      }

      @Override
      public void deleteEventsOlderThan(Instant cutoff) {
        deletedBefore.set(cutoff);
      }
    };

    var before = Instant.now().minus(Duration.ofDays(7));
    new AuditRetentionManager(store, storage(coreConfiguration), null).cleanup();
    var after = Instant.now().minus(Duration.ofDays(7));

    assertTrue(!deletedBefore.get().isBefore(before) && !deletedBefore.get().isAfter(after));
  }

  private ISpCoreConfigurationStorage storage(SpCoreConfiguration configuration) {
    return new ISpCoreConfigurationStorage() {
      @Override
      public boolean exists() {
        return true;
      }

      @Override
      public void createElement(SpCoreConfiguration element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public SpCoreConfiguration get() {
        return configuration;
      }

      @Override
      public SpCoreConfiguration updateElement(SpCoreConfiguration element) {
        return element;
      }

      @Override
      public void deleteElement() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
