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

import org.apache.streampipes.commons.environment.model.OAuthConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code OAuthConfigurationParser} class is responsible for parsing OAuth provider configurations
 * from environment variables and converting them into a list of {@link OAuthConfiguration} objects.
 *
 * <p>This class expects the environment variables to follow a specific naming convention:
 * {@code SP_OAUTH_{provider}_{settings}}. The parser identifies each provider by its unique
 * identifier (e.g., "github" or "azure") and maps the settings (such as "CLIENT_ID", "CLIENT_SECRET")
 * to their corresponding properties in the {@link OAuthConfiguration} object.</p>
 *
 * <p>Since environment variables cannot be structured as lists, the configuration for each provider
 * is derived from prefixed variables. For example, settings for a provider "github" could be
 * specified as:
 * <ul>
 *   <li>SP_OAUTH_GITHUB_CLIENT_ID=example-client-id</li>
 *   <li>SP_OAUTH_GITHUB_CLIENT_SECRET=example-client-secret</li>
 *   <li>...</li>
 * </ul>
 * The parser then groups these settings into a {@link OAuthConfiguration} object for "github".</p>
 */
public class OAuthConfigurationParser {

  private static final Logger LOG = LoggerFactory.getLogger(OAuthConfigurationParser.class);


  private static final String OAUTH_PREFIX = "SP_OAUTH_PROVIDER";

  public List<OAuthConfiguration> parse(Map<String, String> env) {
    Map<String, OAuthConfiguration> oAuthConfigurationsMap = new HashMap<>();


    env.forEach((key, value) -> {
      if (key.startsWith(OAUTH_PREFIX)) {
        parseEnvironmentVariable(key, value, oAuthConfigurationsMap);
      }
    });

    return new ArrayList<>(oAuthConfigurationsMap.values());
  }

  private void parseEnvironmentVariable(
      String key,
      String value,
      Map<String, OAuthConfiguration> oAuthConfigurationsMap
  ) {
    var parts = getParts(key);
    if (parts.length >= 5) {
      // containst the identifier of the provider (e.g. azure, github, ...)
      var registrationId = getRegistrationId(parts);
      var settingName = getSettingName(parts);

      var oAuthConfiguration = getOrCreateOAuthConfiguration(oAuthConfigurationsMap, registrationId);
      oAuthConfiguration.setRegistrationId(registrationId);

      switch (settingName) {
        case "AUTHORIZATION_URI" -> oAuthConfiguration.setAuthorizationUri(value);
        case "CLIENT_NAME" -> oAuthConfiguration.setClientName(value);
        case "CLIENT_ID" -> oAuthConfiguration.setClientId(value);
        case "CLIENT_SECRET" -> oAuthConfiguration.setClientSecret(value);
        case "FULL_NAME_ATTRIBUTE_NAME" -> oAuthConfiguration.setFullNameAttributeName(value);
        case "ISSUER_URI" -> oAuthConfiguration.setIssuerUri(value);
        case "JWK_SET_URI" -> oAuthConfiguration.setJwkSetUri(value);
        case "SCOPES" -> oAuthConfiguration.setScopes(value.split(","));
        case "TOKEN_URI" -> oAuthConfiguration.setTokenUri(value);
        case "USER_INFO_URI" -> oAuthConfiguration.setUserInfoUri(value);
        case "EMAIL_ATTRIBUTE_NAME" -> oAuthConfiguration.setEmailAttributeName(value);
        case "USER_ID_ATTRIBUTE_NAME" -> oAuthConfiguration.setUserIdAttributeName(value);
        case "NAME" -> oAuthConfiguration.setRegistrationName(value);
        default -> LOG.warn(
            "Unknown setting {} for oauth configuration in environment variable {}",
            settingName,
            key
        );
      }
    } else {
      LOG.warn("Invalid environment variable for oauth configuration: {}", key);
    }
  }

  private static String[] getParts(String key) {
    return key.split("_");
  }

  private static String getSettingName(String[] parts) {
    return String.join("_", Arrays.copyOfRange(parts, 4, parts.length));
  }

  private static String getRegistrationId(String[] parts) {
    return parts[3].toLowerCase();
  }

  /**
   * Retrieves an existing OAuthConfiguration for the given providerId or creates a new one if it does not exist.
   *
   * @param oAuthConfigurationsMap The map containing existing OAuthConfiguration objects.
   * @param registrationId         The identifier of the OAuth provider.
   * @return The existing or newly created OAuthConfiguration for the given providerId.
   */
  private OAuthConfiguration getOrCreateOAuthConfiguration(
      Map<String, OAuthConfiguration> oAuthConfigurationsMap,
      String registrationId
  ) {
    var oAuthConfiguration = oAuthConfigurationsMap.computeIfAbsent(registrationId, k -> new OAuthConfiguration());
    oAuthConfiguration.setRegistrationId(registrationId);
    return oAuthConfiguration;
  }
}
