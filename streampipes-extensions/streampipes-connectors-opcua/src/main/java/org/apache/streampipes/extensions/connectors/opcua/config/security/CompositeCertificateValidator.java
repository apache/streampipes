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

package org.apache.streampipes.extensions.connectors.opcua.config.security;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaCertificateUtils;
import org.apache.streampipes.model.opcua.CertificateBuilder;
import org.apache.streampipes.model.opcua.CertificateState;
import org.apache.streampipes.model.opcua.CertificateUtils;

import org.eclipse.milo.opcua.stack.client.security.ClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.TrustListManager;
import org.eclipse.milo.opcua.stack.core.util.validation.CertificateValidationUtil;
import org.eclipse.milo.opcua.stack.core.util.validation.ValidationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CompositeCertificateValidator implements ClientCertificateValidator {

  private static final Logger LOG = LoggerFactory.getLogger(CompositeCertificateValidator.class);

  public static final List<Long> REJECTED_STATUS_CODES = List.of(
      StatusCodes.Bad_CertificateChainIncomplete,
      StatusCodes.Bad_CertificateInvalid,
      StatusCodes.Bad_NoValidCertificates,
      StatusCodes.Bad_CertificateUntrusted,
      StatusCodes.Bad_CertificateUseNotAllowed,
      StatusCodes.Bad_SecurityChecksFailed
  );

  private final OpcUaConfig opcUaConfig;
  private final TrustListManager trustListManager;
  private final List<X509Certificate> trustedCerts;
  private final List<ValidationCheck> validationChecks;
  private final IStreamPipesClient streamPipesClient;

  public CompositeCertificateValidator(OpcUaConfig opcUaConfig,
                                       TrustListManager trustListManager,
                                       List<X509Certificate> trustedCerts,
                                       List<ValidationCheck> validationChecks,
                                       IStreamPipesClient streamPipesClient) {
    this.opcUaConfig = opcUaConfig;
    this.trustListManager = trustListManager;
    this.trustedCerts = trustedCerts;
    this.validationChecks = validationChecks;
    this.streamPipesClient = streamPipesClient;
  }

  @Override
  public void validateCertificateChain(List<X509Certificate> certificateChain) throws UaException {
    PKIXCertPathBuilderResult certPathResult;

    X509Certificate peer = getEndEntity(certificateChain);
    try {
      certPathResult = CertificateValidationUtil.buildTrustedCertPath(
          certificateChain,
          Stream.concat(trustListManager.getTrustedCertificates().stream(), trustedCerts.stream()).toList(),
          trustListManager.getIssuerCertificates()
      );
    } catch (UaException e) {
      if (isCertificateRejected(e.getStatusCode().getValue())) {
        sendToCore(peer);
      }
      throw e;
    }

    var crls = new ArrayList<X509CRL>();
    crls.addAll(trustListManager.getTrustedCrls());
    crls.addAll(trustListManager.getIssuerCrls());

    CertificateValidationUtil.validateTrustedCertPath(
        certPathResult.getCertPath(),
        certPathResult.getTrustAnchor(),
        crls,
        ValidationCheck.NO_OPTIONAL_CHECKS,
        false
    );

    if (opcUaConfig.getAssociatedResourceId() != null) {
      try {
        var thumbprint = CertificateUtils.getThumbprint(peer);
        opcUaConfig.setCertificateThumbprint(thumbprint);
        OpcUaCertificateUtils.sendUsageToCore(thumbprint, opcUaConfig.getAssociatedResourceId(), streamPipesClient);
      } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
        LOG.warn("Error sending certificate to opcUa", e);
      }
    }
  }

  private X509Certificate getEndEntity(List<X509Certificate> chain) {
    return chain.stream()
        .filter(c -> c.getBasicConstraints() < 0)
        .findFirst()
        .orElse(chain.get(0));
  }

  @Override
  public void validateCertificateChain(
      List<X509Certificate> certificateChain,
      String applicationUri,
      String... validHostNames
  ) throws UaException {

    validateCertificateChain(certificateChain);

    X509Certificate certificate = certificateChain.get(0);

    try {
      CertificateValidationUtil.checkApplicationUri(certificate, applicationUri);
    } catch (UaException e) {
      if (validationChecks.contains(ValidationCheck.APPLICATION_URI)) {
        throw e;
      } else {
        LOG.warn(
            "check suppressed: certificate failed application uri check: {} != {}",
            applicationUri, CertificateValidationUtil.getSubjectAltNameUri(certificate)
        );
      }
    }

    try {
      CertificateValidationUtil.checkHostnameOrIpAddress(certificate, validHostNames);
    } catch (UaException e) {
      if (validationChecks.contains(ValidationCheck.HOSTNAME)) {
        throw e;
      } else {
        LOG.warn(
            "check suppressed: certificate failed hostname check: {}",
            certificate.getSubjectX500Principal().getName()
        );
      }
    }
  }

  private void sendToCore(X509Certificate cert) {
    try {
      var certificate = CertificateBuilder.fromX509(cert, CertificateState.REJECTED);
      opcUaConfig.setCertificateThumbprint(certificate.getThumbprint());
      streamPipesClient.customRequest().sendPost(OpcUaCertificateUtils.getCoreCertificatePath(), certificate);
    } catch (Exception ex) {
      LOG.error("Failed to report rejected certificate to API", ex);
    }
  }

  private boolean isCertificateRejected(long statusCode) {
    return REJECTED_STATUS_CODES.contains(statusCode);
  }
}
