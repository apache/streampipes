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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.storage.api.IExtensionsServiceEndpointStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class ExtensionsServiceEndpointStorageImpl extends AbstractDao<ExtensionsServiceEndpoint>
    implements IExtensionsServiceEndpointStorage {

  public ExtensionsServiceEndpointStorageImpl() {
    super(Utils::getCouchDbRdfEndpointClient, ExtensionsServiceEndpoint.class);
  }

  @Override
  public void addExtensionsServiceEndpoint(ExtensionsServiceEndpoint extensionsServiceEndpoint) {
    persist(extensionsServiceEndpoint);
  }

  @Override
  public void removeExtensionsServiceEndpoint(String rdfEndpointId) {
    delete(rdfEndpointId);
  }

  @Override
  public List<ExtensionsServiceEndpoint> getExtensionsServiceEndpoints() {
    return findAll();
  }

}
