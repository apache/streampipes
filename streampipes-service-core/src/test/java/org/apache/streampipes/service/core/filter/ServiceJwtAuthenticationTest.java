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

package org.apache.streampipes.service.core.filter;

import org.apache.streampipes.commons.constants.DefaultEnvValues;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.configuration.JwtSigningMode;
import org.apache.streampipes.model.configuration.LocalAuthConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.security.jwt.JwtTokenGenerator;
import org.apache.streampipes.security.jwt.JwtTokenValidator;
import org.apache.streampipes.security.jwt.KeyGenerator;
import org.apache.streampipes.security.jwt.PublicKeyResolver;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;
import org.apache.streampipes.user.management.jwt.SpKeyResolver;
import org.apache.streampipes.user.management.service.ServiceAccountSecretManager;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceJwtAuthenticationTest {

  private static final String USERNAME = "sp-service-client";
  private static final String SECRET = "s".repeat(64);
  private static final String SERVER_SECRET = "b".repeat(64);
  private final IUserStorage users = mock(IUserStorage.class);
  private final ISpCoreConfigurationStorage configs = mock(ISpCoreConfigurationStorage.class);
  private final LocalAuthConfig config = new LocalAuthConfig();
  private final SpKeyResolver resolver = new SpKeyResolver(SERVER_SECRET, configs, users);
  private ServiceAccount account;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    config.setJwtSigningMode(JwtSigningMode.HMAC);
    config.setTokenSecret(SERVER_SECRET);
    var core = new SpCoreConfiguration();
    core.setLocalAuthConfig(config);
    when(configs.get()).thenReturn(core);
    account = ServiceAccount.from(USERNAME, SecretEncryptionManager.encrypt(SECRET),
        Set.of(DefaultRole.ROLE_ADMIN.name()));
    account.setSecretEncrypted(true);
    when(users.getUser(USERNAME)).thenReturn(account);
    when(users.getServiceAccount(USERNAME)).thenReturn(account);
  }

  @AfterEach
  void cleanContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void serviceGeneratorSupportsAllExistingHmacKeyLengths() {
    for (int length : List.of(32, 48, 64)) {
      String secret = "s".repeat(length);
      account.setClientSecret(SecretEncryptionManager.encrypt(secret));
      assertTrue(valid(token(USERNAME, secret)));
      config.setJwtSigningMode(JwtSigningMode.RSA);
      assertTrue(valid(token(USERNAME, secret)));
      config.setJwtSigningMode(JwtSigningMode.HMAC);
    }
  }

  @Test
  void rejectsDefaultBeforeMigrationAndAcceptsReplacementAfterMigration() {
    String oldSecret = DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT;
    account.setClientSecret(SecretEncryptionManager.encrypt(oldSecret));
    String forged = token(USERNAME, oldSecret);
    assertFalse(valid(forged));
    new ServiceAccountSecretManager(users).replaceLegacyDefault(USERNAME, SECRET);
    assertFalse(valid(forged));
    assertTrue(valid(token(USERNAME, SECRET)));
  }

  @Test
  void rejectsMissingExpiredAndUnsignedTokens() {
    var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    assertFalse(valid(Jwts.builder().setSubject(USERNAME).signWith(key).compact()));
    assertFalse(valid(JwtTokenGenerator.makeJwtToken(USERNAME, SECRET, new Date(0))));
    assertFalse(valid(Jwts.builder().setSubject(USERNAME).setExpiration(future()).compact()));
    assertFalse(valid("malformed"));
  }

  @Test
  void rejectsChangedSubjectAndWrongSignature() {
    assertFalse(valid(token("unknown-service", SECRET)));
    when(users.getUser("another-service")).thenReturn(ServiceAccount.from("another-service", "a".repeat(64), Set.of()));
    assertFalse(valid(token("another-service", SECRET)));
    assertFalse(valid(token(USERNAME, "x".repeat(64))));
  }

  @Test
  void rejectsInactiveAndDeletedPrincipals() {
    String jwt = token(USERNAME, SECRET);
    account.setAccountEnabled(false);
    assertFalse(valid(jwt));
    account.setAccountEnabled(true);
    account.setAccountLocked(true);
    assertFalse(valid(jwt));
    account.setAccountLocked(false);
    account.setAccountExpired(true);
    assertFalse(valid(jwt));
    when(users.getUser(USERNAME)).thenReturn(null);
    assertFalse(valid(jwt));
  }

  @Test
  void rejectsAlgorithmChangesAndHmacUserTokensInRsaMode() {
    var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    assertFalse(valid(Jwts.builder().setSubject(USERNAME).setExpiration(future())
        .signWith(key, SignatureAlgorithm.HS256).compact()));
    var user = UserAccount.from("human", "unused", Set.of());
    when(users.getUser("human")).thenReturn(user);
    String jwt = token("human", SERVER_SECRET);
    assertTrue(valid(jwt));
    config.setJwtSigningMode(JwtSigningMode.RSA);
    assertFalse(valid(jwt));
    assertFalse(JwtTokenValidator.validateJwtToken(token(USERNAME, SECRET), new PublicKeyResolver()));
  }

  @Test
  void supportsBackendRsaTokensWithoutHmacFallback(@TempDir Path directory) throws Exception {
    var generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(3072);
    KeyPair pair = generator.generateKeyPair();
    config.setJwtSigningMode(JwtSigningMode.RSA);
    config.setPublicKey("-----BEGIN PUBLIC KEY-----\n"
        + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()) + "\n-----END PUBLIC KEY-----");
    Path privateKey = directory.resolve("private.pem");
    Files.writeString(privateKey, "-----BEGIN PRIVATE KEY-----\n"
        + Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()) + "\n-----END PRIVATE KEY-----");
    String jwt = JwtTokenGenerator.makeJwtToken(USERNAME, privateKey, Map.of(), future());
    assertTrue(valid(jwt));
    config.setJwtSigningMode(JwtSigningMode.HMAC);
    assertFalse(valid(jwt));
    assertThrows(IllegalStateException.class,
        () -> new KeyGenerator().makeKeyForSecret("RS256", SECRET, "invalid public key"));
  }

  @Test
  void filterGrantsAdminOnlyForValidServiceCredentials() throws Exception {
    authenticate("Bearer " + token(USERNAME, SECRET), "/api/v2/pipelines");
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals(USERNAME, authentication.getName());
    assertTrue(authentication.getAuthorities().stream()
        .anyMatch(a -> DefaultRole.ROLE_ADMIN.name().equals(a.getAuthority())));
    SecurityContextHolder.clearContext();
    account.setAccountEnabled(false);
    authenticate("Bearer " + token(USERNAME, SECRET), "/api/v2/pipelines");
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void prometheusRejectsPublicDefaultAndAcceptsCustomSecret() throws Exception {
    String legacy = DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT;
    account.setClientSecret(SecretEncryptionManager.encrypt(legacy));
    authenticate(basic(legacy), "/actuator/prometheus");
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    account.setClientSecret(SecretEncryptionManager.encrypt(SECRET));
    authenticate(basic(SECRET), "/actuator/prometheus");
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }

  private void authenticate(String authorization, String path) throws Exception {
    var resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);
    when(resources.manageUsers().getDb()).thenReturn(users);
    when(resources.getCoreConfigurationStorage()).thenReturn(configs);
    when(resources.getRoleStorage().getElementById(DefaultRole.ROLE_ADMIN.name()))
        .thenReturn(Role.createDefaultRole(DefaultRole.ROLE_ADMIN.name(), "Admin", List.of()));
    var request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn(authorization);
    when(request.getServletPath()).thenReturn(path);
    new TokenAuthenticationFilter(resources).doFilterInternal(request,
        mock(HttpServletResponse.class), mock(FilterChain.class));
  }

  private String basic(String secret) {
    return "Basic " + Base64.getEncoder().encodeToString((USERNAME + ":" + secret).getBytes(StandardCharsets.UTF_8));
  }

  private String token(String subject, String secret) {
    return JwtTokenGenerator.makeJwtToken(subject, secret, future());
  }

  private Date future() {
    return new Date(System.currentTimeMillis() + 60000);
  }

  private boolean valid(String jwt) {
    return JwtTokenValidator.validateJwtToken(jwt, resolver);
  }
}
