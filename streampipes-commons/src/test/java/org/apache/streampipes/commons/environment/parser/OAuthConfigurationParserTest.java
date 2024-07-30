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

package org.apache.streampipes.commons.environment.parser;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OAuthConfigurationParserTest {

  private final Map<String, String> env = new HashMap<>() {{
    put("SP_OAUTH_PROVIDER_A_AUTHORIZATION_URI", "authorizationUriA");
    put("SP_OAUTH_PROVIDER_A_CLIENT_NAME", "clientNameA");
    put("SP_OAUTH_PROVIDER_A_CLIENT_ID", "clientIdA");
    put("SP_OAUTH_PROVIDER_A_CLIENT_SECRET", "clientSecretA");
    put("SP_OAUTH_PROVIDER_A_FULL_NAME_ATTRIBUTE_NAME", "fullNameA");
    put("SP_OAUTH_PROVIDER_A_ISSUER_URI", "issuerUriA");
    put("SP_OAUTH_PROVIDER_A_JWK_SET_URI", "jwkSetUriA");
    put("SP_OAUTH_PROVIDER_A_SCOPES", "scope1,scope2");
    put("SP_OAUTH_PROVIDER_A_TOKEN_URI", "tokenUriA");
    put("SP_OAUTH_PROVIDER_A_USER_INFO_URI", "userInfoUriA");
    put("SP_OAUTH_PROVIDER_A_USER_ID_ATTRIBUTE_NAME", "userNameA");
    put("SP_OAUTH_PROVIDER_BA_AUTHORIZATION_URI", "authorizationUriB");
  }};

  @Test
  public void testParser() {
    var config = new OAuthConfigurationParser().parse(env);

    assertEquals(2, config.size());
    assertEquals("a", config.get(0).getRegistrationId());
    assertEquals("ba", config.get(1).getRegistrationId());
    assertEquals("authorizationUriA", config.get(0).getAuthorizationUri());
    assertEquals("authorizationUriB", config.get(1).getAuthorizationUri());
    assertEquals("clientNameA", config.get(0).getClientName());
    assertEquals("clientIdA", config.get(0).getClientId());
    assertEquals("clientSecretA", config.get(0).getClientSecret());
    assertEquals("fullNameA", config.get(0).getFullNameAttributeName());
    assertEquals("issuerUriA", config.get(0).getIssuerUri());
    assertEquals("jwkSetUriA", config.get(0).getJwkSetUri());
    assertEquals(2, config.get(0).getScopes().length);
    assertEquals("scope1", config.get(0).getScopes()[0]);
    assertEquals("scope2", config.get(0).getScopes()[1]);
    assertEquals("tokenUriA", config.get(0).getTokenUri());
    assertEquals("userInfoUriA", config.get(0).getUserInfoUri());
    assertEquals("userNameA", config.get(0).getUserIdAttributeName());
    assertNull(config.get(1).getTokenUri());
  }
}
