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
package org.apache.streampipes.rest.shared.serializer;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A runtime options error is sent to the core as JSON. A TLS failure against an untrusted certificate carries a
 * CertPathValidatorException whose sun.security certificate path Jackson cannot access on current JDKs.
 */
public class SpConfigurationExceptionSerializationTest {

  /** A self-signed certificate, only parsed here. */
  private static final String CERTIFICATE =
      "-----BEGIN CERTIFICATE-----\n"
        + "MIIBfjCCAQOgAwIBAgIJAPsHi85yuelMMAoGCCqGSM49BAMDMBMxETAPBgNVBAMT\n"
        + "CHRlc3QtcGxjMCAXDTI2MTExNTE2NDQ1MVoYDzIxMjYxMDIyMTY0NDUxWjATMREw\n"
        + "DwYDVQQDEwh0ZXN0LXBsYzB2MBAGByqGSM49AgEGBSuBBAAiA2IABMd8QhjDiC2N\n"
        + "aoZnWpYf2/+vx0dJU3RHTcE2ANA98z4HOjc/1KbZX5hCscCe7KjZoh5SLnPGUSRA\n"
        + "kPngELteC0kHt9g8j5irsBKsyOsDKTb5CyvZO5X6Edp1IKJeHu46aKMhMB8wHQYD\n"
        + "VR0OBBYEFAIyHzXOgF2PVA1Qh7VhxVKuxeXEMAoGCCqGSM49BAMDA2kAMGYCMQCv\n"
        + "Zi04zFzKCZoRd1vYtqdZBQLeaWSI/8jLpze0bvYBoChXJsByLrZHWot5FN5j8VEC\n"
        + "MQCMDJlmOfcoiBbvftWxQPiV0n9LQZmydwjMp8vnW+8T8NIKycJ6kwLonaX0PGu8\n"
        + "3bY=\n"
        + "-----END CERTIFICATE-----\n";

  private static JsonMapper streamPipesMapper() {
    var builder = JsonMapper.builder();
    new JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder);
    return builder.build();
  }

  private static SpConfigurationException untrustedCertificate() throws Exception {
    var factory = CertificateFactory.getInstance("X.509");
    var certificate = factory.generateCertificate(
        new ByteArrayInputStream(CERTIFICATE.getBytes(StandardCharsets.US_ASCII)));
    var path = factory.generateCertPath(List.of(certificate));
    return new SpConfigurationException("Cannot browse 192.168.23.30:443: the server certificate is not trusted",
        new CertificateException("Server certificate not trusted",
            new CertPathValidatorException("Path does not chain with any of the trust anchors", null, path, 0)));
  }

  @Test
  public void anUntrustedCertificateBreaksTheRawException() throws Exception {
    var e = untrustedCertificate();
    assertThrows(Exception.class, () -> streamPipesMapper().writeValueAsString(e));
  }

  @Test
  public void theSerializableCopyCarriesTheMessageAndItsCauses() throws Exception {
    var json = streamPipesMapper().writeValueAsString(untrustedCertificate().toSerializable());
    assertTrue(json.contains("the server certificate is not trusted"), json);
    assertTrue(json.contains("java.security.cert.CertPathValidatorException: Path does not chain"), json);
  }
}
