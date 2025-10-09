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

package org.apache.streampipes.extensions.connectors.opcua.config.identity;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class X509IdentityConfig implements IdentityConfig {

  private final X509Certificate certificate;
  private final PrivateKey privateKey;

  /**
   * @param certificatePem String containing one X.509 certificate in PEM
   *                       (-----BEGIN CERTIFICATE----- ... -----END CERTIFICATE-----)
   * @param privateKeyPem  String containing a PKCS#8 private key in PEM
   *                       (-----BEGIN PRIVATE KEY----- ... -----END PRIVATE KEY-----)
   */
  public X509IdentityConfig(String certificatePem, String privateKeyPem) {
    this.certificate = parseCertificatePem(certificatePem);
    this.privateKey  = parsePrivateKeyPem(privateKeyPem);
  }

  @Override
  public void configureIdentity(OpcUaClientConfigBuilder builder) {
    builder.setIdentityProvider(new X509IdentityProvider(certificate, privateKey));
  }

  @Override
  public String toString() {
    try {
      String subject = certificate.getSubjectX500Principal().getName();
      String thumb = DigestUtils.sha256Hex(certificate.getEncoded());
      return String.format("%s [%s]", subject, thumb);
    } catch (Exception e) {
      return "X509PemIdentity";
    }
  }

  private X509Certificate parseCertificatePem(String pem) {
    Objects.requireNonNull(pem, "certificatePem");
    byte[] der = extractPemBlock(pem, "CERTIFICATE");
    try {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(der));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse X.509 certificate PEM", e);
    }
  }

  private PrivateKey parsePrivateKeyPem(String pem) {
    Objects.requireNonNull(pem, "privateKeyPem");
    byte[] der = extractPemBlock(pem, "PRIVATE KEY"); // PKCS#8
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);

    // Try common algorithms in order.
    String[] algos = new String[] { "RSA", "EC", "Ed25519", "Ed448" };
    for (String algo : algos) {
      try {
        KeyFactory kf = KeyFactory.getInstance(algo);
        return kf.generatePrivate(spec);
      } catch (Exception ignore) {
        // try next
      }
    }
    throw new IllegalArgumentException(
        "Unsupported or invalid PKCS#8 private key. " +
            "Make sure it is an unencrypted PKCS#8 key (BEGIN PRIVATE KEY).");
  }

  private static byte[] extractPemBlock(String pem, String type) {
    String begin = "-----BEGIN " + type + "-----";
    String end   = "-----END " + type + "-----";

    String normalized = pem.replace("\r", "");
    int start = normalized.indexOf(begin);
    int stop  = normalized.indexOf(end);
    if (start < 0 || stop < 0) {
      throw new IllegalArgumentException("Missing PEM markers for " + type);
    }
    String base64 = normalized.substring(start + begin.length(), stop)
        .replace("\n", "")
        .replace("\t", "")
        .replace(" ", "");
    return Base64.getMimeDecoder().decode(base64.getBytes(StandardCharsets.US_ASCII));
  }
}
