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

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;

public class MqttBase {

    protected final MqttConfig mqttConfig;

    private static final Logger LOG = LoggerFactory.getLogger(MqttBase.class);

    public MqttBase(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    protected Mqtt3AsyncClient setupMqttClient() throws Exception {
        URI brokerUri = new URI(mqttConfig.getUrl());
        boolean tls = tlsEnabled(brokerUri);

        var builder = MqttClient.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerUri.getHost())
                .serverPort(resolvePort(brokerUri))
                .useMqttVersion3();

        if (mqttConfig.getAuthenticated()) {
            builder.simpleAuth()
                    .username(mqttConfig.getUsername())
                    .password(mqttConfig.getPassword().getBytes())
                    .applySimpleAuth();
        }

        if (tls) {
            var sslContext = configureTls();
            builder.sslConfig(sslContext);
        }

        Mqtt3AsyncClient client = builder.buildAsync();

        return client;
    }

    private int resolvePort(URI uri) {
        return uri.getPort();

    }

    private static boolean tlsEnabled(URI brokerUri) {
        String protocol = brokerUri.getScheme();
        if (protocol == null) {
            return false;
        }
        String proto = protocol.toLowerCase();
        return proto.equals("ssl") || proto.equals("tls") || proto.equals("mqtts");
    }

    private MqttClientSslConfig configureTls() throws Exception {

        KeyStore keyStore = null;

        var env = Environments.getEnvironment();
        boolean acceptAllCerts = env.getAllowSelfSignedCertificates().getValueOrDefault();

        if (acceptAllCerts) {

            var sslContext = MqttClientSslConfig.builder().keyManagerFactory(null)
                    .trustManagerFactory(SecurityUtils.createAcceptAllFactory())
                    .build();
            return sslContext;
        }

        keyStore = loadKeyStore();
        TrustManagerFactory trustManagerFactory = SecurityUtils.createTrustManagerFactory(keyStore);

        KeyManagerFactory keyManagers = null;
        if (mqttConfig.getClientCertificatePath() != null && mqttConfig.getClientKeyPath() != null) {
            keyManagers = SecurityUtils.loadClientKeyManagers(
                    mqttConfig.getClientCertificatePath(),
                    mqttConfig.getClientKeyPath());
        }

        var sslContext = MqttClientSslConfig.builder().keyManagerFactory(keyManagers)
                .trustManagerFactory(trustManagerFactory)
                .build();
        return sslContext;
    }

    private KeyStore loadKeyStore()
            throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        try {
            return SecurityUtils.loadServerKeyStore();
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            LOG.error("Error loading keystore from file: {}", e);
            throw e;
        }
    }
}
