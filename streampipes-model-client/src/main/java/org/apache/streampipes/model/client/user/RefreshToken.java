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
package org.apache.streampipes.model.client.user;

import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

public class RefreshToken implements Storable {

  @SerializedName("_id")
  private String tokenId;

  @SerializedName("_rev")
  private String rev;

  // This field should be called $type since this is the identifier used in the CouchDB view
  @SerializedName("$type")
  @JsonIgnore
  private String type = "refresh-token";

  private String principalId;

  @JsonIgnore
  private String hashedToken;

  private long createdAtMillis;
  private long expiresAtMillis;
  private Long revokedAtMillis;
  private String replacedByTokenId;
  private boolean rememberMe;

  public RefreshToken() {
  }

  public static RefreshToken create(String tokenId,
                                    String principalId,
                                    String hashedToken,
                                    long createdAtMillis,
                                    long expiresAtMillis,
                                    boolean rememberMe) {
    RefreshToken token = new RefreshToken();
    token.setTokenId(tokenId);
    token.setPrincipalId(principalId);
    token.setHashedToken(hashedToken);
    token.setCreatedAtMillis(createdAtMillis);
    token.setExpiresAtMillis(expiresAtMillis);
    token.setRememberMe(rememberMe);
    return token;
  }

  @Override
  public String getElementId() {
    return tokenId;
  }

  @Override
  public void setElementId(String elementId) {
    this.tokenId = elementId;
  }

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPrincipalId() {
    return principalId;
  }

  public void setPrincipalId(String principalId) {
    this.principalId = principalId;
  }

  public String getHashedToken() {
    return hashedToken;
  }

  public void setHashedToken(String hashedToken) {
    this.hashedToken = hashedToken;
  }

  public long getCreatedAtMillis() {
    return createdAtMillis;
  }

  public void setCreatedAtMillis(long createdAtMillis) {
    this.createdAtMillis = createdAtMillis;
  }

  public long getExpiresAtMillis() {
    return expiresAtMillis;
  }

  public void setExpiresAtMillis(long expiresAtMillis) {
    this.expiresAtMillis = expiresAtMillis;
  }

  public Long getRevokedAtMillis() {
    return revokedAtMillis;
  }

  public void setRevokedAtMillis(Long revokedAtMillis) {
    this.revokedAtMillis = revokedAtMillis;
  }

  public String getReplacedByTokenId() {
    return replacedByTokenId;
  }

  public void setReplacedByTokenId(String replacedByTokenId) {
    this.replacedByTokenId = replacedByTokenId;
  }

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }
}
