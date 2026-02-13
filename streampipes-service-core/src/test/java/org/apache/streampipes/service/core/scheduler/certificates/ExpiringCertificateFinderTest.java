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

package org.apache.streampipes.service.core.scheduler.certificates;


import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.model.opcua.CertificateBuilder;
import org.apache.streampipes.storage.api.system.ICertificateStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpiringCertificateFinderTest {

  private ICertificateStorage storage;

  private static final Instant FIXED_NOW = Instant.parse("2026-01-10T12:00:00Z");
  private Clock clock;

  @BeforeEach
  void setUp() {
    storage = mock(ICertificateStorage.class);
    clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
  }

  @Test
  void findCertificates_nullCheck() {
    var certificate = createCertificateExpiringInDays(5);
    when(storage.findAll()).thenReturn(List.of(certificate));

    var finder = new ExpiringCertificateFinder(storage, clock);
    var result = finder.findCertificates(null);

    assertTrue(result.isEmpty());
  }

  @Test
  void findCertificates_emptyExpirePeriodsList() {
    var certificate = createCertificateExpiringInDays(5);
    when(storage.findAll()).thenReturn(List.of(certificate));

    var finder = new ExpiringCertificateFinder(storage, clock);
    var result = finder.findCertificates(List.of());

    assertTrue(result.isEmpty());

  }

  @Test
  void findCertificates_noCertificateExpiresInProvidedPeriods() {
    var certificate = createCertificateExpiringInDays(10);
    when(storage.findAll()).thenReturn(List.of(certificate));

    var finder = new ExpiringCertificateFinder(storage, clock);
    var periods = List.of(5, 7);
    var result = finder.findCertificates(periods);

    assertEquals(2, result.size());
    assertTrue(result.containsKey(5));
    assertTrue(result.get(5).isEmpty());
    assertTrue(result.containsKey(7));
    assertTrue(result.get(7).isEmpty());
  }

  @Test
  void findCertificates_onePeriodHasExpiringCertificate() {
    var certExpiringIn5 = createCertificateExpiringInDays(5);
    var certExpiringIn10 = createCertificateExpiringInDays(10);
    when(storage.findAll()).thenReturn(List.of(certExpiringIn5, certExpiringIn10));

    var finder = new ExpiringCertificateFinder(storage, clock);
    var periods = List.of(5, 7);
    var result = finder.findCertificates(periods);

    assertEquals(2, result.size());
    assertTrue(result.containsKey(5));
    assertEquals(1, result.get(5).size());
    assertTrue(result.containsKey(7));
    assertTrue(result.get(7).isEmpty());
  }

  @Test
  void findCertificates_bothPeriodsHaveExpiringCertificates() {
    var certExpiringIn5 = createCertificateExpiringInDays(5);
    var certExpiringIn7 = createCertificateExpiringInDays(7);
    when(storage.findAll()).thenReturn(List.of(certExpiringIn5, certExpiringIn7));

    var finder = new ExpiringCertificateFinder(storage, clock);
    var periods = List.of(5, 7);
    var result = finder.findCertificates(periods);

    assertEquals(2, result.size());
    assertTrue(result.containsKey(5));
    assertEquals(1, result.get(5).size());
    assertTrue(result.containsKey(7));
    assertEquals(1, result.get(7).size());
  }

  private Certificate createCertificateExpiringInDays(int days) {
    Instant notAfter = FIXED_NOW
        .plus(days, ChronoUnit.DAYS);

    return CertificateBuilder.create()
                             .notAfter(Date.from(notAfter).toString())
                             .build();
  }

}
