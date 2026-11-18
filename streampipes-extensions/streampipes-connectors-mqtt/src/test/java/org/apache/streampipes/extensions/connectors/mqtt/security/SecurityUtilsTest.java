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

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.ReferenceCountUtil;
import org.junit.jupiter.api.Test;

import javax.net.ssl.X509ExtendedTrustManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SecurityUtilsTest {

  @Test
  void selfSignedFactoryProvidesExtendedTrustManager() throws Exception {
    var managers = SecurityUtils.createAcceptAllFactory().getTrustManagers();
    assertInstanceOf(X509ExtendedTrustManager.class, managers[0]);
  }

  @Test
  void selfSignedFactorySupportsOpenSslWithHostnameVerificationEnabled() throws Exception {
    assumeTrue(OpenSsl.isAvailable(), "Native OpenSSL is unavailable on this platform");
    var context = SslContextBuilder.forClient()
        .sslProvider(SslProvider.OPENSSL_REFCNT)
        .endpointIdentificationAlgorithm("HTTPS")
        .trustManager(SecurityUtils.createAcceptAllFactory())
        .build();
    try {
      assertNotNull(context);
    } finally {
      ReferenceCountUtil.release(context);
    }
  }
}
