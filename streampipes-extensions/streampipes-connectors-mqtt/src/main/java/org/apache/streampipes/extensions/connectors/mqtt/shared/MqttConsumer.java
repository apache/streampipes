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


import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.messaging.InternalEventProcessor;

import org.fusesource.mqtt.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;

import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
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

/**    private Certificate[] loadServerCert() {
        String serverUrl = mqttConfig.getUrl();
        LOG.info("Connecting to URL: " + serverUrl);

        try {
            URL url = new URL(serverUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            Certificate[] certificates = connection.getServerCertificates();
            LOG.info("Certificates received: " + certificates.length);

            if (certificates != null && certificates.length > 0) {
                X509Certificate serverCert = (X509Certificate) certificates[0];
                serverCert.checkValidity(); // Check if the certificate is valid
                LOG.info("Server Certificate: " + serverCert);
                return certificates;
            } else {
                LOG.error("No certificates received from the server.");
            }

            connection.disconnect();

        } catch (Exception e) {
            LOG.error("Error loading server certificate: " + e.getMessage());
        }
        return null;
    }
*/

public static X509Certificate getServerCertificate(String host, int port) throws NoSuchAlgorithmException, IOException, CertificateException {

  //TODO Trust Store necessary for selfsigned and certificates not in the java trusttore
                //try {
              // Create an SSL context with a TrustManager that does not perform any certificate validation
            //SSLContext sslContext = SSLContext.getInstance("TLS");
            //sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            /**    public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } }, new java.security.SecureRandom());*/

        // Create a socket and initiate SSL handshake
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (Socket socket = factory.createSocket(host, port)) {
            // Start the SSL handshake to establish a secure connection
            ((SSLSocket) socket).startHandshake();

            // Get the server's certificate chain
            SSLSession session = ((SSLSocket) socket).getSession();
           var certChain = session.getPeerCertificates();

            // Return the first certificate in the chain (usually the server's certificate)
            return (X509Certificate) certChain[0];
        }
               // } catch (KeyManagementException e) {
            //throw new IOException("SSLContext initialization failed", e);
       // }

    }
    @Override
    public void run() {
        this.running = true;
        MQTT mqtt = new MQTT();
        LOG.info("TLS Enabled: " + mqttConfig.getTlsEnabled());

        try {
            mqtt.setHost(mqttConfig.getUrl());
            mqtt.setConnectAttemptsMax(1);

            if (mqttConfig.getAuthenticated()) {
                mqtt.setUserName(mqttConfig.getUsername());
                mqtt.setPassword(mqttConfig.getPassword());
            }

            if (mqttConfig.getTlsEnabled()) {
                LOG.info("TLS is enabled. Initializing SSL context.");
                String regex = "^(ssl|mqtts?|tls):\\/\\/([^:\\/]+)(?::(\\d+))?$";

                //Pattern pattern = Pattern.compile(regex);
                //var matcher = pattern.matcher(mqttConfig.getUrl());
                //LOG.info(mqttConfig.getUrl());
                //LOG.info(matcher.group(0));
                //LOG.info(matcher.group(1));
                //LOG.info(matcher.group(2));

                var certs = getServerCertificate("0.0.0.0",8883);//loadServerCert();

                if (certs != null) {
                    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                    ks.load(null, null); // Create an empty KeyStore
                    int index = 0;
                    //for (Certificate cert : certs) {
                        ks.setCertificateEntry("server_ca_" + index++, certs);
                   // }

                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                    mqtt.setSslContext(sslContext);
                } else {
                    throw new RuntimeException("Failed to load server certificates for SSL.");
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
