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

package org.apache.streampipes.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;

public class JwtTokenGenerator {

  public static String makeJwtToken(String subject,
                                    String tokenSecret,
                                    Date expirationDate) {

    return prepareJwtToken(subject, makeHmacKey(tokenSecret), expirationDate).compact();

  }

  public static String makeJwtToken(String subject,
                                    Path keyFilePath,
                                    Map<String, Object> claims,
                                    Date expirationDate)
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

    JwtBuilder builder = prepareJwtToken(subject, makeRsaKey(keyFilePath), expirationDate);

    return builder.addClaims(claims).compact();
  }

  public static String makeJwtToken(String subject,
                                    String tokenSecret,
                                    Map<String, Object> claims,
                                    Date expirationDate) {

    JwtBuilder builder = prepareJwtToken(subject, makeHmacKey(tokenSecret), expirationDate);

    return builder.addClaims(claims).compact();
  }

  private static SecretKey makeHmacKey(String tokenSecret) {
    return Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
  }

  private static RSAPrivateKey makeRsaKey(Path keyFilePath)
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    String key = Files.readString(keyFilePath, Charset.defaultCharset());

    byte[] decoded = KeyUtils.extractPrivate(key);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
  }

  private static JwtBuilder prepareJwtToken(String subject,
                                            Key key,
                                            Date expirationDate) {
    return Jwts
        .builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(expirationDate)
        .signWith(key);
  }
}
