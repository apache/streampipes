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
package org.apache.streampipes.model.configuration;

public class LocalAuthConfig {

  private static final long TokenExpirationTimeMillisDefault = 900000;

  private String tokenSecret;
  private long tokenExpirationTimeMillis;
  private JwtSigningMode jwtSigningMode = JwtSigningMode.HMAC;

  private String publicKey;

  public static LocalAuthConfig fromDefaults(String jwtSecret) {
    return new LocalAuthConfig(jwtSecret, TokenExpirationTimeMillisDefault);
  }

  public static LocalAuthConfig fromDefaults(JwtSigningMode signingMode,
                                             String key,
                                             long tokenExpirationTimeMillis) {
    return new LocalAuthConfig(signingMode, key, tokenExpirationTimeMillis);
  }

  public static LocalAuthConfig from(String tokenSecret,
                                     long tokenExpirationTimeMillis) {
    return new LocalAuthConfig(tokenSecret, tokenExpirationTimeMillis);
  }

  public LocalAuthConfig() {

  }

  private LocalAuthConfig(JwtSigningMode jwtSigningMode,
                          String key,
                          long tokenExpirationTimeMillis) {
    this.jwtSigningMode = jwtSigningMode;
    this.tokenExpirationTimeMillis = tokenExpirationTimeMillis;
    if (jwtSigningMode == JwtSigningMode.HMAC) {
      this.tokenSecret = key;
    } else {
      this.publicKey = key;
    }
  }

  private LocalAuthConfig(String tokenSecret,
                          long tokenExpirationTimeMillis) {
    this.tokenSecret = tokenSecret;
    this.jwtSigningMode = JwtSigningMode.HMAC;
    this.tokenExpirationTimeMillis = tokenExpirationTimeMillis;
  }

  public String getTokenSecret() {
    return tokenSecret;
  }

  public long getTokenExpirationTimeMillis() {
    return tokenExpirationTimeMillis;
  }

  public void setTokenSecret(String tokenSecret) {
    this.tokenSecret = tokenSecret;
  }

  public void setTokenExpirationTimeMillis(long tokenExpirationTimeMillis) {
    this.tokenExpirationTimeMillis = tokenExpirationTimeMillis;
  }

  public JwtSigningMode getJwtSigningMode() {
    return jwtSigningMode;
  }

  public void setJwtSigningMode(JwtSigningMode jwtSigningMode) {
    this.jwtSigningMode = jwtSigningMode;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
}
