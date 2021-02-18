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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserApiToken {

  private String tokenId;
  private String tokenName;

  @JsonIgnore
  private String hashedToken;

  public UserApiToken() {
  }

  public UserApiToken(String tokenId, String tokenName, String hashedToken) {
    this.tokenId = tokenId;
    this.tokenName = tokenName;
    this.hashedToken = hashedToken;
  }

  public String getTokenName() {
    return tokenName;
  }

  public void setTokenName(String tokenName) {
    this.tokenName = tokenName;
  }

  public String getHashedToken() {
    return hashedToken;
  }

  public void setHashedToken(String hashedToken) {
    this.hashedToken = hashedToken;
  }

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }
}
