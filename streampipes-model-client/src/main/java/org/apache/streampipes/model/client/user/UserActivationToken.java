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
import com.google.gson.annotations.SerializedName;

public class UserActivationToken extends AbstractMailToken {

  // This field should be called $type since this is the identifier used in the CouchDB view
  @SerializedName("$type")
  @JsonIgnore
  private String type = "user-activation";

  public UserActivationToken() {
  }

  public static UserActivationToken create(String token, String username) {
    UserActivationToken userActivationToken = new UserActivationToken();
    userActivationToken.setToken(token);
    userActivationToken.setUsername(username);

    return userActivationToken;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
