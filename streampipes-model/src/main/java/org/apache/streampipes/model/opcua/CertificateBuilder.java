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

package org.apache.streampipes.model.opcua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500Principal;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class CertificateBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(CertificateBuilder.class);

  // fluent setters
  public CertificateBuilder subjectDn(String v) {
    cert.setSubjectDn(v);
    return this;
  }

  public CertificateBuilder issuerDn(String v) {
    cert.setIssuerDn(v);
    return this;
  }

  public CertificateBuilder serialNumber(String v) {
    cert.setSerialNumber(v);
    return this;
  }

  public CertificateBuilder notBefore(String v) {
    cert.setNotBefore(v);
    return this;
  }

  public CertificateBuilder notAfter(String v) {
    cert.setNotAfter(v);
    return this;
  }

  public CertificateBuilder sigAlgName(String v) {
    cert.setSigAlgName(v);
    return this;
  }

  public CertificateBuilder algorithm(String v) {
    cert.setAlgorithm(v);
    return this;
  }

  public CertificateBuilder basicConstraints(String v) {
    cert.setBasicConstraints(v);
    return this;
  }

  public CertificateBuilder keyUsages(List<String> v) {
    cert.setKeyUsages(v);
    return this;
  }

  public CertificateBuilder extendedKeyUsages(List<String> v) {
    cert.setExtendedKeyUsages(v);
    return this;
  }

  public CertificateBuilder subjectAlternativeNames(List<String> v) {
    cert.setSubjectAlternativeNames(v);
    return this;
  }

  public CertificateBuilder certificateDerBase64(String v) {
    cert.setCertificateDerBase64(v);
    return this;
  }

  private final Certificate cert;

  private CertificateBuilder() {
    cert = new Certificate();
  }

  public Certificate build() {
    return cert;
  }

  public static Certificate fromX509(X509Certificate cert, CertificateState state) {
    Objects.requireNonNull(cert, "cert");
    var b = new CertificateBuilder();

    var certificate = b
        .subjectDn(name(cert.getSubjectX500Principal()))
        .issuerDn(name(cert.getIssuerX500Principal()))
        .serialNumber(hex(cert.getSerialNumber()))
        .notBefore(cert.getNotBefore().toString())
        .notAfter(cert.getNotAfter().toString())
        .sigAlgName(cert.getSigAlgName())
        .algorithm(describePublicKey(cert.getPublicKey()))
        .basicConstraints(describeBasicConstraints(cert.getBasicConstraints()))
        .keyUsages(describeKeyUsage(cert.getKeyUsage()))
        .extendedKeyUsages(describeExtendedKeyUsage(safe(cert::getExtendedKeyUsage)))
        .subjectAlternativeNames(describeSANs(safe(cert::getSubjectAlternativeNames)))
        .certificateDerBase64(base64(safe(cert::getEncoded)))
        .build();

    certificate.setState(state);
    try {
      certificate.setThumbprint(CertificateUtils.getThumbprint(cert));
    } catch (Exception e) {
      LOG.warn("Could not create thumbprint for certificate: {}", e.getMessage());
    }

    return certificate;
  }

  private static String name(X500Principal p) {
    return p == null ? "" : p.getName(X500Principal.RFC2253);
  }

  private static String hex(BigInteger bi) {
    return bi == null ? "" : bi.toString(16).toUpperCase(Locale.ROOT);
  }

  private static String base64(byte[] bytes) {
    return bytes == null ? "" : Base64.getEncoder().encodeToString(bytes);
  }

  private static <T> T safe(SupplierWithThrow<T> s) {
    try {
      return s.get();
    } catch (Exception e) {
      return null;
    }
  }

  @FunctionalInterface
  private interface SupplierWithThrow<T> {
    T get() throws Exception;
  }

  private static String describePublicKey(PublicKey pk) {
    if (pk == null) {
      return "";
    }
    if (pk instanceof RSAPublicKey rsa) {
      return "RSA (" + rsa.getModulus().bitLength() + " bits)";
    }
    if (pk instanceof ECPublicKey ec) {
      return "EC (" + ec.getParams().getCurve().getField().getFieldSize() + " bits)";
    }
    if (pk instanceof DSAPublicKey dsa && dsa.getParams() != null) {
      return "DSA (" + dsa.getParams().getP().bitLength() + " bits)";
    }
    return pk.getAlgorithm();
  }

  private static String describeBasicConstraints(int bc) {
    if (bc < 0) {
      return "End-entity (no CA)";
    }
    if (bc == Integer.MAX_VALUE) {
      return "CA: true, pathLen: unlimited";
    }
    return "CA: true, pathLen: " + bc;
  }

  private static List<String> describeKeyUsage(boolean[] ku) {
    if (ku == null) {
      return List.of();
    }
    String[] names = {"digitalSignature", "contentCommitment", "keyEncipherment", "dataEncipherment",
        "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly"};
    List<String> out = new ArrayList<>();
    for (int i = 0; i < ku.length && i < names.length; i++) {
      if (ku[i]) {
        out.add(names[i]);
      }
    }
    return out;
  }

  private static final Map<String, String> EKU_KNOWN = Map.ofEntries(
      Map.entry("2.5.29.37.0", "anyExtendedKeyUsage"),
      Map.entry("1.3.6.1.5.5.7.3.1", "serverAuth"),
      Map.entry("1.3.6.1.5.5.7.3.2", "clientAuth"),
      Map.entry("1.3.6.1.5.5.7.3.3", "codeSigning"),
      Map.entry("1.3.6.1.5.5.7.3.4", "emailProtection"),
      Map.entry("1.3.6.1.5.5.7.3.8", "timeStamping"),
      Map.entry("1.3.6.1.5.5.7.3.9", "OCSPSigning")
  );

  private static List<String> describeExtendedKeyUsage(List<String> oids) {
    if (oids == null) {
      return List.of();
    }
    return oids.stream().map(oid -> EKU_KNOWN.getOrDefault(oid, "OID:" + oid)).toList();
  }

  private static List<String> describeSANs(Collection<List<?>> sans) {
    if (sans == null) {
      return List.of();
    }
    List<String> out = new ArrayList<>();
    for (List<?> entry : sans) {
      if (entry == null || entry.size() < 2) {
        continue;
      }
      int tag = (Integer) entry.get(0);
      Object val = entry.get(1);
      String label = switch (tag) {
        case 1 -> "rfc822Name";
        case 2 -> "DNS";
        case 6 -> "URI";
        case 7 -> "IP";
        default -> "SAN(" + tag + ")";
      };
      if (tag == 7 && val instanceof byte[] bytes) {
        try {
          val = InetAddress.getByAddress(bytes).getHostAddress();
        } catch (Exception ignored) {
        }
      }
      out.add(label + ": " + val);
    }
    return out;
  }
}

