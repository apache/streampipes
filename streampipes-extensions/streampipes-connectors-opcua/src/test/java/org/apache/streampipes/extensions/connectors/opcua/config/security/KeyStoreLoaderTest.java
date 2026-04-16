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

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeyStoreLoaderTest {

  private static final String KEYSTORE_TYPE = "PKCS12";
  private static final String KEYSTORE_FILE = "client.pfx";
  private static final String KEYSTORE_PASSWORD = "password";
  private static final String KEYSTORE_ALIAS = "apache-streampipes";
  private static final String APPLICATION_URI = "urn:org:apache:streampipes:opcua:test";

  @TempDir
  Path tempDir;

  @Test
  void loadCreatesConfiguredKeyStoreWhenMissing() throws Exception {
    var loader = new KeyStoreLoader().load(mockEnvironment(), tempDir);

    Path keyStorePath = tempDir.resolve(KEYSTORE_FILE);
    assertTrue(Files.exists(keyStorePath));
    assertNotNull(loader.getClientCertificate());
    assertNotNull(loader.getClientKeyPair());
    assertEquals(1, loader.getClientCertificateChain().length);
    assertEquals(-1, loader.getClientCertificate().getBasicConstraints());
    assertTrue(loader.getClientCertificate().getExtendedKeyUsage().contains("1.3.6.1.5.5.7.3.1"));
    assertTrue(loader.getClientCertificate().getExtendedKeyUsage().contains("1.3.6.1.5.5.7.3.2"));
    assertTrue(hasSubjectAlternativeName(loader.getClientCertificate(), 6, APPLICATION_URI));

    var keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
    try (InputStream in = Files.newInputStream(keyStorePath)) {
      keyStore.load(in, KEYSTORE_PASSWORD.toCharArray());
    }

    assertTrue(keyStore.containsAlias(KEYSTORE_ALIAS));
  }

  @Test
  void loadReusesExistingKeyStore() throws Exception {
    var environment = mockEnvironment();

    var firstLoad = new KeyStoreLoader().load(environment, tempDir);
    var secondLoad = new KeyStoreLoader().load(environment, tempDir);

    assertArrayEquals(firstLoad.getClientCertificate().getEncoded(), secondLoad.getClientCertificate().getEncoded());
    assertArrayEquals(firstLoad.getClientKeyPair().getPrivate().getEncoded(), secondLoad.getClientKeyPair().getPrivate().getEncoded());
  }

  private boolean hasSubjectAlternativeName(X509Certificate certificate,
                                            int expectedType,
                                            String expectedValue) throws Exception {
    Collection<List<?>> subjectAlternativeNames = certificate.getSubjectAlternativeNames();
    if (subjectAlternativeNames == null) {
      return false;
    }

    return subjectAlternativeNames.stream()
        .anyMatch(entry -> entry.size() >= 2
            && expectedType == (Integer) entry.get(0)
            && expectedValue.equals(entry.get(1)));
  }

  private Environment mockEnvironment() {
    var environment = mock(Environment.class);
    var keystoreType = mockVariable(KEYSTORE_TYPE);
    var keystoreFile = mockVariable(KEYSTORE_FILE);
    var keystorePassword = mockVariable(KEYSTORE_PASSWORD);
    var keystoreAlias = mockVariable(KEYSTORE_ALIAS);
    var applicationUri = mockVariable(APPLICATION_URI);

    when(environment.getOpcUaKeystoreType()).thenReturn(keystoreType);
    when(environment.getOpcUaKeystoreFile()).thenReturn(keystoreFile);
    when(environment.getOpcUaKeystorePassword()).thenReturn(keystorePassword);
    when(environment.getOpcUaKeystoreAlias()).thenReturn(keystoreAlias);
    when(environment.getOpcUaApplicationUri()).thenReturn(applicationUri);
    return environment;
  }

  private StringEnvironmentVariable mockVariable(String value) {
    return new StringEnvironmentVariable(Envs.SP_HOST) {
      @Override
      public String getValue() {
        return value;
      }

      @Override
      public boolean exists() {
        return true;
      }

      @Override
      public String getValueOrDefault() {
        return value;
      }
    };
  }
}
