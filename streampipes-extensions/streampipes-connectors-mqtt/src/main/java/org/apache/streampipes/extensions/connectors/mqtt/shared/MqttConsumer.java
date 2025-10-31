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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


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
        String keystoreFilename = env.getKeystoreFilename().getValueOrDefault();
        String keystoreType = env.getKeystoreType().getValueOrDefault();
        String keystorePassword = env.getKeystorePassword().getValueOrDefault();

        try (FileInputStream keystoreFile = new FileInputStream(keystoreFilename)) {
            KeyStore keystore = KeyStore.getInstance(keystoreType);
            keystore.load(keystoreFile, keystorePassword.toCharArray());
            return keystore;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            LOG.error("Error loading keystore from file: {}", keystoreFilename, e);
            throw e;
        }
    }

       @Override
    public void run() {
        this.running = true;
        try {
            MQTT mqtt = setupMqttClient();
            BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            subscribeToTopic(connection);
            processMessages(connection);
            connection.disconnect();
        } catch (Exception e) {
            LOG.error("Error in MQTT consumer: ", e);
        }
    }

        private void processMessages(BlockingConnection connection) throws Exception {
        while (running && (maxElementsToReceive == -1 || messageCount < maxElementsToReceive)) {
            Message message = connection.receive();
            byte[] payload = message.getPayload();
            consumer.onEvent(payload);
            message.ack();
            messageCount++;
        }
    }



    private MQTT setupMqttClient() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(mqttConfig.getUrl());
        mqtt.setConnectAttemptsMax(1);

        if (mqttConfig.getAuthenticated()) {
            mqtt.setUserName(mqttConfig.getUsername());
            mqtt.setPassword(mqttConfig.getPassword());
        }

        if (mqttConfig.getTlsEnabled()) {
            configureTls(mqtt);
        }

        return mqtt;
    }

        private void configureTls(MQTT mqtt) throws Exception {
        LOG.info("Configuring TLS for MQTT connection...");
        var env = Environments.getEnvironment();
        KeyStore keyStore = loadServerKeyStore();
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        mqtt.setSslContext(sslContext);
    }

      private TrustManagerFactory createTrustManagerFactory(KeyStore keystore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        return trustManagerFactory;
    }

        private void subscribeToTopic(BlockingConnection connection) throws Exception {
        Topic[] topics = {new Topic(mqttConfig.getTopic(), QoS.AT_LEAST_ONCE)};
        connection.subscribe(topics);
    }

    public void close() {
        this.running = false;
    }

    public Integer getMessageCount() {
        return messageCount;
    }
}
