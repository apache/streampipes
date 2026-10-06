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

package org.apache.streampipes.service.core.oauth2.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Stores an OAuth2 authorization request in a cookie value and restores it.
 *
 * <p>The cookie is read before the user is authenticated, so its value is untrusted. The request is
 * written as JSON and encrypted with AES-GCM. A value is only parsed after its authentication tag
 * has been verified. The key is created once per process and never leaves memory: a restart only
 * invalidates logins that are in progress.</p>
 */
public class AuthorizationRequestCookieCodec {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationRequestCookieCodec.class);

  private static final String CIPHER = "AES/GCM/NoPadding";
  private static final int IV_LENGTH = 12;
  private static final int TAG_LENGTH_BITS = 128;
  private static final byte[] ASSOCIATED_DATA = "oauth2_auth_request".getBytes(StandardCharsets.UTF_8);

  private static final SecretKey KEY = generateKey();
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Duration maxAge;
  private final Clock clock;

  public AuthorizationRequestCookieCodec(Duration maxAge) {
    this(maxAge, Clock.systemUTC());
  }

  AuthorizationRequestCookieCodec(Duration maxAge,
                                  Clock clock) {
    this.maxAge = maxAge;
    this.clock = clock;
  }

  public String encode(OAuth2AuthorizationRequest request) {
    if (!AuthorizationGrantType.AUTHORIZATION_CODE.equals(request.getGrantType())) {
      throw new IllegalArgumentException("Unsupported grant type: " + request.getGrantType().getValue());
    }
    var stored = new StoredAuthorizationRequest(
        request.getAuthorizationUri(),
        request.getClientId(),
        request.getRedirectUri(),
        request.getScopes(),
        request.getState(),
        request.getAdditionalParameters(),
        request.getAuthorizationRequestUri(),
        request.getAttributes(),
        clock.millis() + maxAge.toMillis()
    );
    try {
      return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypt(MAPPER.writeValueAsBytes(stored)));
    } catch (Exception e) {
      throw new IllegalStateException("Could not store the OAuth authorization request", e);
    }
  }

  /**
   * @return the restored request, or empty if the value was not created by this process, was changed
   *     or has expired
   */
  public Optional<OAuth2AuthorizationRequest> decode(String value) {
    StoredAuthorizationRequest stored;
    try {
      byte[] json = decrypt(Base64.getUrlDecoder().decode(value));
      stored = MAPPER.readValue(json, StoredAuthorizationRequest.class);
    } catch (Exception e) {
      LOG.debug("Ignoring an OAuth authorization request cookie that could not be verified");
      return Optional.empty();
    }
    if (clock.millis() > stored.expiresAt()) {
      LOG.debug("Ignoring an expired OAuth authorization request cookie");
      return Optional.empty();
    }
    return Optional.of(OAuth2AuthorizationRequest.authorizationCode()
        .authorizationUri(stored.authorizationUri())
        .clientId(stored.clientId())
        .redirectUri(stored.redirectUri())
        .scopes(stored.scopes())
        .state(stored.state())
        .additionalParameters(stored.additionalParameters())
        .authorizationRequestUri(stored.authorizationRequestUri())
        .attributes(stored.attributes())
        .build());
  }

  private static byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
    byte[] iv = new byte[IV_LENGTH];
    RANDOM.nextBytes(iv);
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
    cipher.updateAAD(ASSOCIATED_DATA);
    byte[] ciphertext = cipher.doFinal(plaintext);
    return ByteBuffer.allocate(iv.length + ciphertext.length).put(iv).put(ciphertext).array();
  }

  private static byte[] decrypt(byte[] value) throws GeneralSecurityException {
    if (value.length <= IV_LENGTH) {
      throw new GeneralSecurityException("Value too short");
    }
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(TAG_LENGTH_BITS, value, 0, IV_LENGTH));
    cipher.updateAAD(ASSOCIATED_DATA);
    return cipher.doFinal(value, IV_LENGTH, value.length - IV_LENGTH);
  }

  private static SecretKey generateKey() {
    try {
      KeyGenerator generator = KeyGenerator.getInstance("AES");
      generator.init(256);
      return generator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  record StoredAuthorizationRequest(String authorizationUri,
                                    String clientId,
                                    String redirectUri,
                                    Set<String> scopes,
                                    String state,
                                    Map<String, Object> additionalParameters,
                                    String authorizationRequestUri,
                                    Map<String, Object> attributes,
                                    long expiresAt) {
  }
}
