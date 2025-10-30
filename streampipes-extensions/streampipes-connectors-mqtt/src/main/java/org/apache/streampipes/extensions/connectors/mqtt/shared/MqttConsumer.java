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
import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.messaging.InternalEventProcessor;

import org.fusesource.mqtt.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;

public class MqttConsumer implements Runnable {

    private final InternalEventProcessor<byte[]> consumer;
    private boolean running;
    private int maxElementsToReceive = -1;
    private int messageCount = 0;

    private final MqttConfig mqttConfig;

    private static final Logger LOG = LoggerFactory.getLogger(MqttProtocol.class);

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer) {
        this.mqttConfig = mqttConfig;
        this.consumer = consumer;
    }

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer, int maxElementsToReceive) {
        this(mqttConfig, consumer);
        this.maxElementsToReceive = maxElementsToReceive;
    }

    public static TrustManager[] acceptAllCerts() {

        return new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {

                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {

                    }
                }
        };
    }

    public static X509Certificate getServerCertificate(String host, int port)
            throws NoSuchAlgorithmException, IOException, CertificateException {

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (Socket socket = factory.createSocket(host, port)) {

            ((SSLSocket) socket).startHandshake();

            SSLSession session = ((SSLSocket) socket).getSession();
            var certChain = session.getPeerCertificates();

            return (X509Certificate) certChain[0];
        }

    }

    private KeyStore loadServerKeyStore() throws FileNotFoundException, KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {

        var env = Environments.getEnvironment();

        FileInputStream keystoreFile = new FileInputStream(env.getKeystoreFilename().getValueOrDefault());
        KeyStore keystore = null;

        LOG.info(env.getKeystoreType().getValueOrDefault());
        LOG.info(env.getKeystoreFilename().getValueOrDefault());
        LOG.info(env.getKeystorePassword().getValueOrDefault());

        try {
            keystore = KeyStore.getInstance(env.getKeystoreType().getValueOrDefault());
            keystore.load(keystoreFile, env.getKeystorePassword().getValueOrDefault().toCharArray());
        } catch (FileNotFoundException e) {
            LOG.error("Keystore file not found: {}", keystoreFile);
            throw e;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            LOG.error("Error loading keystore from file: {}", keystoreFile, e);
            throw e;
        } finally {
            try {
                if (keystoreFile != null) {
                    keystoreFile.close();
                }
            } catch (IOException e) {
                LOG.error("Error closing keystore file: {}", keystoreFile, e);
            }
        }

        return keystore;
    }

    @Override
    public void run() {
        this.running = true;
        MQTT mqtt = new MQTT();
        LOG.info("TLS Enabled: " + mqttConfig.getTlsEnabled());
        var env = Environments.getEnvironment();

        try {
            mqtt.setHost(mqttConfig.getUrl());
            mqtt.setConnectAttemptsMax(1);

            if (mqttConfig.getAuthenticated()) {
                mqtt.setUserName(mqttConfig.getUsername());
                mqtt.setPassword(mqttConfig.getPassword());
            }

            if (mqttConfig.getTlsEnabled()) {
                KeyStore ks = null;
                TrustManagerFactory tmf = null;
                LOG.info(mqtt.getHost().getHost());
                var certs = getServerCertificate(mqtt.getHost().getHost(), 8883);// loadServerCert();

                if (env.getAllowSelfSignedCertificates().getValueOrDefault()) {
                    TrustManager[] tm = acceptAllCerts();
                    // Initialize SSLContext with the trust-all manager
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, tm, new java.security.SecureRandom());

                } else {

                    if (certs != null) {

                        try {
                            ks = loadServerKeyStore();
                            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                            tmf.init(ks);

                        } catch (FileNotFoundException e) {
                            LOG.error("FILE NOT FOUND" + e);
                            ks = KeyStore.getInstance(KeyStore.getDefaultType());
                            ks.load(null, null); // Create an empty KeyStore

                            int index = 0;
                            ks.setCertificateEntry("server_ca_" + index++, certs);

                            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                            tmf.init(ks);

                        }

                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                        mqtt.setSslContext(sslContext);
                    } else {
                        throw new RuntimeException("Failed to load server certificates for SSL.");
                    }
                }
            }
            BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();

            Topic[] topics = { new Topic(mqttConfig.getTopic(), QoS.AT_LEAST_ONCE) };
            connection.subscribe(topics);

            while (running && (maxElementsToReceive == -1 || messageCount < maxElementsToReceive)) {
                Message message = connection.receive();
                byte[] payload = message.getPayload();
                consumer.onEvent(payload);
                message.ack();
                messageCount++;
            }

            connection.disconnect();

        } catch (Exception e) {
            LOG.error("Error when receiving data from MQTT: " + e.getMessage());
            throw new RuntimeException("Error in MQTT consumer", e);
        }
    }

    public void close() {
        this.running = false;
    }

    public Integer getMessageCount() {
        return messageCount;
    }
}
