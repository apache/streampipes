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
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ComputeCertificateThumbprintMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(ComputeCertificateThumbprintMigration.class);

  private CRUDStorage<Certificate> certificateStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getCertificateStorage();

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    this.certificateStorage
        .findAll()
        .stream()
        .filter(certificate -> certificate.getThumbprint() == null)
        .forEach(certificate -> {
          try {
            certificate.setThumbprint(CertificateUtils.getThumbprint(certificate.getCertificateDerBase64()));
            certificateStorage.updateElement(certificate);
          } catch (CertificateException e) {
            throw new RuntimeException(e);
          } catch (NoSuchAlgorithmException e) {
            LOG.warn("Could not compute thumbprint for existing certificate: {}", e.getMessage());
          }
        });
  }

  @Override
  public String getDescription() {
    return "Adding thumbprint to existing certificates";
  }
}
