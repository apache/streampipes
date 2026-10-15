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

package org.apache.streampipes.service.core.scheduler;

import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.dataset.RetentionAction;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

@Configuration
public class AuditRetentionScheduler implements SchedulingConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(AuditRetentionScheduler.class);
  private final AuditEventStore auditEventStore;
  private final ISpCoreConfigurationStorage coreConfigurationStorage;

  public AuditRetentionScheduler(AuditEventStore auditEventStore,
                                 ISpCoreConfigurationStorage coreConfigurationStorage) {
    this.auditEventStore = auditEventStore;
    this.coreConfigurationStorage = coreConfigurationStorage;
  }

  public void cleanupAuditEvents() {
    try {
      var coreConfiguration = coreConfigurationStorage.get();
      var retention = coreConfiguration == null ? null : coreConfiguration.getAuditRetentionConfiguration();
      if (retention == null || retention.getDataRetentionConfig() == null
          || retention.getDataRetentionConfig().action() != RetentionAction.DELETE) {
        return;
      }
      var days = retention.getDataRetentionConfig().olderThanDays();
      if (days > 0) {
      //TODO
        auditEventStore.deleteEventsOlderThan(Clock.systemUTC().instant().minus(days, ChronoUnit.MINUTES));
      }
    } catch (RuntimeException e) {
      LOG.error("Audit retention cleanup failed", e);
    }
    LOG.info("Audit CRON Job finished.");
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    var environment = Environments.getEnvironment();
    taskRegistrar.addTriggerTask(this::cleanupAuditEvents,
        triggerContext -> new CronTrigger(environment.getAuditRetentionSchedulerCron().getValueOrDefault())
            .nextExecution(triggerContext));
  }
}
