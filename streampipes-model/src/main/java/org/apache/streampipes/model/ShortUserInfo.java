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

package org.apache.streampipes.model;

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class ShortUserInfo {

  private String principalId;
  private String email;
  private String displayName;

  public static ShortUserInfo create(String principalId,
                                     String email,
                                     String displayName) {
    return new ShortUserInfo(principalId, email, displayName);
  }

  public ShortUserInfo() {
  }

  public ShortUserInfo(String principalId,
                       String email,
                       String displayName) {
    this.principalId = principalId;
    this.email = email;
    this.displayName = displayName;
  }

  public String getPrincipalId() {
    return principalId;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setPrincipalId(String principalId) {
    this.principalId = principalId;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
