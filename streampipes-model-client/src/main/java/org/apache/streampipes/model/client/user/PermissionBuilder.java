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

public class PermissionBuilder {

  private final Permission permission;

  private PermissionBuilder(String objectInstanceId,
                            Class<?> objectInstanceClass,
                            String ownerSid) {
    this.permission = new Permission();
    this.permission.setObjectInstanceId(objectInstanceId);
    this.permission.setObjectClassName(objectInstanceClass.getCanonicalName());
    this.permission.setOwnerSid(ownerSid);
  }

  public static PermissionBuilder create(String objectInstanceId,
                                         Class<?> objectInstanceClass,
                                         String ownerSid) {
    return new PermissionBuilder(
        objectInstanceId,
        objectInstanceClass,
        ownerSid
    );
  }

  public PermissionBuilder with(PermissionEntry permissionEntry) {
    this.permission.addPermissionEntry(permissionEntry);

    return this;
  }

  public PermissionBuilder publicElement(boolean publicElement) {
    this.permission.setPublicElement(publicElement);

    return this;
  }

  public Permission build() {
    return permission;
  }
}
