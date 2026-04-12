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

import org.apache.streampipes.commons.environment.Environment;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class KeyStoreLoader {

  private static final Logger LOG = LoggerFactory.getLogger(KeyStoreLoader.class);
  private static final Pattern IPV4_PATTERN =
      Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
  private static final String CLIENT_COMMON_NAME = "Apache StreamPipes OPC UA Client";
  private static final String CLIENT_ORGANIZATION = "Apache StreamPipes";
  private static final String CLIENT_ORGANIZATIONAL_UNIT = "OPC UA";

  private X509Certificate[] clientCertificateChain;
  private X509Certificate clientCertificate;
  private KeyPair clientKeyPair;

  public KeyStoreLoader load(Environment env,
                             Path securityDir) throws Exception {
    var keystore = KeyStore.getInstance(env.getOpcUaKeystoreType().getValueOrDefault());
    var keystoreFile = env.getOpcUaKeystoreFile().getValueOrDefault();
    var keystorePassword = env.getOpcUaKeystorePassword().getValueOrDefault();
    var keystoreAlias = env.getOpcUaKeystoreAlias().getValueOrDefault();
    var applicationUri = env.getOpcUaApplicationUri().getValueOrDefault();
    Path serverKeystore = securityDir.resolve(keystoreFile);
    char[] serverKeyStorePassword = keystorePassword.toCharArray();

    LOG.info("Loading KeyStore at {}", serverKeystore);

    Files.createDirectories(securityDir);

    if (!Files.exists(serverKeystore)) {
      initializeKeyStore(
          keystore,
          serverKeystore,
          serverKeyStorePassword,
          keystoreAlias,
          applicationUri
      );
    } else {
      try (InputStream in = Files.newInputStream(serverKeystore)) {
        keystore.load(in, serverKeyStorePassword);
      }
    }

    Key clientPrivateKey = keystore.getKey(keystoreAlias, serverKeyStorePassword);
    if (!(clientPrivateKey instanceof PrivateKey privateKey)) {
      throw new IllegalStateException("No private key found in OPC UA keystore for alias " + keystoreAlias);
    }

    clientCertificate = (X509Certificate) keystore.getCertificate(keystoreAlias);
    if (clientCertificate == null) {
      throw new IllegalStateException("No certificate found in OPC UA keystore for alias " + keystoreAlias);
    }

    clientCertificateChain = Arrays.stream(keystore.getCertificateChain(keystoreAlias))
        .map(X509Certificate.class::cast)
        .toArray(X509Certificate[]::new);

    PublicKey serverPublicKey = clientCertificate.getPublicKey();
    clientKeyPair = new KeyPair(serverPublicKey, privateKey);

    return this;
  }

  private void initializeKeyStore(KeyStore keyStore,
                                  Path keyStorePath,
                                  char[] password,
                                  String keyStoreAlias,
                                  String applicationUri) throws Exception {
    Files.createDirectories(keyStorePath.getParent());

    keyStore.load(null, password);

    KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

    var dnsNames = new LinkedHashSet<String>();
    var ipAddresses = new LinkedHashSet<String>();
    collectSubjectAlternativeNames(dnsNames, ipAddresses);

    var certificateBuilder = new SelfSignedCertificateBuilder(keyPair)
        .setCommonName(CLIENT_COMMON_NAME)
        .setOrganization(CLIENT_ORGANIZATION)
        .setOrganizationalUnit(CLIENT_ORGANIZATIONAL_UNIT)
        .setApplicationUri(applicationUri);

    dnsNames.forEach(certificateBuilder::addDnsName);
    ipAddresses.forEach(certificateBuilder::addIpAddress);

    X509Certificate certificate = certificateBuilder.build();
    keyStore.setKeyEntry(keyStoreAlias, keyPair.getPrivate(), password, new X509Certificate[]{certificate});

    try (OutputStream out = Files.newOutputStream(keyStorePath)) {
      keyStore.store(out, password);
    }

    LOG.info("Created OPC UA client KeyStore at {}", keyStorePath);
  }

  private void collectSubjectAlternativeNames(Set<String> dnsNames,
                                              Set<String> ipAddresses) {
    dnsNames.add("localhost");
    ipAddresses.add("127.0.0.1");
    ipAddresses.add("::1");

    try {
      InetAddress localHost = InetAddress.getLocalHost();
      addIpAddress(localHost, ipAddresses);
      addDnsName(localHost.getHostName(), dnsNames);
    } catch (IOException e) {
      LOG.debug("Could not resolve local host for OPC UA client certificate generation", e);
    }

    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      if (networkInterfaces == null) {
        return;
      }

      while (networkInterfaces.hasMoreElements()) {
        var networkInterface = networkInterfaces.nextElement();
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
          addIpAddress(inetAddresses.nextElement(), ipAddresses);
        }
      }
    } catch (Exception e) {
      LOG.debug("Could not enumerate local network interfaces for OPC UA client certificate generation", e);
    }
  }

  private void addIpAddress(InetAddress address,
                            Set<String> ipAddresses) {
    String hostAddress = sanitizeHost(address.getHostAddress());
    if (!hostAddress.isBlank()) {
      ipAddresses.add(hostAddress);
    }
  }

  private void addDnsName(String candidate, Set<String> dnsNames) {
    String sanitized = sanitizeHost(candidate);
    if (sanitized.isBlank() || isIpLiteral(sanitized)) {
      return;
    }

    dnsNames.add(sanitized);
  }

  private String sanitizeHost(String candidate) {
    if (candidate == null) {
      return "";
    }

    int zoneSeparator = candidate.indexOf('%');
    String sanitized = zoneSeparator >= 0 ? candidate.substring(0, zoneSeparator) : candidate;
    return sanitized.trim();
  }

  private boolean isIpLiteral(String candidate) {
    return IPV4_PATTERN.matcher(candidate).matches() || candidate.contains(":");
  }

  public X509Certificate getClientCertificate() {
    return clientCertificate;
  }

  public X509Certificate[] getClientCertificateChain() {
    return clientCertificateChain;
  }

  public KeyPair getClientKeyPair() {
    return clientKeyPair;
  }
}
