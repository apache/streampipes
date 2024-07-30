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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuthConfigurationParser {

  private static final String OAUTH_PREFIX = "SP_OAUTH_PROVIDER";

  public List<OAuthConfiguration> parse(Map<String, String> env) {
    Map<String, OAuthConfiguration> oAuthConfigurationsMap = new HashMap<>();

    for (Map.Entry<String, String> entry : env.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith(OAUTH_PREFIX)) {
        String[] parts = key.split("_");
        if (parts.length >= 5) {
          String registrationId = parts[3].toLowerCase();
          String settingName = String.join("_", Arrays.copyOfRange(parts, 4, parts.length));

          OAuthConfiguration config = oAuthConfigurationsMap.getOrDefault(registrationId, new OAuthConfiguration());
          config.setRegistrationId(registrationId);

          switch (settingName) {
            case "AUTHORIZATION_URI":
              config.setAuthorizationUri(entry.getValue());
              break;
            case "CLIENT_NAME":
              config.setClientName(entry.getValue());
              break;
            case "CLIENT_ID":
              config.setClientId(entry.getValue());
              break;
            case "CLIENT_SECRET":
              config.setClientSecret(entry.getValue());
              break;
            case "FULL_NAME_ATTRIBUTE_NAME":
              config.setFullNameAttributeName(entry.getValue());
              break;
            case "ISSUER_URI":
              config.setIssuerUri(entry.getValue());
              break;
            case "JWK_SET_URI":
              config.setJwkSetUri(entry.getValue());
              break;
            case "SCOPES":
              config.setScopes(entry.getValue().split(","));
              break;
            case "TOKEN_URI":
              config.setTokenUri(entry.getValue());
              break;
            case "USER_INFO_URI":
              config.setUserInfoUri(entry.getValue());
              break;
            case "EMAIL_ATTRIBUTE_NAME":
              config.setEmailAttributeName(entry.getValue());
              break;
            case "USER_ID_ATTRIBUTE_NAME":
              config.setUserIdAttributeName(entry.getValue());
              break;
            case "NAME":
              config.setRegistrationName(entry.getValue());
          }

          oAuthConfigurationsMap.put(registrationId, config);
        }
      }
    }

    return new ArrayList<>(oAuthConfigurationsMap.values());
  }
}
