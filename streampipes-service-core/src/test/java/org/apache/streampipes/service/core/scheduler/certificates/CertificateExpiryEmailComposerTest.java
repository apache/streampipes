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

package org.apache.streampipes.service.core.scheduler.certificates;

import org.apache.streampipes.model.opcua.Certificate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateExpiryEmailComposerTest {

  private CertificateExpiryEmailComposer composer;

  @BeforeEach
  void setUp() {
    composer = new CertificateExpiryEmailComposer();
  }

  @Test
  public void composeMessage_shouldReturnEmptyString_whenNoExpiringCertificates() {
    var result = composer.composeMessage(Map.of());
    assertEquals("", result);
  }

  @Test
  public void composeMessage_shouldListSingleExpiringCertificate() {
    var server = "C=US,ST=CA,L=Folsom,OU=dev,O=digitalpetri,CN=Eclipse Milo OPC UA Demo Server";
    var cert = getCertificateForIssuer(server);

    var result = composer.composeMessage(java.util.Map.of(5, java.util.List.of(cert)));

    assertTrue(result.contains(server));
    assertTrue(result.contains("5 days"));
  }

  @Test
  public void composeMessage_shouldListMultipleExpiringCertificatesAcrossPeriods() {
    var serverA = "C=US,ST=CA,L=Folsom,OU=dev,O=digitalpetri,CN=Server A";
    var serverB = "C=US,ST=CA,L=Folsom,OU=dev,O=digitalpetri,CN=Server B";
    var cert5a = getCertificateForIssuer(serverA);
    var cert5b = getCertificateForIssuer(serverB);

    var serverC = "C=US,ST=CA,L=Folsom,OU=dev,O=digitalpetri,CN=Server C";
    var serverD = "C=US,ST=CA,L=Folsom,OU=dev,O=digitalpetri,CN=Server D";
    var cert7a = getCertificateForIssuer(serverC);
    var cert7b = getCertificateForIssuer(serverD);

    var map = new HashMap<Integer, List<Certificate>>();
    map.put(5, java.util.List.of(cert5a, cert5b));
    map.put(7, java.util.List.of(cert7a, cert7b));

    var result = composer.composeMessage(map);

    assertTrue(result.contains(serverA));
    assertTrue(result.contains(serverB));
    assertTrue(result.contains(serverC));
    assertTrue(result.contains(serverD));
    assertTrue(result.contains("5 days"));
    assertTrue(result.contains("7 days"));
  }

  private Certificate getCertificateForIssuer(String issuerDn) {
    var cert = new Certificate();
    cert.setIssuerDn(issuerDn);
    return cert;
  }

}