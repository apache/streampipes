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

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger LOG = LoggerFactory.getLogger(KeyGenerator.class);

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
    if (alg.equals("RS256")) {
      try {
        return makeKeyForRsa(pkContent);
      } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
        LOG.error(
            "Could not properly create the provided key, defaulting to an HMAC token, "
                + "which will almost certainly lead to problems");
        return makeKeyForSecret(tokenSecret);
      }
    } else {
      return makeKeyForSecret(tokenSecret);
    }
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
