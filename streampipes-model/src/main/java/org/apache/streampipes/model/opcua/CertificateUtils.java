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

package org.apache.streampipes.model.opcua;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HexFormat;

public class CertificateUtils {

  public static String getThumbprint(X509Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(certificate.getEncoded());
    return HexFormat.of().withUpperCase().formatHex(digest);
  }

  public static String getThumbprint(String certificateDerBase64) throws CertificateException, NoSuchAlgorithmException {
      byte[] derBytes = Base64.getDecoder().decode(certificateDerBase64);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate cert = (X509Certificate) cf.generateCertificate(
          new ByteArrayInputStream(derBytes));

      return getThumbprint(cert);
  }
}
