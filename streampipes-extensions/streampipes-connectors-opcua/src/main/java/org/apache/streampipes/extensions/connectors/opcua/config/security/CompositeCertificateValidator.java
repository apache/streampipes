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
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtils;
import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.model.opcua.CertificateState;

import org.eclipse.milo.opcua.stack.client.security.ClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.TrustListManager;
import org.eclipse.milo.opcua.stack.core.util.validation.CertificateValidationUtil;
import org.eclipse.milo.opcua.stack.core.util.validation.ValidationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
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

  private final TrustListManager trustListManager;
  private final List<X509Certificate> trustedCerts;
  private final List<ValidationCheck> validationChecks;
  private final IStreamPipesClient streamPipesClient;

  public CompositeCertificateValidator(TrustListManager trustListManager,
                                       List<X509Certificate> trustedCerts,
                                       List<ValidationCheck> validationChecks,
                                       IStreamPipesClient streamPipesClient) {
    this.trustListManager = trustListManager;
    this.trustedCerts = trustedCerts;
    this.validationChecks = validationChecks;
    this.streamPipesClient = streamPipesClient;
  }

  @Override
  public void validateCertificateChain(List<X509Certificate> certificateChain) throws UaException {
    PKIXCertPathBuilderResult certPathResult;

    try {
      certPathResult = CertificateValidationUtil.buildTrustedCertPath(
          certificateChain,
          Stream.concat(trustListManager.getTrustedCertificates().stream(), trustedCerts.stream()).toList(),
          trustListManager.getIssuerCertificates()
      );
    } catch (UaException e) {
      if (isCertificateRejected(e.getStatusCode().getValue())) {
        sendToCore(certificateChain.get(0));
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
      var certificate = new Certificate(
          cert.getSubjectX500Principal().getName(),
          cert.getIssuerX500Principal().getName(),
          cert.getSerialNumber().toString(),
          cert.getNotBefore().toString(),
          cert.getNotAfter().toString(),
          cert.getSigAlgName(),
          cert.getPublicKey().getAlgorithm(),
          Base64.getEncoder().encodeToString(cert.getEncoded()),
          CertificateState.REJECTED
      );

      streamPipesClient.customRequest().sendPost(OpcUaUtils.getCoreCertificatePath(), certificate);
    } catch (Exception ex) {
      LOG.error("Failed to report rejected certificate to API", ex);
    }
  }

  private boolean isCertificateRejected(long statusCode) {
    return REJECTED_STATUS_CODES.contains(statusCode);
  }
}
