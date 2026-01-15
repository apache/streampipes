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
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ExpiringCertificateFinder {

  private static final Logger LOG = LoggerFactory.getLogger(ExpiringCertificateFinder.class);

  private static final DateTimeFormatter NOT_AFTER_FORMATTER =
      DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

  private final CRUDStorage<Certificate> certificateStorage;
  private final Clock clock;

  public ExpiringCertificateFinder(CRUDStorage<Certificate> certificateStorage, Clock clock) {
    this.certificateStorage = certificateStorage;
    this.clock = clock;
  }

  public ExpiringCertificateFinder() {
    this(
        StorageDispatcher.INSTANCE.getNoSqlStore()
                                  .getCertificateStorage(),
        Clock.systemUTC()
    );
  }


  /**
   * Returns a map with one entry per requested period (days).
   * Each value contains certificates whose notAfter timestamp falls within that UTC day.
   * Invalid periods (null/negative) are ignored.
   */
  public Map<Integer, List<Certificate>> findCertificates(List<Integer> expirePeriods) {
    if (expirePeriods == null || expirePeriods.isEmpty()) {
      return Map.of();
    }

    var allCertificates = certificateStorage.findAll();

    var now = clock.instant();

    Map<Integer, List<Certificate>> result = new LinkedHashMap<>();

    for (int days : expirePeriods) {
      var windowStart = now.plus(days, ChronoUnit.DAYS)
                           .truncatedTo(ChronoUnit.DAYS);
      var windowEnd = windowStart.plus(1, ChronoUnit.DAYS);

      result.put(days, filterCertificatesExpiringBetween(allCertificates, windowStart, windowEnd));
    }

    return result;
  }

  private List<Certificate> filterCertificatesExpiringBetween(
      List<Certificate> certificates,
      Instant windowStartInclusive,
      Instant windowEndExclusive
  ) {
    if (certificates == null || certificates.isEmpty()) {
      return List.of();
    }

    return certificates.stream()
                       .filter(Objects::nonNull)
                       .filter(c ->
                                   parseNotAfterInstant(c).map(ts -> !ts.isBefore(windowStartInclusive) && ts.isBefore(windowEndExclusive))
                           .orElse(false))
                       .toList();
  }

  private Optional<Instant> parseNotAfterInstant(Certificate certificate) {
    String notAfter = certificate.getNotAfter();
    if (notAfter == null || notAfter.isBlank()) {
      return Optional.empty();
    }

    try {
      return Optional.of(ZonedDateTime.parse(notAfter, NOT_AFTER_FORMATTER)
                                      .toInstant());
    } catch (DateTimeParseException e) {
      LOG.warn("Unable to parse notAfter for certificate {} -> '{}'", certificate, notAfter, e);
      return Optional.empty();
    }
  }


}
