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

package org.apache.streampipes.integration.sinks.azure;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CosmosDbEmulatorTrustStore implements AutoCloseable {

  private static final char[] TRUST_STORE_PASSWORD = "changeit".toCharArray();
  private static final String TRUST_STORE_TYPE = "JKS";

  private final String certificateEndpoint;
  private Path trustStorePath;
  private String previousTrustStore;
  private String previousTrustStorePassword;
  private String previousTrustStoreType;

  public CosmosDbEmulatorTrustStore(String certificateEndpoint) {
    this.certificateEndpoint = certificateEndpoint;
  }

  public void install() throws Exception {
    rememberSystemProperties();

    byte[] pemBytes = downloadCertificate();
    Certificate certificate = readCertificate(pemBytes);

    KeyStore keyStore = KeyStore.getInstance(TRUST_STORE_TYPE);
    keyStore.load(null, TRUST_STORE_PASSWORD);
    keyStore.setCertificateEntry("cosmos-emulator", certificate);

    this.trustStorePath = Files.createTempFile("cosmos-emulator", ".jks");
    try (var outputStream = Files.newOutputStream(trustStorePath)) {
      keyStore.store(outputStream, TRUST_STORE_PASSWORD);
    }

    System.setProperty("javax.net.ssl.trustStore", trustStorePath.toString());
    System.setProperty("javax.net.ssl.trustStorePassword", new String(TRUST_STORE_PASSWORD));
    System.setProperty("javax.net.ssl.trustStoreType", TRUST_STORE_TYPE);
  }

  @Override
  public void close() throws Exception {
    restoreSystemProperty("javax.net.ssl.trustStore", previousTrustStore);
    restoreSystemProperty("javax.net.ssl.trustStorePassword", previousTrustStorePassword);
    restoreSystemProperty("javax.net.ssl.trustStoreType", previousTrustStoreType);

    if (trustStorePath != null) {
      Files.deleteIfExists(trustStorePath);
      trustStorePath = null;
    }
  }

  private byte[] downloadCertificate() throws Exception {
    Exception lastException = null;

    for (int attempt = 0; attempt < 30; attempt++) {
      try {
        return fetchCertificate();
      } catch (Exception e) {
        lastException = e;
        Thread.sleep(1000);
      }
    }

    throw lastException;
  }

  private byte[] fetchCertificate() throws Exception {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] {new TrustAllX509Manager()}, new SecureRandom());

    URL certificateUrl = new URL(certificateEndpoint);
    int port = certificateUrl.getPort() > 0 ? certificateUrl.getPort() : certificateUrl.getDefaultPort();

    try (SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(
        certificateUrl.getHost(),
        port
    )) {
      socket.setSoTimeout(5000);
      socket.startHandshake();

      Certificate[] peerCertificates = socket.getSession().getPeerCertificates();
      if (peerCertificates.length == 0) {
        throw new IllegalStateException("No peer certificate returned by Cosmos emulator");
      }

      return ((X509Certificate) peerCertificates[0]).getEncoded();
    }
  }

  private Certificate readCertificate(byte[] pemBytes) throws Exception {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    return certificateFactory.generateCertificate(new ByteArrayInputStream(pemBytes));
  }

  private void rememberSystemProperties() {
    this.previousTrustStore = System.getProperty("javax.net.ssl.trustStore");
    this.previousTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    this.previousTrustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
  }

  private void restoreSystemProperty(String key, String value) {
    if (value == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, value);
    }
  }

  private static final class TrustAllX509Manager implements X509TrustManager {

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
      return new java.security.cert.X509Certificate[0];
    }
  }
}
