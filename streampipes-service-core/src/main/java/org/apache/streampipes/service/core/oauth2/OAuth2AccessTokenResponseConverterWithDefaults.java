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

package org.apache.streampipes.service.core.oauth2;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2AccessTokenResponseConverterWithDefaults
    implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
  private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream
      .of(
          OAuth2ParameterNames.ACCESS_TOKEN,
          OAuth2ParameterNames.TOKEN_TYPE,
          OAuth2ParameterNames.EXPIRES_IN,
          OAuth2ParameterNames.REFRESH_TOKEN,
          OAuth2ParameterNames.SCOPE
      )
      .collect(Collectors.toSet());

  private final OAuth2AccessToken.TokenType defaultAccessTokenType = OAuth2AccessToken.TokenType.BEARER;

  @Override
  public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
    var accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);

    OAuth2AccessToken.TokenType accessTokenType = this.defaultAccessTokenType;
    var tokenType = OAuth2AccessToken.TokenType.BEARER.getValue();
    if (tokenType.equalsIgnoreCase((String) tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE))) {
      accessTokenType = OAuth2AccessToken.TokenType.BEARER;
    }

    long expiresIn = 0;
    if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
      try {
        expiresIn = (int) tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
      } catch (NumberFormatException ignored) {
      }
    }

    Set<String> scopes = Collections.emptySet();
    if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
      var scope = tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
      scopes = Arrays
          .stream(StringUtils.delimitedListToStringArray((String) scope, " "))
          .collect(Collectors.toSet());
    }

    Map<String, Object> additionalParameters = new LinkedHashMap<>();
    tokenResponseParameters
        .entrySet()
        .stream().filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
        .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

    return OAuth2AccessTokenResponse
        .withToken((String) accessToken)
        .tokenType(accessTokenType)
        .expiresIn(expiresIn)
        .scopes(scopes)
        .additionalParameters(additionalParameters)
        .build();
  }
}
