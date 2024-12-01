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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class KeyStoreLoader {

  private static final Logger LOG = LoggerFactory.getLogger(KeyStoreLoader.class);

  private X509Certificate[] clientCertificateChain;
  private X509Certificate clientCertificate;
  private KeyPair clientKeyPair;

  public KeyStoreLoader load(Environment env,
                             Path securityDir) throws Exception {
    var keystore = KeyStore.getInstance(env.getOpcUaKeystoreType().getValueOrDefault());
    var keystoreFile = env.getOpcUaKeystoreFile().getValueOrDefault();
    var keystorePassword = env.getOpcUaKeystorePassword().getValueOrDefault();
    var keystoreAlias = env.getOpcUaKeystoreAlias().getValueOrDefault();
    Path serverKeystore = securityDir.resolve(keystoreFile);
    char[] serverKeyStorePassword = keystorePassword.toCharArray();

    LOG.info("Loading KeyStore at {}", serverKeystore);

    try (InputStream in = Files.newInputStream(serverKeystore)) {
      keystore.load(in, serverKeyStorePassword);
    }

    Key clientPrivateKey = keystore.getKey(keystoreAlias, serverKeyStorePassword);
    if (clientPrivateKey instanceof PrivateKey) {
      clientCertificate = (X509Certificate) keystore.getCertificate(keystoreAlias);

      clientCertificateChain = Arrays.stream(keystore.getCertificateChain(keystoreAlias))
          .map(X509Certificate.class::cast)
          .toArray(X509Certificate[]::new);

      PublicKey serverPublicKey = clientCertificate.getPublicKey();
      clientKeyPair = new KeyPair(serverPublicKey, (PrivateKey) clientPrivateKey);
    }

    return this;
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
