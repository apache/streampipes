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

package org.apache.streampipes.service.core;

import org.apache.streampipes.audit.api.AuditEventProvider;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.AuthenticationAuditRecorder;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.audit.influx.InfluxAuditEventStore;
import org.apache.streampipes.audit.management.DefaultAuditService;
import org.apache.streampipes.commons.environment.Environments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/** Explicitly imported by each core application's bootstrap. */
@Configuration
public class AuditConfiguration {
  @Bean
  public AuditEventProvider standardAuditEvents() {
    return new StandardAuditEvents();
  }

  @Bean
  public AuthenticationAuditRecorder authenticationAuditRecorder(AuditService auditService) {
    return new AuthenticationAuditRecorder(auditService);
  }

  // The audit worker closes its store after draining; Spring must not close it a second time.
  @Bean(destroyMethod = "")
  public AuditEventStore auditEventStore() {
    var environment = Environments.getEnvironment();
    if (!environment.getAuditEnabled().getValueOrDefault()) {
      return event -> { };
    }
    if (!"influx".equals(environment.getAuditStorageProvider().getValueOrDefault())) {
      throw new IllegalArgumentException("Unsupported audit storage provider");
    }
    try {
      return new InfluxAuditEventStore(
          environment.getAuditInfluxUrl().getValueOrDefault(),
          environment.getAuditInfluxDatabase().getValueOrDefault(),
          environment.getAuditInfluxToken().getValueOrDefault(),
          environment.getAuditInfluxOrg().getValueOrDefault()
      );
    } catch (IllegalArgumentException e) {
      // Keep explicitly enabled but misconfigured auditing observable as failures, never a no-op.
      return event -> {
        throw new IllegalStateException("Audit storage configuration unavailable");
      };
    }
  }

  @Bean(destroyMethod = "close")
  public AuditService auditService(List<AuditEventProvider> providers,
                                   AuditEventStore auditEventStore) {
    var environment = Environments.getEnvironment();
    if (!environment.getAuditEnabled().getValueOrDefault()) {
      return AuditService.disabled();
    }
    return new DefaultAuditService(providers, auditEventStore, Clock.systemUTC(),
        environment.getAuditQueueCapacity().getValueOrDefault(),
        Duration.ofSeconds(environment.getAuditShutdownTimeoutSeconds().getValueOrDefault()));
  }
}
