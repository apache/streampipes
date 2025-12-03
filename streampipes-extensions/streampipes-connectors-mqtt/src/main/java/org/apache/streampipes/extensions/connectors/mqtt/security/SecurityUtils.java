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

import org.apache.streampipes.commons.environment.Environments;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtils {

    public static final TrustManager[] ACCEPT_ALL = new TrustManager[]{
        new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }
    };


    public static class AcceptAllTMF extends TrustManagerFactorySpi {
        @Override
        protected void engineInit(KeyStore ks) { }

        @Override
        protected void engineInit(javax.net.ssl.ManagerFactoryParameters spec) { }

        @Override
        protected TrustManager[] engineGetTrustManagers() {
            return ACCEPT_ALL;
        }
    }
    
        public static class AcceptAllProvider extends Provider {

        AcceptAllProvider() {
            super("AcceptAllProvider", "1.0",
                  "A provider that supplies an all-trusting TrustManager");

            putService(new Service(
                this,
                "TrustManagerFactory",
                "AcceptAll",
                AcceptAllTMF.class.getName(),
                Collections.emptyList(),
                Collections.emptyMap()
            ));
        }
    }

    public static TrustManagerFactory createAcceptAllFactory() throws Exception {
        Provider provider = new AcceptAllProvider();
        Security.addProvider(provider);

        return TrustManagerFactory.getInstance("AcceptAll", provider);
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

    public static KeyStore loadServerKeyStore() throws FileNotFoundException, KeyStoreException, IOException,
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
            throw e;
        }
    }

    public static KeyManagerFactory loadClientKeyManagers(String certPem, String keyPem) throws Exception {
        X509Certificate certificate = parseCertificateFromPem(certPem);
        PrivateKey privateKey = parsePrivateKeyFromPem(keyPem);

        String password = ""; // no password for in-memory keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("client", privateKey, password.toCharArray(), new java.security.cert.Certificate[]{certificate});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password.toCharArray());
        return kmf;
    }

    public static X509Certificate parseCertificateFromPem(String pem) throws Exception {
        String normalized = pem
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
    }

    public static PrivateKey parsePrivateKeyFromPem(String pem) throws Exception {
        pem = pem.replace("\\n", "\n")
                 .replace("\\r", "")
                 .replace("\r", "")
                 .trim();

        if (pem.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            return parsePkcs1PrivateKey(pem);
        } else {
            throw new IllegalArgumentException("Unsupported key format: missing BEGIN/END markers");
        }
    }

    public static PrivateKey parsePkcs1PrivateKey(String pem) throws Exception {
        Pattern p = Pattern.compile(
            "-----BEGIN RSA PRIVATE KEY-----([A-Za-z0-9+/=\\s]+)-----END RSA PRIVATE KEY-----"
        );
        Matcher m = p.matcher(pem.replaceAll("\\s+", ""));
        if (!m.find()) {
            pem = pem.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                     .replaceAll("-----END RSA PRIVATE KEY-----", "")
                     .replaceAll("[\\s\\r\\n]", "")
                     .trim();
        } else {
            pem = m.group(1);
        }

        byte[] pkcs1Bytes = Base64.getMimeDecoder().decode(pem);
        byte[] pkcs8Bytes = convertPkcs1ToPkcs8(pkcs1Bytes);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8Bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static byte[] convertPkcs1ToPkcs8(byte[] pkcs1Bytes) throws IOException {
        final byte[] pkcs8Header = new byte[] {
            0x30, (byte)0x82, 
            0, 0, 
            0x02, 0x01, 0x00,
            0x30, 0x0d,
            0x06, 0x09,
            0x2a, (byte)0x86, 0x48, (byte)0x86, (byte)0xf7, 0x0d, 0x01, 0x01, 0x01,
            0x05, 0x00,
            0x04, (byte)0x82,
            0, 0
        };

        int pkcs1Length = pkcs1Bytes.length;
        int totalLength = pkcs8Header.length + pkcs1Length;

        pkcs8Header[2] = (byte)((totalLength - 4) >> 8);
        pkcs8Header[3] = (byte)(totalLength - 4);
        pkcs8Header[pkcs8Header.length - 2] = (byte)(pkcs1Length >> 8);
        pkcs8Header[pkcs8Header.length - 1] = (byte)(pkcs1Length);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(pkcs8Header);
        out.write(pkcs1Bytes);
        return out.toByteArray();
    }

    public static TrustManagerFactory createTrustManagerFactory(KeyStore keystore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        return trustManagerFactory;
    }
}
