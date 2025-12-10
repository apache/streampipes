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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.model.opcua.CertificateState;
import org.apache.streampipes.model.opcua.CertificateUsage;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
@RequestMapping("/api/v2/admin/certificates")
public class CertificateResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(CertificateResource.class);

  private final CRUDStorage<Certificate> certificateStorage = StorageDispatcher
      .INSTANCE.getNoSqlStore().getCertificateStorage();

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Certificate> getAll() {
    return certificateStorage.findAll();
  }

  @GetMapping(value = "trusted", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Certificate> getTrusted() {
    return certificateStorage
        .findAll()
        .stream()
        .filter(c -> c.getState() == CertificateState.TRUSTED)
        .toList();
  }

  @PutMapping(
      value = "{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Certificate update(@PathVariable String id,
                            @RequestBody Certificate certificate) {
    if (!id.equals(certificate.getElementId())) {
      throw new IllegalArgumentException("ID in path and body do not match");
    }

    return certificateStorage.updateElement(certificate);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public void create(@RequestBody Certificate certificate) {
    // check if the certificate already exists
    var allCertificates = certificateStorage.findAll();
    if (allCertificates.stream()
        .noneMatch(c -> c.equals(certificate))) {
      certificateStorage.persist(certificate);
    } else {
      LOG.info("Certificate with IssuerDN {} already exists, skipping creation", certificate.getIssuerDn());
    }
  }

  @PostMapping(value = "usage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public void updateUsage(@RequestBody CertificateUsage certificateUsage) {
    var certificates = certificateStorage.findAll();
    certificates
        .stream()
        .filter(c -> Objects.nonNull(c.getThumbprint()) && c.getThumbprint().equals(certificateUsage.thumbprint()))
        .findFirst()
        .ifPresent(c -> {
          c.getAssociatedResourceIds().add(certificateUsage.associatedResourceId());
          certificateStorage.updateElement(c);
        });
  }

  @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public void delete(@PathVariable String id) {
    var certificate = certificateStorage.getElementById(id);
    if (certificate == null) {
      throw new IllegalArgumentException("Certificate with ID " + id + " does not exist");
    }
    certificateStorage.deleteElement(certificate);
  }
}
