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

import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.user.management.model.UserAccountDetails;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OidcUserAccountDetails extends UserAccountDetails implements OAuth2User, OidcUser {

  private final OidcIdToken idToken;
  private final OidcUserInfo userInfo;
  private Map<String, Object> attributes;

  public OidcUserAccountDetails(UserAccount user,
                                OidcIdToken idToken,
                                OidcUserInfo userInfo) {
    super(user);
    this.idToken = idToken;
    this.userInfo = userInfo;
  }

  public static OidcUserAccountDetails create(UserAccount user,
                                              Map<String, Object> attributes,
                                              OidcIdToken idToken,
                                              OidcUserInfo userInfo) {
    OidcUserAccountDetails localUser = new OidcUserAccountDetails(user, idToken, userInfo);
    localUser.setAttributes(attributes);
    return localUser;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getName() {
    return this.details.getFullName();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public Map<String, Object> getClaims() {
    return this.attributes;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return this.userInfo;
  }

  @Override
  public OidcIdToken getIdToken() {
    return this.idToken;
  }
}
