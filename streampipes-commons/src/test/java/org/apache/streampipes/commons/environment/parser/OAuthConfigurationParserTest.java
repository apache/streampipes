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

  private final Map<String, String> env = new HashMap<>() {
    {
      put("SP_OAUTH_PROVIDER_AZURE_AUTHORIZATION_URI", "authorizationUriA");
      put("SP_OAUTH_PROVIDER_AZURE_CLIENT_NAME", "clientNameA");
      put("SP_OAUTH_PROVIDER_AZURE_CLIENT_ID", "clientIdA");
      put("SP_OAUTH_PROVIDER_AZURE_CLIENT_SECRET", "clientSecretA");
      put("SP_OAUTH_PROVIDER_AZURE_FULL_NAME_ATTRIBUTE_NAME", "fullNameA");
      put("SP_OAUTH_PROVIDER_AZURE_ISSUER_URI", "issuerUriA");
      put("SP_OAUTH_PROVIDER_AZURE_JWK_SET_URI", "jwkSetUriA");
      put("SP_OAUTH_PROVIDER_AZURE_SCOPES", "scope1,scope2");
      put("SP_OAUTH_PROVIDER_AZURE_TOKEN_URI", "tokenUriA");
      put("SP_OAUTH_PROVIDER_AZURE_USER_INFO_URI", "userInfoUriA");
      put("SP_OAUTH_PROVIDER_AZURE_USER_ID_ATTRIBUTE_NAME", "userNameA");
      put("SP_OAUTH_PROVIDER_GITHUB_AUTHORIZATION_URI", "authorizationUriB");
    }
  };

  @Test
  public void testParser() {
    var config = new OAuthConfigurationParser().parse(env);

    assertEquals(2, config.size());

    var azureConfig = config.get(1);
    assertEquals("azure", azureConfig.getRegistrationId());
    assertEquals("authorizationUriA", azureConfig.getAuthorizationUri());
    assertEquals("clientNameA", azureConfig.getClientName());
    assertEquals("clientIdA", azureConfig.getClientId());
    assertEquals("clientSecretA", azureConfig.getClientSecret());
    assertEquals("fullNameA", azureConfig.getFullNameAttributeName());
    assertEquals("issuerUriA", azureConfig.getIssuerUri());
    assertEquals("jwkSetUriA", azureConfig.getJwkSetUri());
    assertEquals(2, azureConfig.getScopes().length);
    assertEquals("scope1", azureConfig.getScopes()[0]);
    assertEquals("tokenUriA", azureConfig.getTokenUri());
    assertEquals("userInfoUriA", azureConfig.getUserInfoUri());
    assertEquals("userNameA", azureConfig.getUserIdAttributeName());

    var gitHubConfig = config.get(0);
    assertEquals("github", gitHubConfig.getRegistrationId());
    assertEquals("authorizationUriB", gitHubConfig.getAuthorizationUri());
    assertNull(gitHubConfig.getTokenUri());


  }
}
