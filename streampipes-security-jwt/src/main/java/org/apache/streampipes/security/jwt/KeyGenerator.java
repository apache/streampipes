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

import org.apache.streampipes.commons.environment.Environments;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class KeyGenerator {

  public Key makeKeyForSecret(String tokenSecret) {
    return Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
  }

  public Key makeKeyForSecret(String alg,
                              String tokenSecret) throws IOException {
    return makeKeyForSecret(alg, tokenSecret, readKey());
  }

  public Key makeKeyForSecret(String alg,
                              String tokenSecret,
                              String pkContent) {
    if ("RS256".equals(alg)) {
      try {
        return makeKeyForRsa(pkContent);
      } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | IllegalArgumentException e) {
        throw new IllegalStateException("Could not load configured JWT public key", e);
      }
    }
    if (!"HS256".equals(alg) && !"HS384".equals(alg) && !"HS512".equals(alg)) {
      throw new UnsupportedJwtException("Unsupported JWT algorithm");
    }
    Key key = makeKeyForSecret(tokenSecret);
    if (!SignatureAlgorithm.forSigningKey(key).getValue().equals(alg)) {
      throw new UnsupportedJwtException("JWT algorithm does not match the configured key");
    }
    return key;
  }

  public String readKey() throws IOException {
    var publicKeyLoc = Environments.getEnvironment().getJwtPublicKeyLoc().getValue();
    return Files.readString(Paths.get(publicKeyLoc), Charset.defaultCharset());
  }

  public Key makeKeyForRsa(String key) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    byte[] decoded = KeyUtils.extractPublic(key);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
    return keyFactory.generatePublic(keySpec);
  }
}
