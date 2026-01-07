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
package org.apache.streampipes.manager.permission;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.PermissionBuilder;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class DataLakePermissionManager {

  private Permission createDataLakePermission(String measurement, String principalSid) {
    return PermissionBuilder
        .create(measurement, DataLakeMeasure.class, principalSid)
        .build();
  }

  private static IPermissionStorage getPermissionStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore()
        .getPermissionStorage();
  }

  public void makeAndPersistDataLakePermission(String measurement,
      String ownerSid) {

    Permission p = createDataLakePermission(measurement, ownerSid);
    getPermissionStorage().persist(p);

  }
}
