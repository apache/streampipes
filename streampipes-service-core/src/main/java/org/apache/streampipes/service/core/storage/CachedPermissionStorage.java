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
package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CachedPermissionStorage
    extends AbstractCachedCrudStorage<Permission, IPermissionStorage>
    implements IPermissionStorage {

  static final String CACHE_NAME = "permissions";

  private static final String OBJECT_ID_KEY_PREFIX = "object:";
  private static final String PRINCIPALS_KEY_PREFIX = "principals:";

  public CachedPermissionStorage(IPermissionStorage delegate,
                                 CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedPermissionStorage(IPermissionStorage delegate,
                          CacheManager cacheManager,
                          ObjectMapper objectMapper) {
    super(delegate, cacheManager, CACHE_NAME, objectMapper, Permission.class);
  }

  @Override
  public Set<String> getObjectPermissions(List<String> sids) {
    var sortedSids = new ArrayList<>(sids);
    sortedSids.sort(String::compareTo);
    var serializedSids = makeKey(sortedSids);
    return getOrLoad(
        key(PRINCIPALS_KEY_PREFIX, serializedSids),
        setType(String.class),
        () -> delegate.getObjectPermissions(sids)
    );
  }

  @Override
  public List<Permission> getUserPermissionsForObject(String objectInstanceId) {
    return getOrLoad(
        key(OBJECT_ID_KEY_PREFIX, objectInstanceId),
        listType(Permission.class),
        () -> delegate.getUserPermissionsForObject(objectInstanceId)
    );
  }
}
