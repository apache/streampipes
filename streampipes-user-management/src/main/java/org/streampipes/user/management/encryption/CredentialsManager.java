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
package org.streampipes.user.management.encryption;

import org.apache.commons.codec.digest.DigestUtils;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.user.management.service.UserService;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class CredentialsManager {

  private static final String COLON = ":";
  private static final String TRANSFORMATION = "PBEWithMD5AndDES";

  private static String encrypt(String email, byte[] property) throws GeneralSecurityException {
    String[] userProperties = getUserProperties(email);
    byte[] salt = getShortenedSalt(userProperties[1]);

    SecretKey key = makeSecretKey(userProperties[2].toCharArray());
    Cipher pbeCipher = Cipher.getInstance(TRANSFORMATION);
    pbeCipher.init(Cipher.ENCRYPT_MODE, key,
            new PBEParameterSpec(salt, 20));

    return Base64.getEncoder().encodeToString(pbeCipher.doFinal(property));
  }

  private static String encrypt(String email, char[] property) throws GeneralSecurityException {
    byte[] bytes = new byte[property.length];
    for (int i = 0; i < property.length; i++) {
      bytes[i] = (byte) property[i];
    }
    return encrypt(email, bytes);
  }

  public static String encrypt(String email, String property) throws GeneralSecurityException {
    return encrypt(email, property.getBytes());
  }

  public static String decrypt(String email, String property) throws GeneralSecurityException {
    String[] userProperties = getUserProperties(email);
    byte[] salt = getShortenedSalt(userProperties[1]);
    SecretKey key = makeSecretKey(userProperties[2].toCharArray());
    Cipher pbeCipher = Cipher.getInstance(TRANSFORMATION);
    pbeCipher.init(Cipher.DECRYPT_MODE, key,
            new PBEParameterSpec(salt,
                    20));
    return new String(pbeCipher.doFinal(Base64.getDecoder().decode(property)));
  }

  private static SecretKey makeSecretKey(char[] key) throws NoSuchAlgorithmException,
          InvalidKeySpecException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRANSFORMATION);
    return keyFactory.generateSecret(new PBEKeySpec(key));
  }

  private static String[] getUserProperties(String email) {
    String hashedProperty = new UserService(email).getPassword();
    return hashedProperty.split(COLON);
  }

  private static byte[] getShortenedSalt(String salt) {
    String mergedSalt = DigestUtils.sha256Hex(salt + getBackendEncryptionKey());
    return Arrays.copyOfRange(mergedSalt.getBytes(), 0, 8);
  }

  private static String getBackendEncryptionKey() {
    return BackendConfig.INSTANCE.getEncryptionKey();
  }
}
