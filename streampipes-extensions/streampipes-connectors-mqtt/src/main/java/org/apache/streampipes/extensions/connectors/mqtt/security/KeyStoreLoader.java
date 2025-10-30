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
package org.apache.streampipes.extensions.connectors.mqtt.security;

import org.apache.streampipes.commons.environment.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManagerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

/**
import javax.security.auth.x500.X500Principal;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

public class KeyStoreLoader {

  private static final Logger LOG = LoggerFactory.getLogger(KeyStoreLoader.class);

  private TrustManagerFactory trustManagerFactory;

  public KeyStoreLoader load(Environment env, Path securityDir) throws Exception {
    var keystore = KeyStore.getInstance(env.getKeystoreType().getValueOrDefault());
    var keystoreFile = env.getKeystoreFilename().getValueOrDefault();
    var keystorePassword = env.getKeystorePassword().getValueOrDefault();

    Path serverKeystore = securityDir.resolve(keystoreFile);
    char[] serverKeyStorePassword = keystorePassword.toCharArray();

    LOG.info("Loading KeyStore from {}", serverKeystore);

    //TODO this should npt be necessary Check if keystore exists, if not create one
      /**if (Files.notExists(serverKeystore)) {
            LOG.info("Keystore file not found, generating a new one at {}", serverKeystore);
            createNewKeyStore(serverKeystore, serverKeyStorePassword);
        } else {
            LOG.info("Loading existing keystore from {}", serverKeystore);*/
            try (InputStream in = Files.newInputStream(serverKeystore)) {
                keystore.load(in, serverKeyStorePassword);
            }
       // }

    // Initialize TrustManagerFactory with loaded keystore
    trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keystore);

    LOG.info("TrustManagerFactory initialized using keystore {}", serverKeystore);

    return this;
  }

/**
  private void createNewKeyStore(Path keystorePath, char[] password) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // 2048-bit RSA key
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Generate self-signed certificate
        X509Certificate certificate = generateSelfSignedCertificate(keyPair);

        // Create KeyStore and store the keypair and certificate
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null); // Initialize a new keystore
        keystore.setCertificateEntry("mycert", certificate);
        keystore.setKeyEntry("mykey", keyPair.getPrivate(), password, new java.security.cert.Certificate[]{certificate});

        // Save keystore to file
        try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
            keystore.store(fos, password);
        }

        LOG.info("New keystore generated at {}", keystorePath);
    }

    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        // Set validity for 1 year
        long currentTime = System.currentTimeMillis();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.YEAR, 1);
        Date validityEnd = calendar.getTime();

        // Generate the certificate
        X509Certificate certificate = CertUtils.generateSelfSignedCertificate(
                new X500Principal("CN=localhost"), keyPair, currentTime, validityEnd);

        return certificate;
    }*/

  public TrustManagerFactory getTrustManagerFactory() {
    if (trustManagerFactory == null) {
      throw new IllegalStateException("TrustManagerFactory not initialized. Call load() first.");
    }
    return trustManagerFactory;
  }
}