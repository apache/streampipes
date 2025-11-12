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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.extensions.connectors.mqtt.security.SecurityUtils;

import org.fusesource.mqtt.client.MQTT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

public class MqttBase {

    protected final MqttConfig mqttConfig;

    private static final Logger LOG = LoggerFactory.getLogger(MqttBase.class);

    public MqttBase(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    protected MQTT setupMqttClient() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(mqttConfig.getUrl());
        mqtt.setConnectAttemptsMax(1);

        if (mqttConfig.getAuthenticated()) {
            mqtt.setUserName(mqttConfig.getUsername());
            mqtt.setPassword(mqttConfig.getPassword());
        }

        if (tlsEnabled(new URI(mqttConfig.getUrl()))) {
            configureTls(mqtt);
        }

        return mqtt;
    }


private static boolean tlsEnabled(URI brokerUri) {
    String protocol = brokerUri.getScheme();
    if (protocol == null) {
      return false;
    }
    String proto = protocol.toLowerCase();
    return proto.equals("ssl") || proto.equals("tls") || proto.equals("mqtts");
  }
    private void configureTls(MQTT mqtt) throws Exception {
        LOG.info("Configuring TLS for MQTT connection...");
        KeyStore keyStore = null;

        var env = Environments.getEnvironment();
        boolean acceptAllCerts = env.getAllowSelfSignedCertificates().getValueOrDefault();

        if (acceptAllCerts) {
            LOG.info("Accepting all certificates...");
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, SecurityUtils.acceptAllCerts(), new SecureRandom());
            mqtt.setSslContext(sslContext);
            return;
        }

        keyStore = loadKeyStore();
        TrustManagerFactory trustManagerFactory = SecurityUtils.createTrustManagerFactory(keyStore);

        KeyManager[] keyManagers = null;
        if (mqttConfig.getClientCertificatePath() != null && mqttConfig.getClientKeyPath() != null) {
            keyManagers = SecurityUtils.loadClientKeyManagers(
                    mqttConfig.getClientCertificatePath(),
                    mqttConfig.getClientKeyPath());
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());
        mqtt.setSslContext(sslContext);
    }

    private KeyStore loadKeyStore() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        try {
            return SecurityUtils.loadServerKeyStore();
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            LOG.error("Error loading keystore from file: {}", e);
            throw e;  // Re-throwing to handle it at the top level
        }
    }
}
