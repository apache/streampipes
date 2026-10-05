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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.model.opcua.CertificateUtils;
import org.apache.streampipes.storage.api.system.ICertificateStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComputeCertificateThumbprintMigrationTest {

  @Test
  void updatesOnlyMissingThumbprintsAndIsSafeToRepeat() throws Exception {
    var storage = mock(ICertificateStorage.class);
    var existing = new Certificate();
    existing.setThumbprint("existing");
    var missing = new Certificate();
    missing.setCertificateDerBase64("certificate-data");
    when(storage.findAll()).thenReturn(List.of(existing, missing));
    var migration = new ComputeCertificateThumbprintMigration(storage);

    try (var certificates = mockStatic(CertificateUtils.class)) {
      certificates.when(() -> CertificateUtils.getThumbprint("certificate-data")).thenReturn("computed");

      migration.executeMigration();
      migration.executeMigration();

      assertEquals("computed", missing.getThumbprint());
      assertEquals("existing", existing.getThumbprint());
      verify(storage).updateElement(missing);
      verify(storage, never()).updateElement(existing);
      certificates.verify(() -> CertificateUtils.getThumbprint("certificate-data"));
    }
  }
}
