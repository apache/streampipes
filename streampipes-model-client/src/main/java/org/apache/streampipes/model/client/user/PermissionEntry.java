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

import java.util.Objects;

public class PermissionEntry {

  private String sid;
  private PrincipalType principalType;

  public PermissionEntry() {
  }

  public PermissionEntry(String sid, PrincipalType principalType) {
    this.sid = sid;
    this.principalType = principalType;
  }

  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public PrincipalType getPrincipalType() {
    return principalType;
  }

  public void setPrincipalType(PrincipalType principalType) {
    this.principalType = principalType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PermissionEntry that = (PermissionEntry) o;
    return sid.equals(that.sid) && principalType == that.principalType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sid, principalType);
  }
}
