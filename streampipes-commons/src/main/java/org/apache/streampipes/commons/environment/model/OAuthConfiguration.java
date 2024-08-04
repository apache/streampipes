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

package org.apache.streampipes.commons.environment.model;

public class OAuthConfiguration {

  private String authorizationUri;
  private String clientName;
  private String clientId;
  private String clientSecret;
  private String fullNameAttributeName;
  private String issuerUri;
  private String jwkSetUri;
  private String registrationId;
  private String registrationName;
  private String[] scopes;
  private String tokenUri;
  private String userInfoUri;
  private String emailAttributeName;
  private String userIdAttributeName;


  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  public String[] getScopes() {
    return scopes;
  }

  public void setScopes(String[] scopes) {
    this.scopes = scopes;
  }

  public String getAuthorizationUri() {
    return authorizationUri;
  }

  public void setAuthorizationUri(String authorizationUri) {
    this.authorizationUri = authorizationUri;
  }

  public String getTokenUri() {
    return tokenUri;
  }

  public void setTokenUri(String tokenUri) {
    this.tokenUri = tokenUri;
  }

  public String getJwkSetUri() {
    return jwkSetUri;
  }

  public void setJwkSetUri(String jwkSetUri) {
    this.jwkSetUri = jwkSetUri;
  }

  public String getIssuerUri() {
    return issuerUri;
  }

  public void setIssuerUri(String issuerUri) {
    this.issuerUri = issuerUri;
  }

  public String getUserInfoUri() {
    return userInfoUri;
  }

  public void setUserInfoUri(String userInfoUri) {
    this.userInfoUri = userInfoUri;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getEmailAttributeName() {
    return emailAttributeName;
  }

  public void setEmailAttributeName(String emailAttributeName) {
    this.emailAttributeName = emailAttributeName;
  }

  public String getFullNameAttributeName() {
    return fullNameAttributeName;
  }

  public void setFullNameAttributeName(String fullNameAttributeName) {
    this.fullNameAttributeName = fullNameAttributeName;
  }

  public String getUserIdAttributeName() {
    return userIdAttributeName;
  }

  public void setUserIdAttributeName(String userIdAttributeName) {
    this.userIdAttributeName = userIdAttributeName;
  }

  public String getRegistrationName() {
    return registrationName;
  }

  public void setRegistrationName(String registrationName) {
    this.registrationName = registrationName;
  }
}
