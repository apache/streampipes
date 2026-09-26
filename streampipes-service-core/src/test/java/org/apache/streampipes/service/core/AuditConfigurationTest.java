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

import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.audit.influx.InfluxAuditEventStore;
import org.apache.streampipes.commons.constants.CustomEnvs;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.impl.admin.AuditResource;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditConfigurationTest {
  @Configuration
  @EnableMethodSecurity
  static class MethodSecurityConfiguration { }

  @Test
  void defaultsToDisabledWithoutConnectingToStorage() {
    try (var envs = mockStatic(CustomEnvs.class);
         var context = new AnnotationConfigApplicationContext(AuditConfiguration.class)) {
      assertFalse(context.getBean(AuditService.class).status().enabled());
    }
  }

  @Test
  void enabledButUnconfiguredRecordsFailuresInsteadOfSilentlyDisabling() {
    try (var envs = mockStatic(CustomEnvs.class);
         var context = new AnnotationConfigApplicationContext()) {
      envs.when(() -> CustomEnvs.exists("SP_AUDIT_ENABLED")).thenReturn(true);
      envs.when(() -> CustomEnvs.getEnv("SP_AUDIT_ENABLED")).thenReturn("true");
      context.register(AuditConfiguration.class);
      context.refresh();
      var service = context.getBean(AuditService.class);
      service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.SUCCEEDED, "user-1", "1", null);
      service.close();
      assertEquals(1, service.status().failureCount());
      assertFalse(service.status().available());
    }
  }

  @Test
  void configuredCommonsEnvironmentInitializesInfluxStoreAtStartup() {
    var settings = Map.of(
        "SP_AUDIT_ENABLED", "true",
        "SP_AUDIT_STORAGE_PROVIDER", "influx",
        "SP_AUDIT_INFLUX_URL", "http://127.0.0.1:8086",
        "SP_AUDIT_INFLUX_DATABASE", "audit-test",
        "SP_AUDIT_INFLUX_TOKEN", "test-token"
    );
    try (var stores = mockConstruction(InfluxAuditEventStore.class);
         var envs = mockStatic(CustomEnvs.class);
         var context = new AnnotationConfigApplicationContext()) {
      settings.forEach((key, value) -> {
        envs.when(() -> CustomEnvs.exists(key)).thenReturn(true);
        envs.when(() -> CustomEnvs.getEnv(key)).thenReturn(value);
      });
      context.register(AuditConfiguration.class);
      context.refresh();
      assertInstanceOf(InfluxAuditEventStore.class, context.getBean(AuditEventStore.class));
      assertTrue(context.getBean(AuditService.class).status().enabled());
      verify(stores.constructed().getFirst(), timeout(2000)).initialize();
    }
  }

  @Test
  void statusRequiresAdministratorRole() {
    try (var context = new AnnotationConfigApplicationContext()) {
      var resources = mock(SpResourceManager.class);
      when(resources.getAuditService()).thenReturn(AuditService.disabled());
      context.registerBean(SpResourceManager.class, () -> resources);
      context.register(AuditResource.class, MethodSecurityConfiguration.class);
      context.refresh();
      var resource = context.getBean(AuditResource.class);
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
          "user", "unused", List.of(new SimpleGrantedAuthority("ROLE_PIPELINE_USER"))));
      assertThrows(AccessDeniedException.class, resource::status);
      assertThrows(AccessDeniedException.class, resource::eventTypes);
      assertThrows(AccessDeniedException.class, () -> resource.events("2026-09-25T00:00:00Z",
          "2026-09-26T00:00:00Z", null, null, null, 50, null));
      assertThrows(AccessDeniedException.class, () -> resource.details(java.util.UUID.randomUUID(), "locator"));
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
          "admin", "unused", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
      assertFalse(resource.status().enabled());
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}
