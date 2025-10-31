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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        KeyStore keyStore = loadServerKeyStore();
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(keyStore);
        // === NEW CODE: Add client certificate and key (for two-way auth) ===
        KeyManager[] keyManagers = null;
        if (mqttConfig.getClientCertificatePath() != null && mqttConfig.getClientKeyPath() != null) {
            LOG.info("Loading client certificate for mutual TLS authentication...");
            keyManagers = loadClientKeyManagers(
                    mqttConfig.getClientCertificatePath(),
                    mqttConfig.getClientKeyPath()
            );
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        LOG.info("SSL CONTEYTSET");
        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());
        mqtt.setSslContext(sslContext);
    }

    private KeyManager[] loadClientKeyManagers(String certPem, String keyPem) throws Exception {


        X509Certificate certificate = parseCertificateFromPem(certPem);
        PrivateKey privateKey = parsePrivateKeyFromPem(keyPem);

        String password = ""; // no password for in-memory keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("client", privateKey, password.toCharArray(), new java.security.cert.Certificate[]{certificate});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password.toCharArray());
        return kmf.getKeyManagers();
    }

    private X509Certificate parseCertificateFromPem(String pem) throws Exception {

    LOG.info("CEET"+pem);
    String normalized = pem
            .replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")
            .replaceAll("\\s+", "");
    byte[] decoded = java.util.Base64.getDecoder().decode(normalized);
    java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(decoded));
}

private PrivateKey parsePrivateKeyFromPem(String pem) throws Exception {
    // Normalize to remove all escaped newlines and carriage returns
    pem = pem.replace("\\n", "\n")
             .replace("\\r", "")
             .replace("\r", "")
             .trim();

    if (pem.contains("-----BEGIN RSA PRIVATE KEY-----")) {
        return parsePkcs1PrivateKey(pem);
   // } else if (pem.contains("-----BEGIN PRIVATE KEY-----")) {
    //    return parsePkcs8PrivateKey(pem);
    } else {
        throw new IllegalArgumentException("Unsupported key format: missing BEGIN/END markers");
    }
}

private PrivateKey parsePkcs1PrivateKey(String pem) throws Exception {
    // Works even if PEM is a single long line
    Pattern p = Pattern.compile(
        "-----BEGIN RSA PRIVATE KEY-----([A-Za-z0-9+/=\\s]+)-----END RSA PRIVATE KEY-----"
    );
    Matcher m = p.matcher(pem.replaceAll("\\s+", ""));
    if (!m.find()) {
        // Fallback: try removing markers manually and strip spaces
        pem = pem.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                 .replaceAll("-----END RSA PRIVATE KEY-----", "")
                 .replaceAll("[\\s\\r\\n]", "")
                 .trim();
    } else {
        pem = m.group(1);
    }

    byte[] pkcs1Bytes = java.util.Base64.getMimeDecoder().decode(pem);
    byte[] pkcs8Bytes = convertPkcs1ToPkcs8(pkcs1Bytes);

    var keySpec = new java.security.spec.PKCS8EncodedKeySpec(pkcs8Bytes);
    var kf = java.security.KeyFactory.getInstance("RSA");
    return kf.generatePrivate(keySpec);
}
private byte[] convertPkcs1ToPkcs8(byte[] pkcs1Bytes) throws IOException {
    // Wrap the PKCS#1 structure in a PKCS#8 header
    final byte[] pkcs8Header = new byte[] {
        0x30, (byte)0x82, // SEQUENCE, length (will be recalculated)
        // placeholder bytes (we’ll patch them below)
        0, 0, 
        0x02, 0x01, 0x00, // INTEGER 0
        0x30, 0x0d,       // SEQUENCE (AlgorithmIdentifier)
        0x06, 0x09,       // OID (rsaEncryption)
        0x2a, (byte)0x86, 0x48, (byte)0x86, (byte)0xf7, 0x0d, 0x01, 0x01, 0x01,
        0x05, 0x00,       // NULL
        0x04, (byte)0x82, // OCTET STRING, length (will be recalculated)
        0, 0              // placeholder
    };

    int pkcs1Length = pkcs1Bytes.length;
    int totalLength = pkcs8Header.length + pkcs1Length;

    // Update the placeholder lengths
    pkcs8Header[2] = (byte)((totalLength - 4) >> 8);
    pkcs8Header[3] = (byte)(totalLength - 4);
    pkcs8Header[pkcs8Header.length - 2] = (byte)(pkcs1Length >> 8);
    pkcs8Header[pkcs8Header.length - 1] = (byte)(pkcs1Length);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(pkcs8Header);
    out.write(pkcs1Bytes);
    return out.toByteArray();
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
